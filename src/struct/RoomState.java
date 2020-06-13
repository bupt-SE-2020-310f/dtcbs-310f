package struct;

/**
 * This class stores state information of a room in service
 *
 * @author zyl
 * @since 9 June 2020
 */
public class RoomState {
    private boolean on;
    private String id;
    private String rmId;
    private float fee;
    private float feeRate;
    private int duration;
    private int fanSpeed;
    private int targetTemp;
    private float currentTemp;

    public RoomState(boolean on, String id, String rmId, float fee, float feeRate, int duration, int fanSpeed, int targetTemp, float currentTemp) {
        this.on = on;
        this.id = id;
        this.rmId = rmId;
        this.fee = fee;
        this.feeRate = feeRate;
        this.duration = duration;
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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
}
