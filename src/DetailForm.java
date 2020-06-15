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
	public boolean InsertRecord(String roomId, long startTime, long duration, int fanSpeed, float feeRate, float fee, int cate) {
		Connection conn=null;
		PreparedStatement st = null;
		try {
			conn = Database.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Connection Successful!");
			String sql="insert into Record(RoomId,RequestTime,RequestTimeStr,RequestDuration,FanSpeed,FeeRate,Fee,Cate) values(?, ?, ?, ?, ?, ?, ?, ?)";
			st=conn.prepareStatement(sql);
			st.setString(1, roomId);
			st.setLong(2, startTime);
			Database.DATE.setTime(startTime);
			st.setString(3, Database.DATE_FORMAT.format(Database.DATE));
			st.setLong(4, duration);
			st.setInt(5, fanSpeed);
			st.setFloat(6, feeRate);
			st.setFloat(7, fee);
			st.setInt(8, cate);
			int result=st.executeUpdate();
			st.close();
			conn.close();
			if(result > 0) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public List<RDR> QueryRDR(String roomId, long dateIn, long dateOut){
		List<RDR> listRDR=new ArrayList<RDR>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = Database.getConnection();
			String sql = "SELECT * from Record where RoomId=" +roomId
					+" and RequestTime>="+dateIn
					+" and RequestTime<"+dateOut;
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				String RoomId = resultSet.getString(1);
				long RequestTime = resultSet.getLong(2);
				String RequestTimeStr = resultSet.getString(3);
				long RequestDuration = resultSet.getLong(4);
				int FanSpeed = resultSet.getInt(5);
				float FeeRate = resultSet.getFloat(6);
				float Fee = resultSet.getFloat(7);
				int Cate = resultSet.getInt(8);

				RDR RDR= new RDR(RoomId, RequestTime, RequestTimeStr, RequestDuration, FanSpeed, FeeRate, Fee, Cate);
				listRDR.add(RDR);
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

	public Invoice MakeInvoice(String roomId, long dateIn, long dateOut){
		String RoomId;
    	String DateIn;
    	String DateOut;
    	float TotalFee = 0;
		Connection connection = null;
	    Statement statement = null;
	    ResultSet resultSet = null;
	    try {
	        connection = Database.getConnection();
	        statement = connection.createStatement();
	        //Execute the SQL statement and return the result
			String sql = "SELECT sum(Fee) as TotalFee from Record where RoomId=" +roomId
					+" and RequestTIme>="+dateIn
					+" and RequestTime<"+dateOut;
			System.out.println(sql);
	        resultSet = statement.executeQuery(sql);
	        if (resultSet.next()) {
				TotalFee = resultSet.getFloat(1);
			}
        	Invoice Invoice = new Invoice(roomId,dateIn,dateOut,TotalFee);
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

}