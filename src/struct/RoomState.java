package struct;

/**
 * This class stores state information of a room in service
 *
 * @author zyl
 * @since 9 June 2020
 */
public class RoomState {
    public String roomId;
    public int state;
    public float currTemp;
    public int targetTemp;
    public int fan;
    public float fee;

    public RoomState(){};
}
