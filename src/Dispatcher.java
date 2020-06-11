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
import struct.RoomState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        core = new Server(0, 25, 18,
                22, 1, (float) 0.5, (float) 1/3);
        sQueue = new ServeClientQueue();
        wQueue = new WaitClientQueue();
        sQueue.tother = wQueue;
        wQueue.tother = sQueue;

        calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1,0,0,0);
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
            System.out.print(target + "\n");
            String[] paths = target.split("/");
            response.setHeader("Access-Control-Allow-Origin","*");
            if (paths[1].equals("room") && paths[2] != null) {
                String[] typeAndArgs = paths[2].split("\\?");
                String type = typeAndArgs[0];
                String[] args = typeAndArgs[1].split("&");

                try{
                    if (method.equals("GET")) {
                        if (type.equals("fee")) {
                            String[] values = new String[3];
                            JSONObject jsonFee = new JSONObject();
                            JSONObject jsonData = new JSONObject();
                            for (int i = 0; i < args.length; i++) {
                                //id & currentTemperature & changeTemperature
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            float currT = Float.parseFloat(values[1]);
                            float changeT = Integer.parseInt(values[2]);
                            float fee = 0;
                            int targetT = this.ctrl.core.defaultTargetTemp;
                            int rs = 2;//0 服务，1 等待，2 待机
                            if (sQ().IsIn(id)){
                                fee = sQ().Get(id).fee;
                                targetT = sQ().Get(id).targetTemp;
                                rs = 0;
                            } else if (wQ().IsIn(id)){
                                fee = wQ().Get(id).fee;
                                targetT = sQ().Get(id).targetTemp;
                                rs = 1;
                            }
                            Update(id, targetT, currT);
                            if (sQ().IsIn(id)) { rs = 0; } else if (wQ().IsIn(id)){ rs = 1; }

                            jsonFee.put("status", 0);
                            jsonFee.put("msg", "成功");
                            jsonData.put("fee", fee);
                            jsonData.put("roomState", rs);
                            jsonData.put("id", id);
                            jsonFee.put("data", jsonData);
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonFee));
                            System.out.println("Fee request Room:" + this.ctrl.roomId2id.get(id));
                        }
                    }
                    else if (method.equals("PUT")) {
                        if (type.equals("service")) {
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];

                            Update(id, 0, 0, 0);
                            wQ().Get(id).on = false;

                            JSONObject jsonShutdown = new JSONObject();
                            jsonShutdown.put("status", 0);
                            jsonShutdown.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonShutdown));
                            System.out.println("Shutdown Room: " + this.ctrl.roomId2id.get(id));
                        } else if (type.equals("exit")) {
                            String[] values = new String[1];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            String rmId = this.ctrl.roomId2id.get(Integer.parseInt(id));

                            this.ctrl.roomId2id.remove(Integer.parseInt(id));
                            if (sQ().IsIn(id)){
                                Client c = sQ().Pop(id);
                                //c.ShutDown();
                                if ((id = wQ().HasHighestPriority()) != null){
                                    String idS = sQ().HasLowestPriority();
                                    if (wQ().Get(id).priority > sQ().Get(idS).priority){
                                        wQ().Exchange(idS, id);
                                    }
                                }
                            } else if (wQ().IsIn(id)){
                                Client c = wQ().Pop(id);
                                //c.ShutDown();
                            }

                            JSONObject jsonExit = new JSONObject();
                            jsonExit.put("status", 0);
                            jsonExit.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonExit));
                            System.out.println("Check out Room: " + rmId);
                        }
                    }
                    else {  //POST Request
                        if (type.equals("initial")) {//Check in
                            String[] values = new String[3];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String rmId = values[0];
                            float currT = Float.parseFloat(values[1]);
                            int id = (int) (System.currentTimeMillis() - this.ctrl.calendar.getTime().getTime());
                            Client c = new Client(rmId, String.valueOf(id), currT);

                            wQ().Add(String.valueOf(id), c);
                            for (Iterator<HashMap.Entry<String, String>> it = this.ctrl.roomId2id.entrySet().iterator();
                                 it.hasNext();){
                                HashMap.Entry<String, String> item = it.next();
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
                            jsonData.put("highestTemperature", this.ctrl.core.tempHighLimit);
                            jsonData.put("lowestTemperature", this.ctrl.core.tempLowLimit);
                            jsonData.put("defaultFanSpeed", 1);
                            jsonData.put("defaultTargetTemperature", this.ctrl.core.defaultTargetTemp);
                            jsonCheckIn.put("data", jsonData);
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonCheckIn));
                            System.out.println("Check in" + " Room " + values[0] + " id: " + String.valueOf(id));
                        } else if (type.equals("service")) {
                            String[] values = new String[3];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            int targetT = Integer.parseInt(values[1]);
                            int targetSpd = Integer.parseInt(values[2]);

                            Update(id, targetSpd, targetT);

                            JSONObject jsonExit = new JSONObject();
                            jsonExit.put("status", 0);
                            jsonExit.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonExit));
                            System.out.println("Boot" + " Room: " + id);
                        } else if (type.equals("temp")) {
                            String[] values = new String[2];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            int targetT = Integer.parseInt(values[1]);

                            Update(id, targetT);

                            JSONObject jsonTemp = new JSONObject();
                            jsonTemp.put("status", 0);
                            jsonTemp.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonTemp));
                            System.out.println("Adjust temperature Room: " + this.ctrl.roomId2id.get(id));
                        } else if (type.equals("fan")) {
                            String[] values = new String[3];
                            for (int i = 0; i < args.length; i++) {
                                values[i] = (args[i].split("=")[1]);
                            }
                            String id = values[0];
                            int speed = Integer.parseInt(values[1]);
                            int temp;

                            if (wQ().IsIn(id)){
                                temp = wQ().Get(id).targetTemp;
                            } else {
                                temp = sQ().Get(id).targetTemp;
                            }
                            Update(id, speed, temp);

                            JSONObject jsonFan = new JSONObject();
                            jsonFan.put("status", 0);
                            jsonFan.put("msg", "成功");
                            response.setStatusCode(HttpStatus.SC_OK);
                            response.setEntity(sendBack(jsonFan));
                            System.out.println("Adjust speed Room " + this.ctrl.roomId2id.get(id));
                        }
                    }
                    System.out.print("\n");
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

        private void Update(String id, int temp) {
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            c.targetTemp = temp;
        }

        private void Update(String id, int fanSpd, int temp){
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            c.fanSpeed = fanSpd;
            c.priority = fanSpd;
            c.targetTemp = temp;
            while (Dispatch());
        }

        private void Update(String id, int temp, float currT){
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            if ((this.ctrl.core.mode == 0 && temp - currT < 1e-1 && sQ().IsIn(c.id))
                    || (this.ctrl.core.mode == 1 && currT - temp < 1e-1 && sQ().IsIn(c.id)) ){
                c.priority = 0;
            }
            c.targetTemp = temp;
            c.currentTemp = currT;
            while (Dispatch());
        }

        private void Update(String id, int fanSpd, int temp, float currT){
            Client c;
            if (sQ().IsIn(id)){
                c = sQ().Get(id);
            } else {
                c = wQ().Get(id);
            }
            c.priority = fanSpd;
            if ((this.ctrl.core.mode == 0 && temp - currT < 1e-1 && sQ().IsIn(c.id))
                    || (this.ctrl.core.mode == 1 && currT - temp < 1e-1 && sQ().IsIn(c.id)) ){
                c.priority = 0;
            }
            c.fanSpeed = fanSpd;
            c.targetTemp = temp;
            c.currentTemp = currT;
            while (Dispatch());
        }

        private boolean Dispatch(){
            if(wQ().queueLength > 0){
                String wId = wQ().HasHighestPriority();
                String sId = sQ().HasLowerPriority(wQ().Get(wId).priority);
                if (sId != null){
                    sQ().Exchange(sId, wId);
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) throws Exception {

        int port = 8080;
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
