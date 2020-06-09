import struct.RoomState;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	Client self = new Client();
	    public Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
        this.priority = 1;  // default = 1
    }
	
	public boolean Enable(int roomId, int mode, int speed, int tgTemp, String requestTime, int requestDuration) {
		this.on = true;
		this.fanSpeed = speed;
		this.duration = requestDuration;
		//���³���
		try {
			if(mode == 1) {//����ģʽ
				this.currentTemp = 16;
				this.targetTemp = tgTemp;
				while(currentTemp != targetTemp){
						Thread.sleep(60000);
					 //ģ������ÿ60s��0.5��
					currentTemp += 0.5;
				}
			}
			else if(mode == 0) {//����ģʽ
				this.currentTemp = 32;
				this.targetTemp = tgTemp;
				while(currentTemp != targetTemp){
					Thread.sleep(60000);//ģ�⽵��ÿ60s��0.5��
					currentTemp -= 0.5;
				}
			}
			self.Record(roomId,requestTime);//�����¼
			
			//���³���
			if(mode!=0)
				while(Math.abs(currentTemp-targetTemp) != 3){//�²�Ϊ3��ʱ��������
					Thread.sleep(60000);//ģ�����ÿ60s��0.5��
					currentTemp -= 0.5;
				}
			else
				while(Math.abs(currentTemp-targetTemp) != 3){
					Thread.sleep(60000);//ģ�����ÿ60s��0.5��
					currentTemp += 0.5;
				}
		
			//���·����޸��¶ȷ�������
			if(Math.abs(currentTemp-targetTemp) == 3){
				String u = "address";//server��ַ
		        try {
		            URL url = new URL(u + "/request");
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		            connection.setRequestMethod("POST"); // ��������ʽ
		            connection.setRequestProperty("Keep-Alive", "application/x-www-form-urlencoded; charset=UTF-8");
		
		            PrintWriter pw = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
		            pw.write("name=����е���!");
		            pw.flush();
		            pw.close();
		
		            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		            String line = null;
		            StringBuilder result = new StringBuilder();
		            while ((line = br.readLine()) != null) { // ��ȡ����
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
	
	public Client GetRoomState() {
		Client RoomState = new Client();
		return RoomState;
	}
	
	public void Record(int roomId, String requestTime) {
		self.detailForm.InsertRecord(roomId, requestTime, this.duration, this.fanSpeed, 
				this.feeRate, this.fee);
	}

	/**
     * Get information of the room associated with client.
     *
     * @return the information
     */
    public RoomState GetRoomState() {
        return null;
    }
}
