
import struct.RoomState;

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
	boolean on;
	String id;
	String rmId;
	int fanSpeed;
	float fee;
	float feeRate;
	long duration;
	int priority;
	int targetTemp;
	float currentTemp;
	float preTemp;
	long startTime;
	long currentTime;
	long preTime;
	DetailForm detailForm;
	Timer timer;

	Client() {
		this.fanSpeed = Server.defaultFanSpeed;
		this.feeRate = (float)1 / this.fanSpeed;
		this.fee = 0;
		this.priority = this.fanSpeed;
		this.targetTemp = Server.defaultTargetTemp;
		this.duration = 0;
		this.startTime = System.currentTimeMillis();
		this.currentTime = startTime;
		this.timer = null;
	}

	Client(String rmId, String id, float currTmep){
		this.fanSpeed = Server.defaultFanSpeed;
		this.feeRate = (float)1 / this.fanSpeed;
		this.id = id;
		this.rmId = rmId;
		this.fee = 0;
		this.priority = this.fanSpeed;
		this.currentTemp = currTmep;
		this.preTemp = currTmep;
		this.targetTemp = Server.defaultTargetTemp;
		this.duration = 0;
		this.startTime = System.currentTimeMillis();
		this.currentTime = startTime;
		this.timer = null;
	}

	public void Enable(int mode, int speed) {
		Date date = new Date();
		SimpleDateFormat startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		fanSpeed = speed;
		feeRate = (float)1 / (3-speed);
		//TODO
//		this.Record(rmId,startTime.format(date),speed,feeRate);
	}

	/**
	 * Get information of the room associated with client.
	 *
	 * @return this
	 */
	public RoomState GetRoomState() {
		preTime = currentTime;
		currentTime = System.currentTimeMillis();
		duration = currentTime - startTime;
		if (timer != null) {
			timer.waitTime -= currentTime - preTime;
		}
		fee += feeRate * Math.abs(currentTemp - preTemp);
		preTemp = currentTemp;
		return new RoomState(on, id, rmId, fee, feeRate, duration, fanSpeed, targetTemp, currentTemp);
	}

	public void Record(String roomId, String startTime, int fanSpeed, float feeRate) {
		DetailForm detailForm = new DetailForm();
		detailForm.InsertRecord(roomId, startTime, fanSpeed, feeRate);
	}

}
