package test;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocketImplFactory;
import java.net.URLDecoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
public class Dispatcher extends HttpServerSys implements DispatcherAbstra {
    int defaultTargetTemp;
    Queue sQueue, wQueue;
    Server core;
    Dispatcher(){
        super();
        core = new Server(0, 25, 18,
                22, 1, (float) 0.5, (float) 1/3);
        sQueue = new ServeClientQueue(this, wQueue);
        wQueue = new WaitClientQueue(this, sQueue);
    }

    @Override
    public float RequestFee(int roomId) {
        return sQueue.Get(String.valueOf(roomId)).fee;
    }

    @Override
    public boolean RequestOff(int roomId) {
        return  !(sQueue.Pop(String.valueOf(roomId)) == null) ||
                !(wQueue.Pop(String.valueOf(roomId)) == null);
    }

    @Override
    public boolean RequestOn(int roomId, float currentRoomTemp) {
        return false;
    }

    @Override
    public void SetPara(int mode, int tempHighLimit, int tempLowLimit,
                        int defaultTargetTemp, int feeRateH,
                        int feeRateM, int feeRateL) {
        core.SetPara(mode, tempHighLimit, tempLowLimit,
                    defaultTargetTemp, feeRateH,
                    feeRateM, feeRateL);
    }

    @Override
    public boolean StartUp() {
        return false;
    }

    @Override
    public void ChangeFanSpeed(int roomId, int fanSpeed) {
        if(sQueue.IsIn(String.valueOf(roomId))){
            sQueue.ChangeSpeed(String.valueOf(roomId), fanSpeed);
        }
    }

    @Override
    public void ChangeTargetTemp(int roomId, int targetTemp) {
        if(sQueue.IsIn(String.valueOf(roomId))) {
            sQueue.ChangeTemp(String.valueOf(roomId), targetTemp);
        }
        else {
            wQueue.ChangeTemp(String.valueOf(roomId), targetTemp);
        }
        /*
        Improve needed
         */
    }

    @Override
    public List<RoomState> CheckRoomState(List<Integer> listRoomId) {
        return null;
    }


    @Override
    public boolean PowerOn() {
        return false;
    }

    public boolean DeleteReport(int ReportId, String date) {
    	Database db = new Database();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName(db.driverName);
            connection = DriverManager.getConnection(db.url, db.user, db.password);

            String sql = "DELETE FROM Record WHERE id=?";
            preparedStatement = connection.prepareStatement(sql);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
            	preparedStatement.close();
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
    
    public boolean PrintReport(Date date, List<String> listRoomId, String typeReport) {
    	try {
    		List<Report> listReport = new ArrayList<Report>();
    		listReport = this.QueryReport(date, listRoomId, typeReport);
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
            return false;
        }
    }
       
    public List<Report> QueryReport(Date date, List<String> listRoomId, String typeReport){
    	Database db = new Database();
    	List<Report> listReport = new ArrayList<Report>();
    	
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	
        try {
            Class.forName(db.driverName);
            connection = DriverManager.getConnection(db.url, db.user, db.password);
	    	for(int i=0; i<listRoomId.size(); i++) {
	            String roomId = listRoomId.get(i);
	            String sql = "SELECT RoomId,sum(FeeRate) as TotalFee,count(StartTime) as NumberOfRDR,"
	            		+ "count(StartTime) as TimesOfOnOff,count(StartTime) as TimesOfDisPatch,"
	            		+ "count(StartTime) as TimesOfChangeTemp,count(distinct FanSpeed) as TimesOfChangeFanSpeed"
	            		+ "sum((Termination-StartTime)sec) as Duration"
	            		+ "from Record where RoomId="+roomId;
	            preparedStatement = connection.prepareStatement(sql);
	            resultSet = preparedStatement.executeQuery();
	            while(resultSet.next()) {
	            	String RoomId = resultSet.getString(1);
	            	float TotalFee = resultSet.getFloat(2);
	            	int NumberOfRDR = resultSet.getInt(3);
	            	int TimesOfOnOff = resultSet.getInt(4);
	            	int TimesOfDisPatch = resultSet.getInt(5);
	            	int TimesOfChangeTemp = resultSet.getInt(6);
	            	int TimesOfChangeFanSpeed = resultSet.getInt(7);
	            	int Duration = resultSet.getInt(8);
	            	Report Report = new Report(RoomId,TotalFee,NumberOfRDR,TimesOfOnOff,TimesOfDisPatch,TimesOfChangeTemp,TimesOfChangeFanSpeed,Duration);
	            	listReport.add(Report);
	            	System.out.println("roomId£º"+RoomId);
	            	System.out.println("TotalFee£º"+TotalFee);
	            	System.out.println("NumberOfRDR£º"+NumberOfRDR);
	            	System.out.println("TimesOfOnOff£º"+TimesOfOnOff);
	            	System.out.println("TimesOfDisPatch£º"+TimesOfDisPatch);
	            	System.out.println("TimesOfChangeTemp£º"+TimesOfChangeTemp);
	            	System.out.println("TimesOfChangeFanSpeed£º"+TimesOfChangeFanSpeed);
	            	System.out.println("Duration£º"+Duration);
	            }
	            
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
    
    @Override
    public boolean PrintRDR(String roomId, String dateIn, String dateOut) {
    	List<RDR> listReport = new ArrayList<RDR>();
    	DetailForm RDR = new DetailForm();
    	listReport = RDR.QueryRDR(roomId, dateIn, dateOut);
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
        	return true;
        	} catch (IOException e) {
        		e.printStackTrace();
        		return false;
        	}
    }
    
    public List<RDR> QueryRDR(String roomId, String dateIn, String dateOut){
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
    
    public Invoice QueryInvoice(String roomId, String dateIn ,String dateOut){
    	DetailForm df = new DetailForm();
		return df.MakeInvoice(roomId, dateIn, dateOut);
    }

    public boolean PrintInvoice(String roomId, String dateIn ,String dateOut) {
    	List<Invoice> listInvoice = new ArrayList<Invoice>();
		DetailForm df = new DetailForm();
		listInvoice.add(df.MakeInvoice(roomId, dateIn, dateOut));
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		String deskPath = com.getPath();
		File file = new File( deskPath + "\\" + "310fInvoice.txt" );
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(file) );
			for(int i = 0; i < listInvoice.size(); i++ ) {
				bw.write( listInvoice.get(i).toString() );
				bw.newLine();
				}
			bw.close();
			return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
    }
    

    
    static class HttpHandler implements HttpRequestHandler {
        Dispatcher controller = null;
        public HttpHandler(Dispatcher dispatcher) {
            super();
            controller = dispatcher;
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
                        jsonFee.put("msg", "æˆåŠŸ");
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
                        jsonShutdown.put("msg", "æˆåŠŸ");
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
                        jsonExit.put("msg", "æˆåŠŸ");
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
                        jsonCheckIn.put("msg", "æˆåŠŸ");
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
                        jsonExit.put("msg", "æˆåŠŸ");
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
                        jsonTemp.put("msg", "æˆåŠŸ");
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
                        jsonFan.put("msg", "æˆåŠŸ");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(sendBack(jsonFan));
                        System.out.println("Adjust speed " + "room " + Float.toString(values[0]));
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        int port = 8080;
        Dispatcher server = new Dispatcher();

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final HttpServer httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("Server/0.1")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new HttpServerSys.StdErrorExceptionLogger())
                .registerHandler("*", new Dispatcher.HttpHandler())
                .create();

        httpServer.start();
        httpServer.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                httpServer.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }

}
