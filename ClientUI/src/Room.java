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

public class Room {
    private String host = "http://localhost:80";
    private final String checkInS = "/room/initial?roomId=**&currentTemperature=**";
    private final String bootS = "/room/service?id=**&targetTemperature=**&fanSpeed=**&currentTemperature=**";
    private final String shutdownS = "/room/service?id=**";
    private final String cgTempS = "/room/temp?id=**&targetTemperature=**";
    private final String cgFanS = "/room/fan?id=**&fanSpeed=**";
    private final String feeS = "/room/fee?id=**&currentTemperature=**&changeTemperature=**";
    private final String checkOutS = "/room/exit?id=**";

    long roomId = 123;
    double currentTemperature = 24.0;
    double fee;
    int targetTemperature, tempH, tempL, defTemp, defFan;
    int fanSpeed, roomState;
    int id;
    double changeTemperature;

    long timeSlice = 60 * 1000; //milisec

    String timeLinePath = "/timeline.txt";

    public void checkIn(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(roomId));
        vals.add(String.valueOf(currentTemperature));
        String s = craftStr(checkInS, vals);
        JSONObject raw = doPost(vals.size(), s);
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
        JSONObject j = doPost(vals.size(), s);
    }

    public void shutdown(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        String s = craftStr(shutdownS,vals);
        JSONObject j = doPut(vals.size(), s);
    }

    public void cgTemp(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(targetTemperature));
        String s = craftStr(cgTempS,vals);
        JSONObject j = doPost(vals.size(), s);
    }

    public void cgFan(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(fanSpeed));
        String s = craftStr(cgFanS, vals);
        JSONObject j = doPost(vals.size(), s);
    }
    public void fee(){
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(id));
        vals.add(String.valueOf(currentTemperature));
        vals.add(String.valueOf(changeTemperature));
        String s = craftStr(feeS, vals);
        JSONObject raw = doGet(vals.size(), s);
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
        JSONObject j = doPut(vals.size(), s);
    }

    private JSONObject doPost(int count, String s){
        CloseableHttpClient client;
        CloseableHttpResponse response;
        try {
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(host + s);
            StringEntity entity = new StringEntity("", "UTF-8");
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

    private JSONObject doGet(int count, String s){
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

    private JSONObject doPut(int count, String s){
        CloseableHttpClient client;
        CloseableHttpResponse response;
        try {
            client = HttpClients.createDefault();
            HttpPut put = new HttpPut(host + s);
            StringEntity entity = new StringEntity("", "UTF-8");
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
