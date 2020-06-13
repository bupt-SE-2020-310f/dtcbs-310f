
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
	float fee;
	float feeRate;
	int duration;
	int priority;
	int fanSpeed;
	int targetTemp;
	float currentTemp;
	float preTemp;
	long startTime;
	long currentTime;
	long preTime;
	int state; // 0-serve, 1-wait, 2-standby
	DetailForm detailForm;
	Timer timer;

	Client() {

	}

	Client(String rmId, String id, float currTmep){
		this.id = id;
		this.rmId = rmId;
		this.fee = 0;
		this.priority = 0;
		this.currentTemp = currTmep;
		this.duration = 0;
		this.startTime = System.currentTimeMillis();
		this.currentTime = startTime;
		this.timer = null;
	}
  
  Client(String rmId, String id, float currTmep, int fanSpeed, int targetTemp){
		Systemut.println("!!!!!!!!!!!!!!!!!!");
		this.rmId = rmId;
		this.id = id;
		this.currentTemp = currTmep;
		this.priority = 0;
		this.fee = 0;
		this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        System.out.println(this.rmId);
        System.out.println(this.id);
        System.out.println(this.currentTemp);
        System.out.println(this.fanSpeed);
        System.out.println(this.targetTemp);
        //this.currentTemp = currentTemp;
	}

	public void Enable(int mode, int speed) {
		Date date = new Date();
		SimpleDateFormat startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		float changeTemp;
		float needMinute;
		float electricQuantity;
		if(mode == 1) {//????
			changeTemp = this.targetTemp - this.currentTemp;
			needMinute = (float) (changeTemp / 0.5);
			if(speed == 3) {//??
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if(speed == 2) {//??
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			if(speed == 1) {//??
				electricQuantity = needMinute / 3;
				feeRate = electricQuantity * 1;
			}
		}
		else if(mode == 0) {//????
			changeTemp = this.currentTemp - this.targetTemp;
			needMinute = (float) (changeTemp / 0.5);
			if(speed == 3) {//??
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if(speed == 2) {//??
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			if(speed == 1) {//??
				electricQuantity = needMinute / 3;
				feeRate = electricQuantity * 1;
			}
		}
		fanSpeed = speed;
		this.Record(rmId,startTime.format(date),speed,feeRate);
	}



	/**
	 * Get information of the room associated with client.
	 *
	 * @return this
	 */
	public RoomState GetRoomState() {
		preTemp = currentTemp;
		preTime = currentTime;
		currentTime = System.currentTimeMillis();
		if (state >= 1  && Math.abs(currentTemp-targetTemp) < 1) { // recover limit: 1
			if (Server.mode == 0 && currentTemp >= targetTemp) { // heat
				currentTemp -= 0.5 * (float)(currentTime-preTime)/60000;
			} else if (Server.mode == 1 && currentTemp <= targetTemp) { // cool
				currentTemp += 0.5 * (float)(currentTime-preTime)/60000;
			}
		} else if (state == 0){ // serve
			if (Server.mode == 0 && currentTemp < targetTemp) { // heat
				currentTemp += ((float)(currentTime-preTime)/60000) / fanSpeed;
			} else if (Server.mode == 1 && currentTemp > targetTemp) { // cool
				currentTemp -= ((float)(currentTime-preTime)/60000) / fanSpeed;;
			}
		}
		duration += currentTemp - startTime;
		if (timer != null) {
			timer.waitTime -= currentTime - preTime;
		}
		fee += feeRate * Math.abs(currentTemp - preTemp);
		return new RoomState(on, id, rmId, fee, feeRate, duration, fanSpeed, targetTemp, currentTemp);
	}

	public void Record(String roomId, String startTime, int fanSpeed, float feeRate) {
		DetailForm detailForm = new DetailForm();
		detailForm.InsertRecord(roomId, startTime, fanSpeed, feeRate);
	}

}
