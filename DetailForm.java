import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailForm {
	//连接oracle路径方式 "gfs"是要建立连接的数据库名 1521端口
	String url="jdbc:oracle:thin:@localhost:1521:gfs";
	String user="name";//user是数据库的用户名
	String password="xxxxx";//用户登录密码
	
	public boolean InsertRecord(int roomId, String requestTime, int requestDuration, int fanSpeed, float feeRate, float fee) {
		Connection conn=null;
		Statement st=null;
		//建立连接
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");//首先建立驱动
			conn=DriverManager.getConnection(url, user, password);//驱动成功后进行连接
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		//插入操作
		try {
			st=conn.createStatement();//获得连接
			//创建插入的sql语句
			String sql="insert into stu values(roomId, requestTime, requestDuration, fanSpeed, feeRate, fee)";
			//返回一个进行此操作的结果，要么成功，要么失败，如果返回的结果>0就是成功，反之失败
			int result=st.executeUpdate(sql);
			if(result > 0) {
				return true;//插入成功
			}
			else {
				return false;//插入失败
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public Map<String, Object> MakeInvoice(int roomId, String dateOut) {
		Map<String, Object> Invoice = new HashMap<String, Object>();
		Connection conn=null;
		Statement st=null;
		ResultSet rs=null;
		//获取连接对象
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");//首先建立驱动
			conn=DriverManager.getConnection(url, user, password);//驱动成功后进行连接
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//查询操作
		try {
			st=conn.createStatement();//创建statement类对象，用来执行SQL语句！
			//创建sql查询语句
			String sql="select RoomId,RequestTime,Fee from RDR where %dateOut% like RequestTime and RoomId=roomId";
			rs=st.executeQuery(sql);//执行sql语句并且换回一个查询的结果集
			while(rs.next()) {//循环遍历结果集，将结果保存到Map中
				Invoice.put("RoomId", rs.getString("RoomId"));
				Invoice.put("TotalFee", rs.getInt("TotalFee"));
				Invoice.put("DateIn", rs.getString("DateIn"));
				Invoice.put("Dateout", rs.getFloat("Dateout"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Invoice;
	}
	
	public List<RDR> QueryRDR(int roomId, String dateIn, String dateOut){
		List<RDR> listRDR=new ArrayList<RDR>();
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

            String sql = "SELECT * FROM RDR where id=roomId";
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
            	String RoomId = resultSet.getString(1);
            	int RequestTime = resultSet.getInt(2);
            	int RequestDuration = resultSet.getInt(3);
            	int FanSpeed = resultSet.getInt(4);
            	int FeeRate = resultSet.getInt(5);
            	int Fee = resultSet.getInt(5);
                RDR RDR= new RDR(RoomId, RequestTime, RequestDuration, FanSpeed, FeeRate, Fee);
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
	
}