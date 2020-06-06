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

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
 *
 * @since 4 April 2020
 */
public class Dispatcher extends HttpServerSys{
    int defaultTargetTemp;

    public static void main(String[] args) throws Exception {

        int port = 8080;

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Server/0.1")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new HttpServerSys.StdErrorExceptionLogger())
                .registerHandler("*", new Dispatcher.HttpHandler())
                .create();

        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }
    public void ChangeFanSpeed(String roomId, int fanSpeed){

    }

    public void ChangeTargetTemp(String roomId, int targetTemp){
    }

    static class HttpHandler implements HttpRequestHandler {

        public HttpHandler() {
            super();
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
            if (paths[1].equals("room") && paths[2] != null) {
                String[] typeAndArgs = paths[2].split("\\?");
                String type = typeAndArgs[0];
                String[] args = typeAndArgs[1].split("&");

                if (method.equals("GET")) {
                    if (type.equals("fee")){
                        float[] values = new float[3];
                        JSONObject jsonFee = new JSONObject();
                        jsonFee.put("status", 0);
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                        StringEntity entity = new StringEntity(
                                jsonFee.toString(),
                                ContentType.create("APPLICATION_JSON", "UTF-8"));
                        response.setEntity(entity);
                        System.out.println("File " + " not found");
                    }
                }
                else if (method.equals("PUT")) {
                    if (type.equals("service")){
                        float[] values = new float[1];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                    }
                    else if (type.equals("exit")){
                        float[] values = new float[1];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }


                    }
                }
                else {  //POST Request
                    if (type.equals("initial")){
                        float[] values = new float[3];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }

                    }
                    else if (type.equals("service")){
                        float[] values = new float[3];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }

                    }
                    else if (type.equals("temp")){
                        float[] values = new float[3];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }

                    }
                    else if (type.equals("fan")){
                        float[] values = new float[3];
                        for(int i = 0; i < args.length; i++){
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }

                    }
                }
            }
        }

    }
}
