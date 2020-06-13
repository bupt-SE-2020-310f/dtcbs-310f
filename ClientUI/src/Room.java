import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

public class Room implements RoomConstant{
    private String host = "http://localhost:8080";
    private final String checkInS = "/room/initial?roomId=**&currentTemperature=**";
    private final String bootS = "/room/service?id=**&targetTemperature=**&fanSpeed=**&currentTemperature=**";
    private final String shutdownS = "/room/service?id=**";
    private final String cgTempS = "/room/temp?id=**&targetTemperature=**";
    private final String cgFanS = "/room/fan?id=**&fanSpeed=**";
    private final String feeS = "/room/fee?id=**&currentTemperature=**&changeTemperature=**";
    private final String checkOutS = "/room/exit?id=**";

    //Room info
    long roomId = 123;
    double currentTemperature = 24.0;
    double fee;
    int targetTemperature, tempH, tempL, defTemp, defFan;
    int fanSpeed;
    int roomState, id;
    int mode; // 0 HOT, 1 COLD

    double changeTemperature;

    double tempDetectGranularity = 1e-2;

    long lastTimePoint;
    boolean recuperateMode = false;

    long timeSlice = 60 * 1000; //milisec

    String timeLinePath = "/timeline.txt";

    public void checkIn(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(roomId));
        vals.add(String.valueOf(currentTemperature));
        String s = craftStr(checkInS, vals);
        JSONObject send = JSON.parseObject("{\"roomId\":" + roomId
                + ",\"currentTemperature\":" + currentTemperature + "}");
        JSONObject raw = doPost(send, s);
        if (raw != null) {
            JSONObject j = raw.getJSONObject("data");
            id = j.getInteger("id");
            tempH = j.getInteger("highestTemperature");
            tempL = j.getInteger("lowestTemperature");
            defFan = j.getInteger("defaultFanSpeed");
            defTemp = j.getInteger("defaultTargetTemperature");
        }
    }

    public void boot(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(targetTemperature));
        vals.add(String.valueOf(fanSpeed));
        vals.add(String.valueOf(currentTemperature));
        String s = craftStr(bootS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"targetTemperature\":" + targetTemperature
                + ",\"fanSpeed\":" + fanSpeed
                + ",\"currentTemperature\":" + currentTemperature + "}");
        JSONObject j = doPost(send, s);
    }

    public void shutdown(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        String s = craftStr(shutdownS,vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id + "}");
        JSONObject j = doPut(send, s);
    }

    public void cgTemp(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(targetTemperature));
        String s = craftStr(cgTempS,vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"targetTemperature\":" + targetTemperature + "}");
        JSONObject j = doPost(send, s);
    }

    public void cgFan(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(fanSpeed));
        String s = craftStr(cgFanS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"fanSpeed\":" + fanSpeed + "}");
        JSONObject j = doPost(send, s);
    }

    public void fee(){
        LinkedList<String> vals = new LinkedList<>();
        ifRecuperate();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(currentTemperature));
        vals.add(String.valueOf(changeTemperature));
        String s = craftStr(feeS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"currentTemperature\":" + currentTemperature
                + ",\"changeTemperature\":" + changeTemperature + "}");
        changeTemperature = 0;
        JSONObject raw = doGet(send, s);
        if (raw != null) {
            JSONObject j = raw.getJSONObject("data");
            fee = j.getDouble("fee");
            id = j.getInteger("id");
            roomState = j.getInteger("roomState");
        }

    }
    public void checkOut(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        String s = craftStr(checkOutS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id + "}");
        JSONObject j = doPut(send, s);
    }

    private void ifRecuperate(){
        if (recuperateMode){
            if (mode == HOT) {
                refrigerate(0.5);
                if (targetTemperature - currentTemperature - 1 > tempDetectGranularity) {
                    recuperateMode = false;
                }
            } else if (mode == COLD) {
                heat(0.5);
                if (currentTemperature - targetTemperature - 1 > tempDetectGranularity) {
                    recuperateMode = false;
                }
            }
        } else {
            double temperatureChangeRate;
            switch (fanSpeed){
                case LOW:
                    temperatureChangeRate = 0.3333;
                    break;
                case MID:
                    temperatureChangeRate = 0.5;
                    break;
                case HIGH:
                    temperatureChangeRate = 1;
                    break;
                default:
                    temperatureChangeRate = 0;
            }
            if (mode == HOT) {
                heat(temperatureChangeRate);
            } else if (mode == COLD) {
                refrigerate(temperatureChangeRate);
            }
        }
    }

    private void heat(double tempRate) {
        long currTime = System.currentTimeMillis();
        long delta = currTime - lastTimePoint;
        lastTimePoint = currTime;
        changeTemperature = ((double) delta) / 60 / 1000 * tempRate;
        currentTemperature += changeTemperature;
    }

    private void refrigerate(double tempRate) {
        long currTime = System.currentTimeMillis();
        long delta = currTime - lastTimePoint;
        lastTimePoint = currTime;
        changeTemperature = - ((double) delta) / 60 / 1000 * tempRate;
        currentTemperature += changeTemperature;
    }

    private JSONObject doPost(JSONObject jsonObject, String s){
        CloseableHttpClient client;
        CloseableHttpResponse response;
        try {
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(host + s);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            post.setEntity(entity);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            post.addHeader("Accept", "text/plain;charset=utf-8");
            System.out.print("Try to POST: " + host + s + "\n");
            response = client.execute(post);
            client.close();

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                return JSONObject.parseObject(result);
            }

        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            System.err.print("Connection closed.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject doGet(JSONObject jsonObject, String s){
        CloseableHttpClient client;
        CloseableHttpResponse response;
        try {
            client = HttpClients.createDefault();
            HttpGet get = new HttpGet(host + s);
            get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            get.addHeader("Accept", "text/plain;charset=utf-8");
            System.out.print("Try to GET: " + host + s + "\n");
            response = client.execute(get);
            client.close();

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                return JSONObject.parseObject(result);
            }

        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            System.err.print("Connection closed.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject doPut(JSONObject jsonObject, String s){
        CloseableHttpClient client;
        CloseableHttpResponse response;
        try {
            client = HttpClients.createDefault();
            HttpPut put = new HttpPut(host + s);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            put.setEntity(entity);
            put.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            put.addHeader("Accept", "text/plain;charset=utf-8");
            System.out.print("Try to PUT: " + host + s + "\n");
            response = client.execute(put);
            client.close();

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                return JSONObject.parseObject(result);
            }

        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            System.err.print("Connection closed.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String craftStr(String s, LinkedList<String> vals){
        for (String v: vals){
            s = s.replaceFirst("\\*\\*", v);
        }
        return s;
    }

}
