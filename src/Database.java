import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Database {
	public static String driverName;
	public static String url;
	public static String user;
	public static String password;
	public static Connection conn;
	public static PreparedStatement pst;
	public static ResultSet rs;
	public static Statement st;
	public static final String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Database.TIME_FORMAT);
	public static final Date DATE = new Date();

	public static Connection getConnection() {
		Properties properties = new Properties();
		try {
//			properties.load(Database.class.getResourceAsStream("database.properties"));
			properties.load(new FileInputStream("conf/database.properties"));
			Database.driverName = properties.getProperty("DRIVERNAME");
			Database.url = properties.getProperty("URL");
			Database.user = properties.getProperty("USER");
			Database.password = properties.getProperty("PASSWORD");
			Class.forName(Database.driverName);
			conn = DriverManager.getConnection(
					Database.url,
					Database.user,
					Database.password
			);
		} catch (IOException | ClassNotFoundException | SQLException e) {
			System.err.println(e.getMessage());
		}

		return conn;
	}

	public static boolean createTables() {
		conn = Database.getConnection();
		String sql1 = "DROP TABLE IF EXISTS Record;";
		String sql2 = "CREATE TABLE Record ("
				+ "RoomId VARCHAR(255) NOT NULL,"
				+ "RequestTime BIGINT,"
				+ "RequestTimeStr VARCHAR(255),"
				+ "RequestDuration BIGINT,"
				+ "FanSpeed INT,"
				+ "FeeRate FLOAT,"
				+ "Fee FLOAT,"
				+ "Cate INT);";
		String sql3 = "DROP TABLE IF EXISTS Report;";
		String sql4 = "CREATE TABLE IF NOT EXISTS Report ("
				+ "ReportId INT PRIMARY KEY AUTO_INCREMENT,"
				+ "RoomId VARCHAR(255) NOT NULL,"
				+ "Duration BIGINT,"
				+ "TotalFee FLOAT,"
				+ "NumberofRDR INT,"
				+ "TimesofOnOff INT,"
				+ "TimesofChangeFanSpeed INT);";
		try {
			st = conn.createStatement();
			if(st.executeUpdate(sql1) >= 0 && st.executeUpdate(sql2) >= 0) {
				System.out.println("Table Record created.");
			}
			if (st.executeUpdate(sql3) >= 0 && st.executeUpdate(sql4) >= 0) {
				System.out.println("Table Report created.");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return true;
	}
}
