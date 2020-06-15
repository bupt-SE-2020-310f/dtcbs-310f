import struct.Invoice;
import struct.RDR;
import struct.Report;
import struct.RoomState;

import javax.swing.filechooser.FileSystemView;
import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
    static int updateDelay = 1500;

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

    public boolean PowerOn() {
        return true;
    }

    public boolean PrintReport(int roomId, long dateIn, long dateOut) {
        List<Report> listReport = new ArrayList<Report>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Database.getConnection();

            String sql = "SELECT * FROM Report where RoomId=" + roomId;
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int ReportId = resultSet.getInt(1);
                String RoomId = resultSet.getString(2);
                long Duration = resultSet.getLong(3);
                float TotalFee = resultSet.getFloat(4);
                int NumberofRDR = resultSet.getInt(5);
                int TimesofOnOff = resultSet.getInt(6);
                int TimesofChangeFanSpeed = resultSet.getInt(7);
                Report report= new Report(String.valueOf(roomId),Duration,TotalFee,NumberofRDR,TimesofOnOff,TimesofChangeFanSpeed);
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

    public Report QueryReport(int roomId, long dateIn, long dateOut){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Report report = null;

        try {
            connection = Database.getConnection();
            String sql = "SELECT sum(Fee) as TotalFee, sum(RequestDuration) as Duration From Record WHERE RoomId=" + roomId
                    + " and RequestTime>=" + dateIn
                    + " and RequestTime<" + dateOut;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            float totalFee = 0;
            long duration = 0;
            if (resultSet.next()) {
                totalFee = resultSet.getFloat(1);
                duration = resultSet.getLong(2);
            }
            sql = "SELECT count(*) as nOnOff From Record WHERE RoomId=" + roomId
                    + " and RequestTime>=" + dateIn
                    + " and RequestTime<" + dateOut
                    + " and Cate=" + 0;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            int nOnOff = 0;
            if (resultSet.next()) {
                nOnOff = resultSet.getInt(1);
            }
            sql = "SELECT count(*) as nOnOff From Record WHERE RoomId=" + roomId
                    + " and RequestTime>=" + dateIn
                    + " and RequestTime<" + dateOut
                    + " and Cate=" + 1;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            int nChangeF = 0;
            if (resultSet.next()) {
                nChangeF = resultSet.getInt(1);
            }
            int nRDR = nChangeF + nOnOff;
            report= new Report(String.valueOf(roomId),duration,totalFee,nRDR,nOnOff,nChangeF);
            sql = "insert into Report(RoomId,Duration,TotalFee,NumberofRDR,TimesofOnOff,TimesofChangeFanSpeed) values(?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, report.getRoomId());
            preparedStatement.setLong(2, report.getDuration());
            preparedStatement.setFloat(3,report.getTotalFee());
            preparedStatement.setInt(4,report.getNumberofRDR());
            preparedStatement.setInt(5, report.getTimesofOnOff());
            preparedStatement.setInt(6, report.getTimesofChangeFanSpeed());
            int res = preparedStatement.executeUpdate();
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

        return report;
    }

    public boolean PrintRDR(String roomId, long dateIn, long dateOut) {
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

    public List<RDR> QueryRDR(String roomId, long dateIn, long dateOut){
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

    public Invoice QueryInvoice(String roomId, long dateIn ,long dateOut){
    	DetailForm df = new DetailForm();
		return df.MakeInvoice(roomId, dateIn, dateOut);
    }

    public boolean PrintInvoice(String roomId, long dateIn ,long dateOut) {
		DetailForm df = new DetailForm();
		Invoice invoice = df.MakeInvoice(roomId, dateIn, dateOut);

		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		String deskPath = com.getPath();
		File file = new File( deskPath + "\\" + "310fInvoice.txt" );
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new FileWriter(file) );
			bw.write(invoice.toString());
			bw.newLine();
			bw.close();
			return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
    }
}
