public class RDR {
	private String RoomId;
	private int RequestTime;
	private int RequestDuration;
	private int FanSpeed;
	private float FeeRate;
	private float Fee;
	
	public RDR(String RoomId, int RequestTime, int RequestDuration,int FanSpeed, float FeeRate, float Fee) {
        super();
        this.RoomId = RoomId;
        this.RequestTime = RequestTime;
        this.RequestDuration = RequestDuration;
        this.FanSpeed = FanSpeed;
        this.FeeRate = FeeRate;
        this.Fee = Fee;
    }
    //创建每个成员变量的set和get方法
    public String getRoomId() {
        return RoomId;
    }
    public void setRoomId(String RoomId) {
        this.RoomId = RoomId;
    }
    public int getRequestTime() {
        return RequestTime;
    }
    public void setRequestTime(int RequestTime) {
        this.RequestTime = RequestTime;
    }
    public int getRequestDuration() {
        return RequestDuration;
    }
    public void RequestDuration(int RequestDuration) {
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
    public void setFee(int Fee) {
        this.Fee = Fee;
    }
    /*
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + user_name + ", age=" + age + "]";
    }*/

}
