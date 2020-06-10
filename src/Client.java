import struct.RoomState;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class works as a service provider for rooms,
 * these rooms are in service or waitting for service.
 *
 * @author zyl
 * @since 9 June 2020
 */
public class Client {
	boolean on;
	float fee;
	float feeRate;
	int duration;
    int priority;   // smaller value has lower priority
    int fanSpeed; // also as priority: 0,1,2, smaller value has lower fanSpeed/priority
    int targetTemp;
    float currentTemp;


	DetailForm detailForm;
	
	Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
    }
	
	public boolean Enable(int roomId, int mode, int speed, int tgTemp, String requestTime, int requestDuration) {
		this.on = true;
		this.fanSpeed = speed;
		this.duration = requestDuration;
		//变温程序
		try {
			if(mode == 1) {//制热模式
				this.currentTemp = 16;
				this.targetTemp = tgTemp;
				while(currentTemp != targetTemp){
						Thread.sleep(60000);
					 //模拟升温每60s升0.5度
					currentTemp += 0.5;
				}
			}
			else if(mode == 0) {//制冷模式
				this.currentTemp = 32;
				this.targetTemp = tgTemp;
				while(currentTemp != targetTemp){
					Thread.sleep(60000);//模拟降温每60s降0.5度
					currentTemp -= 0.5;
				}
			}
			this.Record(roomId,requestTime);//保存记录
			
			//回温程序
			if(mode!=0)
				while(Math.abs(currentTemp-targetTemp) != 3){//温差为3度时发送请求
					Thread.sleep(60000);//模拟回温每60s降0.5度
					currentTemp -= 0.5;
				}
			else
				while(Math.abs(currentTemp-targetTemp) != 3){
					Thread.sleep(60000);//模拟回温每60s升0.5度
					currentTemp += 0.5;
				}
		
			//重新发送修改温度风速请求
			if(Math.abs(currentTemp-targetTemp) == 3){
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
			}
		return true;
		}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
/*	public Client GetRoomState() {
		Client RoomState = new Client();
		return RoomState;
	}*/

    /**
     * Get information of the room binded with this client.

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

}
