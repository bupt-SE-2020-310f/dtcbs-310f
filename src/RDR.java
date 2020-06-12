public class RDR {
	private String RoomId;
	private String RequestTime;
	private long RequestDuration;
	private int FanSpeed;
	private float FeeRate;
	
	public RDR(String RoomId, String RequestTime, long RequestDuration,int FanSpeed, float FeeRate) {
        super();
        this.RoomId = RoomId;
        this.RequestTime = RequestTime;
        this.RequestDuration = RequestDuration;
        this.FanSpeed = FanSpeed;
        this.FeeRate = FeeRate;
    }
    //����ÿ����Ա������set��get����
    public String getRoomId() {
        return RoomId;
    }
    public void setRoomId(String RoomId) {
        this.RoomId = RoomId;
    }
    public String getRequestTime() {
        return RequestTime;
    }
    public void setRequestTime(String RequestTime) {
        this.RequestTime = RequestTime;
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

}
