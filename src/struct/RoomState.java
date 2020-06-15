package struct;

/**
 * This class stores state information of a room in service
 *
 * @author zyl
 * @since 9 June 2020
 */
public class RoomState {
    private int state;
    private String id;
    private String rmId;
    private float fee;
    private float feeRate;
    private long duration;
    private int fanSpeed;
    private int targetTemp;
    private float currentTemp;
    private long waitTime;

    public RoomState(int state, String id, String rmId, float fee, float feeRate, long duration, int fanSpeed, int targetTemp, float currentTemp, long waitTime) {
        this.state = state;
        this.id = id;
        this.rmId = rmId;
        this.fee = fee;
        this.feeRate = feeRate;
        this.duration = duration;
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
        this.waitTime = waitTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRmId() {
        return rmId;
    }

    public void setRmId(String rmId) {
        this.rmId = rmId;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public float getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(float feeRate) {
        this.feeRate = feeRate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(int fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public int getTargetTemp() {
        return targetTemp;
    }

    public void setTargetTemp(int targetTemp) {
        this.targetTemp = targetTemp;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
