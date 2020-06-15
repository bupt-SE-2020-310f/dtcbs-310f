package struct;

public class RDR {
    private String RoomId;
    private long RequestTime;
    private String RequestTimeStr;
    private long RequestDuration;
    private int FanSpeed;
    private float FeeRate;
    private float Fee;
    private int Cate;

    public RDR(String RoomId, long RequestTime, String RequestTimeStr, long RequestDuration, int FanSpeed, float FeeRate, float Fee, int cate) {
        super();
        this.RoomId = RoomId;
        this.RequestTime = RequestTime;
        this.RequestTimeStr = RequestTimeStr;
        this.RequestDuration = RequestDuration;
        this.FanSpeed = FanSpeed;
        this.FeeRate = FeeRate;
        this.Fee = Fee;
        this.Cate = cate;
    }
    //创建每个成员变量的set和get方法
    public String getRoomId() {
        return RoomId;
    }
    public void setRoomId(String RoomId) {
        this.RoomId = RoomId;
    }
    public long getRequestTime() {
        return RequestTime;
    }
    public void setRequestTime(long RequestTime) {
        this.RequestTime = RequestTime;
    }
    public String getRequestTimeStr() {
        return RequestTimeStr;
    }
    public void setRequestTimeStr(String requestTimeStr) {
        RequestTimeStr = requestTimeStr;
    }
    public long getRequestDuration() {
        return RequestDuration;
    }
    public void RequestDuration(long RequestDuration) {
        this.RequestDuration = RequestDuration;
    }
    public int getFanSpeed() {
        return FanSpeed;
    }
    public void setFanSpeed(int FanSpeed) {
        this.FanSpeed = FanSpeed;
    }
    public float getFeeRate() {
        return FeeRate;
    }
    public void setFeeRate(int FeeRate) {
        this.FeeRate = FeeRate;
    }
    public float getFee() {
        return Fee;
    }
    public void setFee(float fee) {
        Fee = fee;
    }
    public int getCate() {
        return Cate;
    }
    public void setCate(int cate) {
        Cate = cate;
    }
}