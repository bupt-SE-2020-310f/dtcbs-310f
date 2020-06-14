package struct;

public class Invoice {
	String roomId;
	long dateIn;
	long dateOut;
	float fee;
	
	public Invoice(String roomId, long dateIn, long dateOut, float fee) {
		this.roomId = roomId;
		this.dateIn = dateIn;
		this.dateOut = dateOut;
		this.fee = fee;
	}
	
	public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
	public long getDateIn() {
        return dateIn;
    }
    public void setDateIn(long dateIn) {
        this.dateIn = dateIn;
    }
    
	public long getDateOut() {
        return dateOut;
    }
    public void setDateOut(long dateOut) {
        this.dateOut = dateOut;
    }
    
	public float getFee() {
        return fee;
    }
    public void setFee(float fee) {
        this.fee = fee;
    }
}