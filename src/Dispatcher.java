import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import struct.Invoice;
import struct.RDR;
import struct.Report;
import struct.RoomState;

import java.io.IOException;
import java.net.DatagramSocketImplFactory;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.util.Map.Entry;
import javax.swing.filechooser.FileSystemView;


/**
 * The controller layer of the system.
 *
 * This class receives requests through http form the clients and calls related
 * functions to process them.
 * This class needs priority development urgently by all authors.
 * This class will be the template of the format, every line you write in this
 * project SHOULD NOT violate the format.
 *
 *
 * @author Ziheng Ni, twist@bupt.edu.cn
 * @author FuSheng Guo
 *
 * @since 4 April 2020
 */
public class Dispatcher extends HttpServerSys {
    int defaultTargetTemp;
    HashMap<String , String> roomId2id = new HashMap<>();
    Queue sQueue, wQueue;
    Server core;
    Calendar calendar = Calendar.getInstance();

    Dispatcher(){
        super();
        core = new Server();
        sQueue = new ServeClientQueue();
        wQueue = new WaitClientQueue();
        sQueue.tother = wQueue;
        wQueue.tother = sQueue;

        calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1,0,0,0);

        Database.createTables();
    }


    static class HttpHandler implements HttpRequestHandler {
        Dispatcher ctrl = null;

        public HttpHandler(Dispatcher dispatcher) {
            super();
            ctrl = dispatcher;
        }

        public StringEntity sendBack(JSONObject js) {
            return new StringEntity(
                    js.toString(),
                    ContentType.create("APPLICATION_JSON", "UTF-8"));
        }

        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
            if (!method.equals("GET") && !method.equals("PUT") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();
            if (!target.contains("/fee") && !target.contains("/monitor")) {
                System.out.print(target + "\n");
            }
            String[] paths = target.split("/");
            response.setHeader("Access-Control-Allow-Origin","*");
            if (paths[1].equals("room") && paths[2] != null) {
                String[] typeAndArgs = paths[2].split("\\?");
                String type = typeAndArgs[0];
                String[] args = typeAndArgs[1].split("&");

                try{
                    if (method.equals("GET")) {
                        if (type.equals("fee")) { // get fee
                            String[] values = new String[3];
                            JSONObject jsonFee = new JSONObject();
                            JSONObject jsonData = new JSONObject();
                            for (int i = 0; i < args.length; i++) {
                                //id & currentTemperature & changeTemperature
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            String rmId = this.ctrl.roomId2id.get(id);
                            float currT = Float.parseFloat(values[1]);
                            float fee = 0;
                            Client client = null;
                            if (sQ().IsIn(rmId)) {
                                client = sQ().Get(rmId);
                            } else if (wQ().IsIn(rmId)) {
                                client = wQ().Get(rmId);
                            }
                            assert client != null;
                            fee = client.GetRoomState().getFee();
                            Update(rmId, client.targetTemp, currT);

                            jsonFee.put("status", 0);
                            jsonFee.put("msg", "成功");
                            jsonData.put("fee", fee);
                            jsonData.put("roomState", client.state);
                            jsonData.put("id", id);
                            jsonFee.put("data", jsonData);
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonFee));
                        }
                    }
                    else if (method.equals("PUT")) {
                        if (type.equals("service")) { // shutdown
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            String rmId = this.ctrl.roomId2id.get(id);


                            Update(rmId, 0, 0, 0);

                            JSONObject jsonShutdown = new JSONObject();
                            jsonShutdown.put("status", 0);
                            jsonShutdown.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonShutdown));
                        }
                        else if (type.equals("exit")) { // check out
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            String rmId = this.ctrl.roomId2id.get(id);

                            this.ctrl.roomId2id.remove(id);
                            if (sQ().IsIn(rmId)){
                                Client c = sQ().Pop(rmId);
                                //c.ShutDown();
                                if ((rmId = wQ().HasHighestPriority()) != null){
                                    String idS = sQ().HasLowestPriority();
                                    if (idS != null && wQ().Get(rmId).priority > sQ().Get(idS).priority){
                                        wQ().Exchange(idS, rmId);
                                    }
                                }
                            } else if (wQ().IsIn(rmId)){
                                Client c = wQ().Pop(rmId);
                                //c.ShutDown();
                            }

                            JSONObject jsonExit = new JSONObject();
                            jsonExit.put("status", 0);
                            jsonExit.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonExit));
                        }
                    }
                    else {  //POST Request
                        switch (type) {
                            case "initial": {//Check in
                                String[] values = new String[3];
                                for (int i = 0; i < args.length; i++) {
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String rmId = values[0];
                                float currT = Float.parseFloat(values[1]);
                                int id = (int) (System.currentTimeMillis() - this.ctrl.calendar.getTime().getTime());
                                Client c = new Client(rmId, String.valueOf(id), currT);
                                wQ().Add(rmId, c);
                                for (Entry<String, String> item : this.ctrl.roomId2id.entrySet()) {
                                    String key = item.getKey();
                                    String val = item.getValue();

                                    if (val.equals(rmId)) {
                                        id = Integer.parseInt(key);
                                        break;
                                    }
                                }
                                this.ctrl.roomId2id.put(String.valueOf(id), rmId);

                                JSONObject jsonCheckIn = new JSONObject();
                                JSONObject jsonData = new JSONObject();
                                jsonCheckIn.put("status", 0);
                                jsonCheckIn.put("msg", "成功");
                                jsonData.put("id", id);
                                jsonData.put("highestTemperature", Server.tempHighLimit);
                                jsonData.put("lowestTemperature", Server.tempLowLimit);
                                jsonData.put("defaultFanSpeed", Server.defaultFanSpeed);
                                jsonData.put("defaultTargetTemperature", Server.defaultTargetTemp);
                                jsonCheckIn.put("data", jsonData);
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonCheckIn));
                                break;
                            }
                            case "service": {
                                String[] values = new String[4];
                                for (int i = 0; i < args.length; i++) {
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String id = values[0];
                                String rmId = this.ctrl.roomId2id.get(id);
                                int targetT = Integer.parseInt(values[1]);
                                int targetSpd = Integer.parseInt(values[2]);
                                float currT = Float.parseFloat(values[3]);
                                Update(rmId, targetSpd, targetT, currT);

                                JSONObject jsonExit = new JSONObject();
                                jsonExit.put("status", 0);
                                jsonExit.put("msg", "成功");
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonExit));
                                break;
                            }
                            case "temp": {
                                String[] values = new String[2];
                                for (int i = 0; i < args.length; i++) {
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String id = values[0];
                                String rmId = this.ctrl.roomId2id.get(id);
                                int targetT = Integer.parseInt(values[1]);

                                int tarT = Update(rmId, targetT);

                                JSONObject jsonTemp = new JSONObject();
                                jsonTemp.put("status", 0);
                                jsonTemp.put("msg", "成功");
                                jsonTemp.put("tarT", tarT);
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonTemp));
                                break;
                            }
                            case "fan": {
                                String[] values = new String[3];
                                for (int i = 0; i < args.length; i++) {
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String id = values[0];
                                String rmId = this.ctrl.roomId2id.get(id);
                                int speed = Integer.parseInt(values[1]);
                                int temp;

                                if (wQ().IsIn(rmId)) {
                                    temp = wQ().Get(rmId).targetTemp;
                                } else {
                                    temp = sQ().Get(rmId).targetTemp;
                                }
                                Update(rmId, speed, temp);

                                JSONObject jsonFan = new JSONObject();
                                jsonFan.put("status", 0);
                                jsonFan.put("msg", "成功");
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonFan));
                                break;
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    System.err.print("Wrong uri params from client: " + request.getRequestLine() + "\n");
                }

            }
            else if (paths[1].equals("server") && paths[2] != null) {
                String[] typeAndArgs = paths[2].split("\\?");
                String type = typeAndArgs[0];
                String[] args = typeAndArgs[1].split("&");
                try {
                    if (method.equals("GET")) {
                        switch (type) {
                            case "monitor": { // monitor
                                JSONObject jsonObject = new JSONObject();
                                List<RoomState> roomStateList = sQ().CheckRoomState();

                                jsonObject.put("status", 0);
                                jsonObject.put("msg", "成功");
                                JSONArray datas = new JSONArray();
                                for (RoomState rs : roomStateList) {
                                    JSONObject jsonData = new JSONObject();
                                    jsonData.put("rmId", rs.getRmId());
                                    jsonData.put("state", rs.getState());
                                    jsonData.put("currT", rs.getCurrentTemp());
                                    jsonData.put("targetT", rs.getTargetTemp());
                                    jsonData.put("fanSpd", rs.getFanSpeed());
                                    jsonData.put("fee", rs.getFee());
                                    jsonData.put("waitTime", rs.getWaitTime());
                                    datas.add(jsonData);
                                }
                                jsonObject.put("data", datas);
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonObject));
                                break;
                            }
                            case "queryRDR": {
                                String[] values = new String[3];
                                for (int i = 0; i < args.length; i++) {
                                    //id & currentTemperature & changeTemperature
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String rmId = values[0];
                                long dateIn = Long.parseLong(values[1]);
                                long dateOut = Long.parseLong(values[2]);
                                List<RDR> res = ctrl.core.QueryRDR(rmId, dateIn, dateOut);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("status", 0);
                                jsonObject.put("msg", "成功");
                                JSONArray datas = new JSONArray();
                                for (RDR rs : res) {
                                    JSONObject jsonData = new JSONObject();
                                    jsonData.put("rmId", rs.getRoomId());
                                    jsonData.put("requestTime", rs.getRequestDuration());
                                    jsonData.put("duration", rs.getRequestDuration());
                                    jsonData.put("fanSpd", rs.getFanSpeed());
                                    jsonData.put("feeRate", rs.getFeeRate());
                                    jsonData.put("fee", rs.getFee());
                                    datas.add(jsonData);
                                }
                                jsonObject.put("data", datas);
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonObject));
                                break;
                            }
                            case "queryInvoice": {
                                System.out.println("GET");
                                String[] values = new String[3];
                                for (int i = 0; i < args.length; i++) {
                                    //id & currentTemperature & changeTemperature
                                    values[i] = (args[i].split("=")[1]);
                                }
                                String rmId = values[0];
                                long dateIn = Long.parseLong(values[1]);
                                long dateOut = Long.parseLong(values[2]);
                                Invoice invoice = ctrl.core.QueryInvoice(rmId, dateIn, dateOut);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("status", 0);
                                jsonObject.put("msg", "成功");
                                jsonObject.put("totalFee", (invoice == null) ? 0 : (invoice.getFee()));
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonObject));
                                break;
                            }
                            case "queryReport": {
                                String[] values = new String[3];
                                for (int i = 0; i < args.length; i++) {
                                    //id & currentTemperature & changeTemperature
                                    values[i] = (args[i].split("=")[1]);
                                }
                                int rmId = Integer.parseInt(values[0]);
                                long dateIn = Long.parseLong(values[1]);
                                long dateOut = Long.parseLong(values[2]);
                                Report res = ctrl.core.QueryReport(rmId, dateIn, dateOut);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("status", 0);
                                jsonObject.put("msg", "成功");
                                JSONObject jd = new JSONObject();
                                jd.put("id", res.getReportId());
                                jd.put("rmId", res.getRoomId());
                                jd.put("duration", res.getDuration());
                                jd.put("totalFee", res.getTotalFee());
                                jd.put("nRDR", res.getNumberofRDR());
                                jd.put("nOnOff", res.getTimesofOnOff());
                                jd.put("nChangeF", res.getTimesofChangeFanSpeed());
                                jsonObject.put("data", jd);
                                response.setStatusCode(HttpStatus.SC_OK);
                                response.setEntity(sendBack(jsonObject));
                                break;
                            }
                        }
                    }
                    else if (method.equals("POST")) {
                        if (type.equals("poweron")) { // poweron
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                //id & currentTemperature & changeTemperature
                                values[i] = (args[i].split("=")[1]);
                            }
                            int mode = Integer.parseInt(values[0]);
                            ctrl.core.SetPara(mode, 25, 18, 22, 1, 1, (float)0.5, (float)1/3);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("status", 0);
                            jsonObject.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonObject));
                        }
                        else if (type.equals("poweroff")) {
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                //id & currentTemperature & changeTemperature
                                values[i] = (args[i].split("=")[1]);
                            }
                            int mode = Integer.parseInt(values[0]);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("status", 0);
                            jsonObject.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonObject));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        System.err.println(e.getMessage());
                                    }
                                    System.out.println("exit");
                                    System.exit(0);
                                }
                            }).start();
                        }
                    }
                } catch (NumberFormatException e){
                    System.err.print("Wrong uri params from client: " + request.getRequestLine() + "\n");
                }
            }
        }
        private ServeClientQueue sQ(){
            return (ServeClientQueue) this.ctrl.sQueue;
        }

        private WaitClientQueue wQ(){
            return (WaitClientQueue) this.ctrl.wQueue;
        }

        // POST: temp
        private int Update(String id, int temp) {
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            if (temp >= 18 && temp <= 25) {
                c.targetTemp = temp;
            }
            if (c.state == Client.STANDBY) {
                c.priority = c.fanSpeed;
                c.state = Client.WAIT;
                c.prevStTime = System.currentTimeMillis();
            }
            return c.targetTemp;
        }

        // POST: fan
        private void Update(String id, int fanSpd, int temp){
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
                sQ().ChangeSpeed(id, fanSpd);
            } else {
                c = wQ().Get(id);
                wQ().ChangeSpeed(id, fanSpd);
            }
            if (c.state != Client.STANDBY) {
                c.priority = fanSpd;
            }
            c.targetTemp = temp;
            while (Dispatch());
        }

        // GET: fee
        private void Update(String id, int temp, float currT){
            Client c;
            if (sQ().IsIn(id)){
                if ((Server.mode == 0 && temp - currT < 1e-1 && currT - temp < 1)
                        || (Server.mode == 1 && currT - temp < 1e-1 && temp - currT < 1)) { // standby
                    sQ().Get(id).priority = Client.P_LAST;
                    sQ().Get(id).state = Client.STANDBY;
                    wQ().Add(id, sQ().Pop(id));
                }
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            c.currentTemp = currT;
            while (Dispatch());
        }

        // PUT: service && POST: service
        private void Update(String id, int fanSpd, int temp, float currT){
            if (temp != 0) { //on
                Client c = wQ().Pop(id);
                if ((Server.mode == 0 && temp - currT < 1e-1 && temp - currT > -1)
                        || (Server.mode == 1 && currT - temp < 1e-1 && temp - currT < 1)) {
                    c.priority = Client.P_LAST;
                    c.state = Client.STANDBY;
                } else {
                    c.priority = fanSpd;
                    c.state = Client.WAIT;
                }
                c.startTime = System.currentTimeMillis();
                c.currentStartTime = c.startTime;
                c.prevStTime = c.startTime;
                c.targetTemp = temp;
                c.currentTemp = currT;
                c.Enable(fanSpd, 0);
                wQ().Add(id, c);
            }
            else { // off
                Client c;
                if (sQ().IsIn(id)){
                    c = sQ().Pop(id);
                } else {
                    c = wQ().Pop(id);
                }
                c.priority = Client.P_LAST;
                c.state = Client.SHUTDOWN;
                if (c.timer != null) {
                    c.timer.TimeCancel();
                    c.timer = null;
                }
                wQ().Add(id, c);
            }
            while (Dispatch());
        }

        private boolean Dispatch(){
            if(wQ().queueLength > 0){
                String wId = wQ().HasHighestPriority();
                if (wId == null) {
                    return false;
                }
                String sId = sQ().HasLowerPriority(wQ().Get(wId).priority);
                if (sId != null){
                    sQ().Exchange(sId, wId);
                    return true;
                } else if (sQ().queueLength < ServeClientQueue.SQLEN && wQ().Get(wId).state == Client.WAIT) {
                    sQ().Add(wId, wQ().Pop(wId));
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) throws Exception {

        int port = 80;
        Dispatcher dispa = new Dispatcher();

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final HttpServer httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Server/301f")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new HttpServerSys.StdErrorExceptionLogger())
                .registerHandler("*", new Dispatcher.HttpHandler(dispa))
                .create();

        httpServer.start();
        httpServer.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                httpServer.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }
}
