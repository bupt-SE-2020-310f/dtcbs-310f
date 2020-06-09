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

    /**
     * Get the roomId of target room,
     * the room is binded with a client having the lowest priority and lower than level.
     * When some clients have equal priority, select the first one,
     * because it has the longest serve-time.
     * Priority can be represented by fanSpeed here.
     *
     * @param level the limit priority
     * @return room identifier normally, null if no such room exits
     */
    public String HasLowerPriority(int level) {
        int low = level;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.Get(roomId);
            if (client.fanSpeed < low) {
                low = client.fanSpeed;
                id = roomId;
            }
        }
        return id;
    }

    @Override
    public List<RoomState> CheckRoomState(List<String> listRoomId) {
        return super.CheckRoomState(listRoomId);
    }
}
