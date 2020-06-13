
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
	Timer timer = null;
    int priority;   // smaller value has lower priority
    int fanSpeed; // also as priority: 0,1,2, smaller value has lower fanSpeed/priority
    int targetTemp;
    float currentTemp;

	Client(String rmId, String id, float currTmep, int fanSpeed, int targetTemp){
		System.out.println("!!!!!!!!!!!!!!!!!!");
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
	/*
	Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
    }*/

	public boolean Enable(int roomId, int mode, int speed, int tgTemp, String requestTime, int requestDuration) throws InterruptedException {
		this.on = true;
		this.fanSpeed = speed;
		this.duration = requestDuration;
		//���³���
		try {
			if (mode == 1) {//����ģʽ
				this.currentTemp = 16;
				this.targetTemp = tgTemp;
				while (currentTemp != targetTemp) {
					Thread.sleep(60000);
					//ģ������ÿ60s��0.5��
					currentTemp += 0.5;
				}

				this.priority = 1;  // default = 1
				this.fee = 0;
			}
		} finally {

		}
		return true;
	}

	public boolean Enable(String roomId, int mode, int speed) {
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
		this.fee += this.feeRate;
		this.Record(roomId,startTime.format(date),speed,feeRate);
		return true;
	}
	
/*	public Client GetRoomState() {
		Client RoomState = new Client();
		return RoomState;
	}*/


	/**
     * Get information of the room associated with client.
     *
     * @return the information
     */
    public RoomState GetRoomState() {
        return null;
    }

	public void Record(String roomId, String startTime, int fanSpeed, float feeRate) {
		DetailForm detailForm = new DetailForm();
		detailForm.InsertRecord(roomId, startTime, fanSpeed, feeRate);
	}

}
