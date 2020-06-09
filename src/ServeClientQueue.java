import struct.RoomState;

import java.util.ArrayList;
import java.util.List;

/**
 * This class mantains the relation between server
 * and clients in service.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class ServeClientQueue extends Queue {

    public ServeClientQueue(Dispatcher dispatcher, Queue tother) {
        super(dispatcher, tother);
    }

    /**
     * Get information of rooms listed in listRoomId.
     *
     * @param listRoomId the list of roomId
     * @return the list of room information;
     *          if room is not in this queue, its information is null
     */
    public List<RoomState> CheckRoomState(List<String> listRoomId) {
        List<RoomState> roomStateList = new ArrayList<>();
        for (String roomId : listRoomId) {
            roomStateList.add(this.roomInfo.get(roomId).GetRoomState());
        }
        return roomStateList;
    }

    /**
     * Get an identifier of room,
     * this room has the lowest priority and lower than level.
     *
     * @param level the limit priority
     * @return room identifier normally, null if no such room exits
     */
    public String HasLowerPriority(int level) {
        int low = level;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.roomInfo.get(roomId);
            if (client.priority < low) {
                low = client.priority;
                id = roomId;
            }
        }
        return id;
    }
}
