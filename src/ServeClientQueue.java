import struct.RoomState;

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
     * Get information of rooms listed in listRoomId.
     *
     * @param listRoomId the list of roomId
     * @return the list of room information, null if error occurs
     */
    public List<RoomState> CheckRoomState(List<String> listRoomId) {
        return null;
    }

    /**
     * Get an identifier of room,
     * this room has the lowest priority and lower than level.
     *
     * @param level the limit priority
     * @return room identifier normally, null if no such room exits
     */
    public String HasLowerPriority(int level) {
        return null;
    }
}
