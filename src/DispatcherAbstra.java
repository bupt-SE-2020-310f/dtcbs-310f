/*
Used temporarily
 */

import struct.RoomState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface DispatcherAbstra {
    public void ChangeFanSpeed(int roomId, int fanSpeed);
    public void ChangeTargetTemp(int roomId, int targetTemp);
    public List<RoomState> CheckRoomState(List<Integer> listRoomId);
    public boolean DeleteReport(int ceportId, String date);
    public boolean PowerOn();
    public boolean PrintRDR(int roomId, Time dateIn,Time dateOut);
    public boolean PrintReport(int roomId, String dateIn, String dateOut);
    public Invoice QueryInvoice(int roomId,String StringIn,String StringOut);
    public List<RDR> QueryRDR(int roomId,String StringIn,String StringOut);
    public List<Report> QueryReport(int roomId, String dateIn, String dateOut);
    float RequestFee (int roomId);
    boolean RequestOff (int roomId);
    boolean RequestOn (int roomId,float currentRoomTemp );
    public void SetPara(int mode, int tempHighLimit,
                        int tempLowLimit, int defaultTargetTemp,
                        int feeRateH, int feeRateM, int feeRateL);
    public boolean StartUp();

}
