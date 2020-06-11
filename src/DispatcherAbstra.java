package test;
/*
Used temporarily
 */

import struct.Invoice;
import struct.RDR;
import struct.Report;
import struct.RoomState;

import java.sql.Date;
//import java.sql.*;
//import java.util.ArrayList;
import java.util.List;

public interface DispatcherAbstra {
    public void ChangeFanSpeed(int roomId, int fanSpeed);
    public void ChangeTargetTemp(int roomId, int targetTemp);
    public List<RoomState> CheckRoomState(List<Integer> listRoomId);
    public boolean PowerOn();
    
    public boolean DeleteReport(int ceportId, String date);
    public boolean PrintReport(Date date, List<String> listRoomId, String typeReport);
    public List<Report> QueryReport(Date date, List<String> listRoomId, String typeReport);
    public Invoice QueryInvoice(String roomId,String StringIn,String StringOut);
    public boolean PrintInvoice(String roomId, String dateIn ,String dateOut);
    public List<RDR> QueryRDR(String roomId,String StringIn,String StringOut);
    public boolean PrintRDR(String roomId, String dateIn, String dateOut);
    
    float RequestFee (int roomId);
    boolean RequestOff (int roomId);
    boolean RequestOn (int roomId,float currentRoomTemp );
    public void SetPara(int mode, int tempHighLimit,
                        int tempLowLimit, int defaultTargetTemp,
                        int feeRateH, int feeRateM, int feeRateL);
    public boolean StartUp();

}
