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
        if (client.state != Client.SHUTDOWN && client.state != Client.STANDBY) {
            client.timer = new Timer(roomId, waitTime, this);
            client.prevStTime = System.currentTimeMillis();
            client.timer.TimeSet();
            client.state = Client.WAIT;
        }
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
            if (client.state == Client.WAIT && client.priority > high) {
                high = client.priority;
                id = roomId;
            }
        }
        return id;
    }

    /**
     * When a client has waitted for a time slice in waitQ, this method will be invoked by Timer class.
     * This client will be exchanged with a target client in serveQ,
     * target client has the lower or the same priority, if priorities are equal, select the first one.
     * If no such target client, then do nothing.
     *
     * @param roomId identifier of room in timeout
     */
    public void TimeOut(String roomId) {
        if (this.IsIn(roomId)) {
            // exchange this client in waitQ with client having lower or the same priority in serveQ
            Client c = this.Get(roomId);
            if (c.state == Client.STANDBY || c.state == Client.SHUTDOWN) {
                return;
            }
            for (String id : this.roomInfo.keySet()) {
                Client client = this.Get(id);
                if (!id.equals(roomId) && client.timer != null && client.timer.waitTime < Server.updateDelay*2) {
                    client.timer.TimeCancel();
                    client.timer = null;
                }
            }
            int level = c.priority;
            String roomId2 = ((ServeClientQueue)this.tother).HasLowerPriority(level+1);
            if (roomId2 != null){
                this.Exchange(roomId, roomId2);
            } else if (this.tother.queueLength < ServeClientQueue.SQLEN) {
                this.tother.Add(roomId, this.Pop(roomId));
            }
        }
    }
}
