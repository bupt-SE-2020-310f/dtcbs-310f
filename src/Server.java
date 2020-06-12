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
    int mode;
    int tempHighLimit;
    int tempLowLimit;
    int defaultTargetTemp;
    float feeRateH;
    float feeRateM;
    float feeRateL;

    Server(){
    }
    Server(int mode, int tempHighLimit, int tempLowLimit,
           int defaultTargetTemp, float feeRateH,
           float feeRateM,
           float feeRateL){
        this.mode = mode;
        this.tempHighLimit = tempHighLimit;
        this.tempLowLimit = tempLowLimit;
        this.defaultTargetTemp = defaultTargetTemp;
        this.feeRateH = feeRateH;
        this.feeRateM = feeRateM;
        this.feeRateL = feeRateL;
    }

    void SetPara(int mode, int tempHighLimit, int tempLowLimit,
                  int defaultTargetTemp, float feeRateH,
                  float feeRateM,
                  float feeRateL){
        this.mode = mode;
        this.tempHighLimit = tempHighLimit;
        this.tempLowLimit = tempLowLimit;
        this.defaultTargetTemp = defaultTargetTemp;
        this.feeRateH = feeRateH;
        this.feeRateM = feeRateM;
        this.feeRateL = feeRateL;
    }

    public List<RoomState> CheckRoomState(List<Integer> listRoomId) {
        return null;
    }

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