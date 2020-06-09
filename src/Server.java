import struct.RoomState;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
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



    public List<RoomState> CheckRoomState(List<Integer> listRoomId) {
        return null;
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


    public boolean PowerOn() {
        return false;
    }


    public boolean PrintRDR(int roomId, Time dateIn, Time dateOut) {
        return false;
    }

/*    public Invoice QueryInvoice(int roomId, String StringIn, String StringOut) {
        return null;
    }*/

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
            Set<Map.Entry<String, Object>> set = Invoice.entrySet();
            Iterator<Map.Entry<String, Object>> iter = set.iterator();
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
            Set<Map.Entry<String, Object>> set = Invoice.entrySet();
            Iterator<Map.Entry<String, Object>> iter = set.iterator();
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
}
