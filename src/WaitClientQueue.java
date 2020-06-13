/**
 * This class mantains the relation between server
 * and clients waiting for the service.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class WaitClientQueue extends Queue {

    /**
     * Override method, add timer to client in waitQ and start timer.
     */
    @Override
    public void Add(String roomId, Client client) {
        if (roomId == null || client == null) {
            return;
        }
        client.timer = new Timer(roomId, waitTime, this);
        client.timer.TimeSet();
        this.roomInfo.put(roomId, client);
        this.queueLength += 1;
    }

    /**
     * Override method, cancel timer and set as null.
     */
    @Override
    public  Client Pop(String roomId) {
        Client client = this.roomInfo.remove(roomId);
        if (client != null) {
            queueLength -= 1;
            if (client.timer != null) {
                client.timer.TimeCancel();
                client.timer = null;
            }
        }
        return client;
    }

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
            if (client.on && client.priority > high) {
                high = client.priority;
                id = roomId;
            }
        }
        return id;
    }
}
