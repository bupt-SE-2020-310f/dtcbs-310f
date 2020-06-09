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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    
    
    public boolean PrintReport(int roomId, String dateIn, String dateOut) {
    	List<Report> listReport = new ArrayList<Report>();
    	
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;

        try {
            String driverClass = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql:///mydb";
            String user = "root";
            String pass= "1234";

            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT * FROM Report where id=roomId";
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	int ReportId = resultSet.getInt(1);
            	String RoomId = resultSet.getString(2);
            	float TotalFee = resultSet.getFloat(3);
            	int NumberofRDR = resultSet.getInt(4);
            	int TimesofOnOff = resultSet.getInt(5);
            	int TimesofDispatch = resultSet.getInt(6);
            	int TimesofChangeTemp = resultSet.getInt(7);
            	int TimesofChangeFanSpeed = resultSet.getInt(8);
            	int Duration = resultSet.getInt(9);
                Report report= new Report(ReportId,RoomId,TotalFee,NumberofRDR,TimesofOnOff,TimesofDispatch,TimesofChangeTemp,TimesofChangeFanSpeed,Duration);
                listReport.add(report);
            }
            
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File com = fsv.getHomeDirectory();
            String deskPath = com.getPath();
            File file = new File( deskPath + "\\" + "310fReport.txt" );
            BufferedWriter bw = null;
            try {
            	bw = new BufferedWriter( new FileWriter(file) );
            	for(int i = 0; i < listReport.size(); i++ ) {
            		bw.write( listReport.get(i).toString() );
            		bw.newLine();
            		}
            	bw.close();
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(preparedStatement != null){
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
		return false;
    }
    
    public List<Report> QueryReport(int roomId, String dateIn, String dateOut){
    	List<Report> listReport = new ArrayList<Report>();
    	
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;

        try {
            String driverClass = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql:///mydb";
            String user = "root";
            String pass= "1234";

            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT * FROM Report where id=roomId";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	int ReportId = resultSet.getInt(1);
            	String RoomId = resultSet.getString(2);
            	float TotalFee = resultSet.getFloat(3);
            	int NumberofRDR = resultSet.getInt(4);
            	int TimesofOnOff = resultSet.getInt(5);
            	int TimesofDispatch = resultSet.getInt(6);
            	int TimesofChangeTemp = resultSet.getInt(7);
            	int TimesofChangeFanSpeed = resultSet.getInt(8);
            	int Duration = resultSet.getInt(9);
                Report report= new Report(ReportId,RoomId,TotalFee,NumberofRDR,TimesofOnOff,TimesofDispatch,TimesofChangeTemp,TimesofChangeFanSpeed,Duration);
                listReport.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(preparedStatement != null){
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listReport;
    }
    
    public boolean DeleteReport(int ReportId, String date) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                String driverClass = "com.mysql.jdbc.Driver";
                String url = "jdbc:mysql:///mydb";
                String user = "root";
                String pass= "1234";

                Class.forName(driverClass);
                connection = DriverManager.getConnection(url, user, pass);

                String sql = "DELETE FROM Report WHERE id=?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,ReportId);
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                try {
                    if(preparedStatement != null){
                        preparedStatement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    if(connection != null){
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
			return false;
    }
    
    public boolean PrintRDR(int roomId, String dateIn, String dateOut) {
    	List<RDR> listReport = new ArrayList<RDR>();
    	
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;

        try {
            String driverClass = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql:///mydb";
            String user = "root";
            String pass= "1234";

            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT * FROM Report where id=roomId";
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	String RoomId = resultSet.getString(1);
            	int  RequestTime = resultSet.getInt(2);
            	int RequestDuration = resultSet.getInt(3);
            	int FanSpeed = resultSet.getInt(4);
            	int FeeRate = resultSet.getInt(5);
            	int Fee = resultSet.getInt(6);
            	RDR listRDR= new RDR(RoomId,RequestTime,RequestDuration,FanSpeed,FeeRate,Fee);
                listReport.add(listRDR);
            }
            
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File com = fsv.getHomeDirectory();
            String deskPath = com.getPath();
            File file = new File( deskPath + "\\" + "310fRDR.txt" );
            BufferedWriter bw = null;
            try {
            	bw = new BufferedWriter( new FileWriter(file) );
            	for(int i = 0; i < listReport.size(); i++ ) {
            		bw.write( listReport.get(i).toString() );
            		bw.newLine();
            		}
            	bw.close();
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(preparedStatement != null){
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
		return false;
    }
    
    public List<RDR> QueryRDR(int roomId, String dateIn, String dateOut){
    	try {
    		List<RDR> listRDR = new ArrayList<RDR>();
    		DetailForm df = new DetailForm();
    		listRDR = df.QueryRDR(roomId, dateIn, dateOut);
    		return listRDR;
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    public boolean PrintInvoice(int roomId, String dateOut) {
    	try {
    		Map<String, Object> Invoice = new HashMap<String, Object>();
    		DetailForm df = new DetailForm();
    		Invoice = df.MakeInvoice(roomId, dateOut);
        	String line = System.getProperty("line.separator");
        	StringBuffer str = new StringBuffer();
        	FileWriter fw = new FileWriter("C:\\310fInvoice.txt", true);
        	Set<Entry<String, Object>> set = Invoice.entrySet();
        	Iterator<Entry<String, Object>> iter = set.iterator();
        	while(iter.hasNext()){
        		@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry)iter.next();
        		str.append(entry.getKey()+" : "+entry.getValue()).append(line);
        	}
        	fw.write(str.toString());
        	fw.close();
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public Map<String, Object> QueryInvoice(int roomId, String dateOut) {
    	try {
    		Map<String, Object> Invoice = new HashMap<String, Object>();
    		DetailForm df = new DetailForm();
    		Invoice = df.MakeInvoice(roomId, dateOut);
        	String line = System.getProperty("line.separator");
        	StringBuffer str = new StringBuffer();
        	FileWriter fw = new FileWriter("C:\\310fInvoice.txt", true);
        	Set<Entry<String, Object>> set = Invoice.entrySet();
        	Iterator<Entry<String, Object>> iter = set.iterator();
        	while(iter.hasNext()){
        		@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry)iter.next();
        		str.append(entry.getKey()+" : "+entry.getValue()).append(line);
        	}
        	fw.write(str.toString());
        	fw.close();
    		return Invoice;
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

    }

    static class HttpHandler implements HttpRequestHandler {

        public HttpHandler() {
            super();
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
            if (paths[1].equals("room") && paths[2] != null) {
                String[] typeAndArgs = paths[2].split("\\?");
                String type = typeAndArgs[0];
                String[] args = typeAndArgs[1].split("&");

                if (method.equals("GET")) {
                    if (type.equals("fee")) {
                        float[] values = new float[3];
                        JSONObject jsonFee = new JSONObject();
                        JSONObject jsonData = new JSONObject();
                        for (int i = 0; i < args.length; i++) {
                            //id & currentTemperature & changeTemperature
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*
                        do something here
                         */
                        jsonFee.put("status", 0);
                        jsonFee.put("msg", "成功");
                        jsonData.put("fee", 4.05);
                        jsonData.put("roomState", 0);
                        jsonData.put("id", 5);
                        jsonFee.put("data", jsonData);
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonFee));
                        System.out.println("Fee request " + " Room 5");
                    }
                } else if (method.equals("PUT")) {
                    if (type.equals("service")) {
                        float[] values = new float[1];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*
                        do sth.
                         */
                        JSONObject jsonShutdown = new JSONObject();
                        jsonShutdown.put("status", 0);
                        jsonShutdown.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonShutdown));
                        System.out.println("Shutdown " + " Room 5");
                    } else if (type.equals("exit")) {
                        float[] values = new float[1];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonExit = new JSONObject();
                        jsonExit.put("status", 0);
                        jsonExit.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonExit));
                        System.out.println("Check out" + " Room 5");
                    }
                } else {  //POST Request
                    if (type.equals("initial")) {//Check in
                        float[] values = new float[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonCheckIn = new JSONObject();
                        JSONObject jsonData = new JSONObject();
                        jsonCheckIn.put("status", 0);
                        jsonCheckIn.put("msg", "成功");
                        jsonData.put("id", 10);
                        jsonData.put("highestTemperature", 24);
                        jsonData.put("lowestTemperature", 16);
                        jsonData.put("defaultFanSpeed", 1);
                        jsonData.put("defaultTargetTemperature", 26);
                        jsonCheckIn.put("data", jsonData);
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonCheckIn));
                        System.out.println("Check in" + " Room 5");
                    } else if (type.equals("service")) {
                        float[] values = new float[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonExit = new JSONObject();
                        jsonExit.put("status", 0);
                        jsonExit.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonExit));
                        System.out.println("Shutdown " + " Room 5");
                    } else if (type.equals("temp")) {
                        float[] values = new float[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonTemp = new JSONObject();
                        jsonTemp.put("status", 0);
                        jsonTemp.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonTemp));
                        System.out.println("Adjust speed " + "room " + Float.toString(values[0]));
                    } else if (type.equals("fan")) {
                        float[] values = new float[3];
                        for (int i = 0; i < args.length; i++) {
                            values[i] = Float.parseFloat(args[1].split("=")[1]);
                        }
                        /*

                         */
                        JSONObject jsonFan = new JSONObject();
                        jsonFan.put("status", 0);
                        jsonFan.put("msg", "成功");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonFan));
                        System.out.println("Adjust speed " + "room " + Float.toString(values[0]));
                    }
                }
            }
        }
    }
}
