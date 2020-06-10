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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
    HashMap<Long, Integer> roomId2id = new HashMap<>();
    Queue sQueue, wQueue;
    Server core;

    Dispatcher(){
        super();
        core = new Server(0, 25, 18,
                22, 1, (float) 0.5, (float) 1/3);
        sQueue = new ServeClientQueue();
        wQueue = new WaitClientQueue();
        sQueue.tother = wQueue;
        wQueue.tother = sQueue;
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
                        float changeT = Float.parseFloat(values[2]);
                        float fee = 0;
                        int rs = 2;
                        if (sQ().IsIn(id)){
                            Client c = sQ().Get(id);
                            //c.Update(currT, changeT);
                            fee = sQ().Get(id).fee;
                            rs = 0;
                        } else if (wQ().IsIn(id)){
                            Client c = sQ().Get(id);
                            //c.Update(currT, changeT);
                            fee = wQ().Get(id).fee;
                            rs = 1;
                        }
                        jsonFee.put("status", 0);
                        jsonFee.put("msg", "成功");
                        jsonData.put("fee", fee);
                        jsonData.put("roomState", rs);
                        jsonData.put("id", id);
                        jsonFee.put("data", jsonData);
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonFee));
                        System.out.println("Fee request " + " Room 5");
                    }
                }
                else if (method.equals("PUT")) {
                    if (type.equals("service")) {
                        String[] values = new String[1];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = (args[i].split("=")[1]);
                        }
                        String id = values[0];
                        if (sQ().IsIn(id)){
                            Client c = sQ().Pop(id);
                            c.ShutDown();
                            if ((id = wQ().HasHighestPriority()) != null){
                                String idS = sQ().HasLowestPriority();
                                if (wQ().Get(id).priority > sQ().Get(idS).priority){
                                    wQ().Exchange(idS, id);
                                }
                            }
                        } else if (wQ().IsIn(id)){
                            Client c = sQ().Pop(id);
                            c.ShutDown();
                        }
                        JSONObject jsonShutdown = new JSONObject();
                        jsonShutdown.put("status", 0);
                        jsonShutdown.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonShutdown));
                        System.out.println("Shutdown " + " Room 5");
                    } else if (type.equals("exit")) {
                        String[] values = new String[1];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = (args[i].split("=")[1]);
                        }
                        String id = values[0];
                        String rmId = "";

                        if (sQ().IsIn(id)){
                            Client c = sQ().Pop(id);
                            c.ShutDown();
                            if ((id = wQ().HasHighestPriority()) != null){
                                String idS = sQ().HasLowestPriority();
                                if (wQ().Get(id).priority > sQ().Get(idS).priority){
                                    wQ().Exchange(idS, id);
                                }
                            }
                        } else if (wQ().IsIn(id)){
                            Client c = sQ().Pop(id);
                            c.ShutDown();
                        }
                        Iterator iter = this.ctrl.roomId2id.entrySet().iterator();
                        while (iter.hasNext()){
                            Map.Entry entry = (Map.Entry) iter.next();
                            String val = (String) entry.getValue();
                            if (val.equals(id)){
                                rmId = (String) entry.getKey();
                                iter.remove();
                            }
                        }
                        JSONObject jsonExit = new JSONObject();
                        jsonExit.put("status", 0);
                        jsonExit.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonExit));
                        System.out.println("Check out " + rmId);
                    }
                }
                else {  //POST Request
                    if (type.equals("initial")) {//Check in
                        String[] values = new String[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = (args[i].split("=")[1]);
                        }
                        Long rmId = Long.parseLong(values[0]);
                        float currT = Float.parseFloat(values[1]);
                        Client c = new Client(1, (int)currT, currT);
                        int id = (int) System.currentTimeMillis();

                        this.ctrl.roomId2id.put(rmId, id);
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
                        Client c = new Client(targetSpd, targetT, )
                        JSONObject jsonExit = new JSONObject();
                        jsonExit.put("status", 0);
                        jsonExit.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonExit));
                        System.out.println("Boot" + " Room 5");
                    } else if (type.equals("temp")) {
                        String[] values = new String[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = (args[i].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonTemp = new JSONObject();
                        jsonTemp.put("status", 0);
                        jsonTemp.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonTemp));
                        System.out.println("Adjust speed " + "room " + (values[0]));
                    } else if (type.equals("fan")) {
                        String[] values = new String[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = (args[i].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonFan = new JSONObject();
                        jsonFan.put("status", 0);
                        jsonFan.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonFan));
                        System.out.println("Adjust speed " + "room " + (values[0]));
                    }
                }
            }
        }
        private Queue sQ(){
            return sQ();
        }
        
        private Queue wQ(){
            return wQ();
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
