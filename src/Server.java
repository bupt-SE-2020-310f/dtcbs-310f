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








/*    public Invoice QueryInvoice(int roomId, String StringIn, String StringOut) {
        return null;
    }*/




}
