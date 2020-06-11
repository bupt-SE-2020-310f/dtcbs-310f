import struct.RoomState;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;

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
    int priority;   // smaller value has lower priority
    int fanSpeed; // also as priority: 0,1,2, smaller value has lower fanSpeed/priority
    int targetTemp;
    float currentTemp;


	DetailForm detailForm;

	Client(String rmId, String id, float currTmep){
		this.rmId = rmId;
		this.id = id;
		this.currentTemp = currTmep;
		this.priority = 0;
		this.fee = 0;
	}

	Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
    }
	
	public boolean Enable(int roomId, int mode, int speed, int tgTemp, String requestTime, int requestDuration) throws InterruptedException {
		this.on = true;
		this.fanSpeed = speed;
		this.duration = requestDuration;
		//变温程序
		try {
			if (mode == 1) {//制热模式
				this.currentTemp = 16;
				this.targetTemp = tgTemp;
				while (currentTemp != targetTemp) {
					Thread.sleep(60000);
					//模拟升温每60s升0.5度
					currentTemp += 0.5;
				}

				this.priority = 1;  // default = 1
				this.fee = 0;
			}
		} finally {

		}
		return true;
	}

	public boolean Enable(String roomId, int mode, int speed) throws InterruptedException {
		SimpleDateFormat startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		float changeTemp;
		float needMinute;
		float electricQuantity;
		if (mode == 1) {//heating
			changeTemp = this.targetTemp - this.currentTemp;
			needMinute = (float) (changeTemp / 0.5);
			if (speed == 3) {//H
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if (speed == 2) {//M
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			if (speed == 1) {//L
				electricQuantity = needMinute / 3;
				feeRate = electricQuantity * 1;
			}
		} else if (mode == 0) {//refrigeration
			changeTemp = this.currentTemp - this.targetTemp;
			needMinute = (float) (changeTemp / 0.5);
			if (speed == 3) {//H
				electricQuantity = needMinute / 1;
				feeRate = electricQuantity * 1;
			}
			if (speed == 2) {//M
				electricQuantity = needMinute / 2;
				feeRate = electricQuantity * 1;
			}
			//this.Record(roomId, requestTime);//保存记录

			//回温程序
			if (mode != 0)
				while (Math.abs(currentTemp - targetTemp) != 3) {//温差为3度时发送请求
					Thread.sleep(60000);//模拟回温每60s降0.5度
					currentTemp -= 0.5;
				}
			else
				while (Math.abs(currentTemp - targetTemp) != 3) {
					Thread.sleep(60000);//模拟回温每60s升0.5度
					currentTemp += 0.5;
				}

			//重新发送修改温度风速请求
			if (Math.abs(currentTemp - targetTemp) == 3) {
				String u = "address";//server地址
				try {
					URL url = new URL(u + "/request");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setRequestMethod("POST"); // 设置请求方式
					connection.setRequestProperty("Keep-Alive", "application/x-www-form-urlencoded; charset=UTF-8");

					PrintWriter pw = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
					pw.write("name=请进行调温!");
					pw.flush();
					pw.close();

					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
					String line = null;
					StringBuilder result = new StringBuilder();
					while ((line = br.readLine()) != null) { // 读取数据
						result.append(line + "\n");
					}
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (speed == 1) {//L
					electricQuantity = needMinute / 3;
					feeRate = electricQuantity * 1;
				}
			}
			this.fee += this.feeRate;
			this.Record(roomId, startTime, speed, feeRate);
			return true;
		}
		return false;
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


	public void Record(int roomId, String requestTime) {
		this.detailForm.InsertRecord(roomId, requestTime, this.duration, this.fanSpeed,
				this.feeRate, this.fee);
	}

	public void Record(String roomId, SimpleDateFormat startTime, int fanSpeed, float feeRate) {
		//this.detailForm.InsertRecord(roomId, startTime, fanSpeed, feeRate);
	}

}
