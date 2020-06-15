
import struct.RoomState;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class works as a service provider for rooms,
 * these rooms are in service or waitting for service.
 *
 * @author zyl
 * @since 9 June 2020
 */
public class Client {
	int state; // 0served 1wait 2standby 3shutdown
	public static int SERVED = 0;
	public static int WAIT = 1;
	public static int STANDBY = 2;
	public static int SHUTDOWN = 3;
	String id;
	String rmId;
	int fanSpeed;
	float fee;
	float feeRate;
	long duration;
	int priority;
	public static int P_LAST = -1;
	int targetTemp;
	float currentTemp;
	float preTemp;
	long startTime;
	long currentTime;
	long currentStartTime;
	long prevStTime;
	Timer timer;

	Client(String rmId, String id, float currTmep){
		this.id = id;
		this.rmId = rmId;
		this.fee = 0;
		this.currentTemp = currTmep;
		this.targetTemp = Server.defaultTargetTemp;
		this.duration = 0;
		this.timer = null;
		this.priority = Client.P_LAST;
		this.state = Client.SHUTDOWN;
	}

	/**
	 * record power on and change fanSpeed event
	 *
	 * @param speed target fanSpd to set or change
	 * @param cate event category: 0powerOn, 1changSpd
	 */
	public void Enable(int speed, int cate) {
		currentTime = System.currentTimeMillis();
		fanSpeed = speed;
		feeRate = (float)1 / (3-speed);
		this.Record(rmId, currentTime,(currentTime-currentStartTime),speed,feeRate,fee,cate);
		currentStartTime = currentTime;
	}

	/**
	 * Get information of the room associated with client.
	 *
	 * @return this
	 */
	public RoomState GetRoomState() {
		currentTime = System.currentTimeMillis();
		duration = currentTime - startTime;
		if (timer != null) {
			timer.waitTime -= currentTime - prevStTime;
		}
		fee += feeRate * Math.abs(currentTemp - preTemp);
		preTemp = currentTemp;
		prevStTime = currentTime;
		long waitTime = 0;
		if (timer != null) {
			waitTime = timer.waitTime;
		}
		return new RoomState(state, id, rmId, fee, feeRate, duration, fanSpeed, targetTemp, currentTemp, waitTime);
	}

	public void Record(String roomId, long startTime, long duration, int fanSpeed, float feeRate, float fee, int cate) {
		DetailForm detailForm = new DetailForm();
		detailForm.InsertRecord(roomId, startTime, duration, fanSpeed, feeRate, fee, cate);
	}

}
