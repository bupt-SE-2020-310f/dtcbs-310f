package test;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import struct.Invoice;
import struct.RDR;

public class DetailForm {
	Database db = new Database();
	
	public boolean InsertRecord(String roomId, String startTime, int fanSpeed, float feeRate) {	
		Connection conn=null;
		Statement st=null;
		try {
			Class.forName(db.driverName);
			conn=DriverManager.getConnection(db.url, db.user, db.password);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		try {
			st=conn.createStatement();
			String sql="insert into Record(RoomId,StartTime,FanSpeed,FeeRate) values(roomId, startTime, fanSpeed, feeRate)";
			int result=st.executeUpdate(sql);
			if(result > 0) {
				return true;
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public Invoice MakeInvoice(String roomId, String dateIn, String dateOut){
		String RoomId;
    	String DateIn;
    	String DateOut;
    	float TotalFee;
		Connection connection = null;
	    Statement statement = null;
	    ResultSet resultSet = null;
	    try {
	        Class.forName(db.driverName).newInstance();
	        Driver driver = DriverManager.getDriver(db.url); 
	        Properties props = new Properties();
	        props.put("user", db.user);
	        props.put("password", db.password);
	        
	        connection = driver.connect(db.url, props);
	        statement = connection.createStatement();
	        //Execute the SQL statement and return the result
	        resultSet = statement.executeQuery("select RoomId,StartTime,TerminationTime,sum(FeeRate) as TotalFee "
	        		+ "from Record where RoomId="+roomId+" and StartTime>"+dateIn+" and TerminationTime<"+dateOut);
        	RoomId = resultSet.getString(1);
        	DateIn = resultSet.getString(2);
        	DateOut = resultSet.getString(3);
        	TotalFee = resultSet.getFloat(4);
        	System.out.println("RoomId："+RoomId);
        	System.out.println("RoomId："+DateIn);
        	System.out.println("DateOut："+DateOut);
        	System.out.println("DateOut："+TotalFee);
        	Invoice Invoice = new Invoice(RoomId,DateIn,DateOut,TotalFee);
        	return Invoice;
	    }
	    catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            try {
                if(resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	    
	}
	
	public List<RDR> QueryRDR(String roomId, String dateIn, String dateOut){
		List<RDR> listRDR=new ArrayList<RDR>();
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Class.forName(db.driverName);
            connection = DriverManager.getConnection(db.url, db.user, db.password);

            String sql = "SELECT * from Record where RoomId="+roomId+" and StartTime>"+dateIn+" and TerminationTime<"+dateOut;
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	String RoomId = resultSet.getString(1);
            	String RequestTime = resultSet.getString(2);
            	String TerminationTime = resultSet.getString(3);
            	int FanSpeed = resultSet.getInt(4);
            	float FeeRate = resultSet.getInt(5);
            	
                long NTime =df.parse(RequestTime).getTime();
                long OTime = df.parse(TerminationTime).getTime();
                long RequestDuration=(NTime-OTime)/1000;
            	
                RDR RDR= new RDR(RoomId, RequestTime, RequestDuration, FanSpeed, FeeRate);
                listRDR.add(RDR);
                System.out.println("roomId："+RoomId);
                System.out.println("RequestTime："+RequestTime);
                System.out.println("RequestDuration："+RequestDuration);
                System.out.println("FanSpeed："+FanSpeed);
                System.out.println("FeeRate："+FeeRate);
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

        return listRDR;
	}
	
}