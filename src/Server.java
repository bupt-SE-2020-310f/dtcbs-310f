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

}
