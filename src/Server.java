import struct.Invoice;
import struct.RDR;
import struct.Report;
import struct.RoomState;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;


public class Server {
    static int mode; // 0-heat, 1-cool
    static int tempHighLimit;
    static int tempLowLimit;
    static int defaultTargetTemp;
    static int defaultFanSpeed;
    static float feeRateH;
    static float feeRateM;
    static float feeRateL;

    Server(){
    }

    void SetPara(int mode, int tempHighLimit, int tempLowLimit,
                 int defaultTargetTemp, int defaultFanSpeed, float feeRateH,
                 float feeRateM,
                 float feeRateL){
        Server.mode = mode;
        Server.tempHighLimit = tempHighLimit;
        Server.tempLowLimit = tempLowLimit;
        Server.defaultTargetTemp = defaultTargetTemp;
        Server.defaultFanSpeed = defaultFanSpeed;
        Server.feeRateH = feeRateH;
        Server.feeRateM = feeRateM;
        Server.feeRateL = feeRateL;
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

    public boolean PowerOn() {
        return true;
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
            preparedStatement.setInt(1,ReportId);
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
	                int ReportId = resultSet.getInt(1);
	            	String RoomId = resultSet.getString(2);
	            	float TotalFee = resultSet.getFloat(3);
	            	int NumberOfRDR = resultSet.getInt(4);
	            	int TimesOfOnOff = resultSet.getInt(5);
	            	int TimesOfDisPatch = resultSet.getInt(6);
	            	int TimesOfChangeTemp = resultSet.getInt(7);
	            	int TimesOfChangeFanSpeed = resultSet.getInt(8);
	            	int Duration = resultSet.getInt(9);
	            	Report Report = new Report(ReportId,RoomId,TotalFee,NumberOfRDR,TimesOfOnOff,TimesOfDisPatch,TimesOfChangeTemp,TimesOfChangeFanSpeed,Duration);
	            	listReport.add(Report);
	            	System.out.println("roomId��"+RoomId);
	            	System.out.println("TotalFee��"+TotalFee);
	            	System.out.println("NumberOfRDR��"+NumberOfRDR);
	            	System.out.println("TimesOfOnOff��"+TimesOfOnOff);
	            	System.out.println("TimesOfDisPatch��"+TimesOfDisPatch);
	            	System.out.println("TimesOfChangeTemp��"+TimesOfChangeTemp);
	            	System.out.println("TimesOfChangeFanSpeed��"+TimesOfChangeFanSpeed);
	            	System.out.println("Duration��"+Duration);
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
}
