import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Server {
    private String host = "http://localhost:80";
    private final String powerOnS = "/server/poweron?mode=**";
    private final String monitorS = "/server/monitor?mode=**";
    private final String queryRDR = "/server/queryRDR?rmId=**&dataIn=**&dateOut=**";
    private final String queryInvoice = "/server/queryInvoice?rmId=**&dateIn=**&dateOut=**";
    private final String queryReport = "/server/queryReport?rmId=**&dateIn=**&dateOut=**";
    private final String powerOffS = "/server/poweroff?mode=**";
    public static final String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TIME_FORMAT);
    public static final Date DATE = new Date();

    static boolean on = false;
    static int mode;

    public boolean PowerOn(int mode) {
        LinkedList<String> vals = new LinkedList<>();
        Server.mode = mode;
        Server.on = true;
        vals.add(String.valueOf(Server.mode));
        String s = craftStr(powerOnS, vals);
        JSONObject send = JSON.parseObject("{\"on\":" + on + "}");
        JSONObject j = doPost(send, s);
        return (j != null);
    }

    public JSONObject CheckRoomState() {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(mode));
        String s = craftStr(monitorS, vals);
        JSONObject send = JSON.parseObject("{\"mode\":" + mode + "}");
        return doGet(send, s);
    }

    public JSONObject QueryRDR(String rmId, String dateIn, String dateOut) {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(rmId);
        long dI = 0, dO = 0;
        try {
            dI = Server.DATE_FORMAT.parse(dateIn).getTime();
            vals.add(String.valueOf(dI));
            dO = Server.DATE_FORMAT.parse(dateOut).getTime();
            vals.add(String.valueOf(dO));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(vals);
        String s = craftStr(queryRDR, vals);
        JSONObject send = JSON.parseObject("{\"rmId\":" + rmId
                + ",\"dateIn\":" + dI
                + ",\"dateOut\":" + dO + "}");
        return doGet(send, s);
    }

    public JSONObject QueryInvoice(String rmId, String dateIn, String dateOut) {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(rmId);
        long dI = 0, dO = 0;
        try {
            dI = Server.DATE_FORMAT.parse(dateIn).getTime();
            vals.add(String.valueOf(dI));
            dO = Server.DATE_FORMAT.parse(dateOut).getTime();
            vals.add(String.valueOf(dO));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        String s = craftStr(queryInvoice, vals);
        JSONObject send = JSON.parseObject("{\"rmId\":" + rmId
                + ",\"dateIn\":" + dI
                + ",\"dateOut\":" + dO + "}");
        return doGet(send, s);
    }

    public JSONObject QueryReport(String rmId, String dateIn, String dateOut) {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(rmId);
        long dI = 0, dO = 0;
        try {
            dI = Server.DATE_FORMAT.parse(dateIn).getTime();
            vals.add(String.valueOf(dI));
            dO = Server.DATE_FORMAT.parse(dateOut).getTime();
            vals.add(String.valueOf(dO));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(dI + " " + dO);
        String s = craftStr(queryReport, vals);
        JSONObject send = JSON.parseObject("{\"rmId\":" + rmId
                + ",\"dateIn\":" + dI
                + ",\"dateOut\":" + dO + "}");
        return doGet(send, s);
    }

    public boolean PowerOff() {
        LinkedList<String> vals = new LinkedList<>();
        vals.add(String.valueOf(mode));
        String s = craftStr(powerOffS, vals);
        JSONObject send = JSON.parseObject("{\"mode\":" + mode + "}");
        JSONObject j = doPost(send, s);
        return (j != null);
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
                client.close();
                return JSONObject.parseObject(result);
            }
            client.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private JSONObject doPost(JSONObject jsonObject, String s) {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(host + s);
        StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
        post.setEntity(entity);
        post.addHeader("Content-Type", "application/json; charset=utf-8");
        post.addHeader("Accept", "text/plain;charset=utf-8");
        System.out.print("Try to POST: " + host + s + "\n");
        try {
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                client.close();
                return JSONObject.parseObject(result);
            }
            client.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());;
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
