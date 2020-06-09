/**
 * This class mantains the relation between server
 * and clients waiting for the service.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class WaitClientQueue extends Queue {

    public WaitClientQueue(Dispatcher dispatcher, Queue tother) {
        super(dispatcher, tother);
    }

    /**
     * Get an identifier of room,
     * this room has the highest priority and higher than level.
     *
     * @param level the limit priority
     * @return room identifier normally, null if no such room exits.
     */
    public String HasHigherPriority(int level) {
        int high = level;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.roomInfo.get(roomId);
            if (client.priority > high) {
                high = client.priority;
                id = roomId;
            }
        }
        return id;
    }
}
