import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * A class for client to handles logic works and communicates with the server.
 * Using apache HTTP client.
 *
 * @author Ziheng Ni, twist@bupt.edu.cn
 * @since 12 June, 2020
 */

public class Room implements RoomConstants {
    private final String host = "http://localhost:80";
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
    int inItTemp;
    int fanSpeed;
    int roomState = SHUTDOWN;// 0 SERVED, 1 WAIT, 2 STANDBY
    int id;
    int mode; // 0 HOT, 1 COLD

    double changeTemperature;

    double tempDetectGranularity = 1e-2;

    long lastTimePoint;
    boolean recuperateMode = false;

    long timeSlice = 60 * 1000; //milisec

    String timeLinePath = "/timeline.txt";

    public void checkIn() {
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
            targetTemperature = defTemp;
            fanSpeed = j.getInteger("defaultFanSpeed");
        }
    }

    public void boot() {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(defTemp));
        vals.add(String.valueOf(defFan));
        vals.add(String.valueOf(inItTemp));
        String s = craftStr(bootS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"targetTemperature\":" + defTemp
                + ",\"fanSpeed\":" + defFan
                + ",\"currentTemperature\":" + inItTemp + "}");
        JSONObject j = doPost(send, s);
    }

    public void shutdown() {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        String s = craftStr(shutdownS,vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id + "}");
        JSONObject j = doPut(send, s);
    }

    public JSONObject cgTemp(String targetT) {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(targetT);
        String s = craftStr(cgTempS,vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"targetTemperature\":" + targetT + "}");
        return doPost(send, s);
    }

    public void cgFan() {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(fanSpeed));
        String s = craftStr(cgFanS, vals);
        JSONObject send = JSON.parseObject("{\"id\":" + id
                + ",\"fanSpeed\":" + fanSpeed + "}");
        JSONObject j = doPost(send, s);
    }

    public void fee() {
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

    public void checkOut()  {
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
                    cgTemp(String.valueOf(targetTemperature));
                }
            } else if (mode == COLD) {
                heat(0.5);
                if (currentTemperature - targetTemperature - 1 > tempDetectGranularity) {
                    recuperateMode = false;
                    cgTemp(String.valueOf(targetTemperature));
                }
            }
        } else if (roomState == SERVED) {
            double temperatureChangeRate;
            switch (fanSpeed){
                case LOW:
                    temperatureChangeRate = 0.3333333;
                    break;
                case MID:
                    temperatureChangeRate = 0.5;
                    break;
                case HIGH:
                    temperatureChangeRate = 1;
                    break;
                default:
                    temperatureChangeRate = 0.5;
            }
            if (mode == HOT) {
                heat(temperatureChangeRate);
            } else if (mode == COLD) {
                refrigerate(temperatureChangeRate);
            }
        } else if (roomState == STANDBY) {
            recuperateMode = true;
            lastTimePoint = System.currentTimeMillis();
        } else {
            lastTimePoint = System.currentTimeMillis();
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

    private JSONObject doPost(JSONObject jsonObject, String s) {
        CloseableHttpClient client = HttpClients.createDefault();;
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(host + s);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            post.setEntity(entity);
            post.addHeader("Content-Type", "application/json; charset=utf-8");
            post.addHeader("Accept", "text/plain;charset=utf-8");
            System.out.print("Try to POST: " + host + s + "\n");
            response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                client.close();
                return JSONObject.parseObject(result);
            }
            client.close();
        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            e.printStackTrace();
            System.err.print("Connection closed.\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    private JSONObject doGet(JSONObject jsonObject, String s) {
        CloseableHttpClient client = HttpClients.createDefault();;
        CloseableHttpResponse response = null;

        try {
            HttpGetWithEntity get = new HttpGetWithEntity(host + s);
            HttpEntity getE = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
            get.setEntity(getE);
            get.addHeader("Content-Type", "application/json; charset=utf-8");
            System.out.print("Try to GET: " + host + s + "\n");
            response = client.execute(get);

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                client.close();
                return JSONObject.parseObject(result);
            }
            client.close();
        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            System.err.print("Connection closed.\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    private JSONObject doPut(JSONObject jsonObject, String s) {
        CloseableHttpClient client = HttpClients.createDefault();;
        CloseableHttpResponse response = null;
        try {
            HttpPut put = new HttpPut(host + s);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            put.setEntity(entity);
            put.addHeader("Content-Type", "application/json; charset=utf-8");
            put.addHeader("Accept", "text/plain;charset=utf-8");
            System.out.print("Try to PUT: " + host + s + "\n");
            response = client.execute(put);

            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.print(result);
                System.out.print("\n");
                client.close();
                return JSONObject.parseObject(result);
            }
            client.close();
        } catch (ClientProtocolException e) {
            System.err.print("Connection failed.\n");
        } catch (SocketException e) {
            e.printStackTrace();
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
