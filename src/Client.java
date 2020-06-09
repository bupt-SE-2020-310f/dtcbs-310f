import struct.RoomState;

import java.text.SimpleDateFormat;

public class Client {
	boolean on;
	float currentTemp;
	int targetTemp;
	int fanSpeed;
	float fee;
	float feeRate;
	int duration;
    int priority;   // smaller value has lower priority

	DetailForm detailForm;
	
	Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
        this.priority = 1;  // default = 1
        this.fee = 0;
    }
	
	public boolean Enable(String roomId, int mode, int speed) {
		SimpleDateFormat startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");  
		float changeTemp;
		float needMinute;
		float electricQuantity;
		if(mode == 1) {//制热
			changeTemp = this.targetTemp - this.currentTemp;
			needMinute = (float) (changeTemp / 0.5);
			if(speed == 3) {//高
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if(speed == 2) {//中
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			if(speed == 1) {//低
				electricQuantity = needMinute / 3;
				feeRate = electricQuantity * 1;
			}
		}
		else if(mode == 0) {//制冷
			changeTemp = this.currentTemp - this.targetTemp;
			needMinute = (float) (changeTemp / 0.5);
			if(speed == 3) {//高
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if(speed == 2) {//中
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			if(speed == 1) {//低
				electricQuantity = needMinute / 3;
				feeRate = electricQuantity * 1;
			}
		}
		this.fee += this.feeRate;
		this.Record(roomId,startTime,speed,feeRate);
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

	public void Record(String roomId, SimpleDateFormat startTime, int fanSpeed, float feeRate) {
		this.detailForm.InsertRecord(roomId, startTime, fanSpeed, feeRate);
	}

}
