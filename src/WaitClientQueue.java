/**
 * This class mantains the relation between server
 * and clients waiting for the service.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class WaitClientQueue extends Queue {

    /**
     * Get the roomId of target room,
     * this room is binded with a client having the highest priority and higher than level.
     * When some clients have equal priority, select the first one,
     * because it has the shortest wait-time
     * Priority can be represented by fanSpeed here.
     *
     * @param level the limit priority
     * @return room identifier normally, null if no such room exits.
     */
    public String HasHigherPriority(int level) {
        int high = level;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.Get(roomId);
            if (client.fanSpeed > high) {
                high = client.fanSpeed;
                id = roomId;
            }
        }
        return id;
    }
}
