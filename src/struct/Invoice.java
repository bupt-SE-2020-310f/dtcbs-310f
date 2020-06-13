package struct;

public class Invoice {
    String roomId;
    String dateIn;
    String dateOut;
    float fee;

    public Invoice(String roomId, String dateIn, String dateOut, float fee) {
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

    public String getDateIn() {
        return dateIn;
    }
    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public String getDateOut() {
        return dateOut;
    }
    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public float getFee() {
        return fee;
    }
    public void setFee(float fee) {
        this.fee = fee;
    }
}
