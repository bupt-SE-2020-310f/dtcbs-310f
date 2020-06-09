import struct.RoomState;

public class Client {
    int fanSpeed;
    int targetTemp;
    float currentTemp;
    int priority;   // smaller value has lower priority

    public Client(int fanSpeed, int targetTemp, float currentTemp) {
        this.fanSpeed = fanSpeed;
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
        this.priority = 1;  // default = 1
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
