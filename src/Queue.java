import java.util.HashMap;
import java.util.Map;

/**
 * This class is the base class of WaitClentQueue and ServeClientQueue.
 * This class offers some basic operations of tow queues above.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class Queue {
    Dispatcher dispatcher;
    int queueLength;
    Map<String, Client> roomInfo;
    Queue tother;
    int waitTime;

    public Queue(Dispatcher dispatcher, Queue tother) {
        this.dispatcher = dispatcher;
        this.tother = tother;
        this.roomInfo = new HashMap<>();
    }

    /**
     * Add request from target room to current queue.
     * The message is stored in self.roomInfo and self.length plus 1.
     *
     * @param roomId identifier of room
     * @param speed target fan speed
     * @param temp target temperature
     * @param curTemp current temperature
     * @return  true
     */
    public boolean Add(String roomId, int speed, int temp, float curTemp) {
        this.roomInfo.put(roomId, new Client(speed, temp, curTemp));
        this.queueLength += 1;
        return true;
    }

    public boolean Add(String roomId, Client client) {
        this.roomInfo.put(roomId, client);
        this.queueLength += 1;
        return true;
    }

    /**
     * Change fan speed of target room in queue.
     *
     * @param roomId identifier of room
     * @param speed target fan speed
     */
    public void ChangeSpeed(String roomId, int speed) {
        this.roomInfo.get(roomId).fanSpeed = speed;
    }

    /**
     * Exchange client1 at rmId1 in this queue with client2 at rmId2 in the tother queue.
     *
     * @param rmId1 identifier of room in this queue
     * @param rmId2 identifier of room in the tother queue
     */
    public void Exchange(String rmId1, String rmId2) {
        Client client1 = this.Pop(rmId1);
        Client client2 = this.tother.Pop(rmId2);
        this.Add(rmId2, client2);
        this.tother.Add(rmId1, client1);
    }

    public Client Get(String rmId) { return this.roomInfo.get(rmId); }

    /**
     * Check whether client at roomId is in this queue.
     *
     * @param roomId identifier of room
     * @return true if room in, false else
     */
    public boolean IsIn(String roomId) {
        return this.roomInfo.containsKey(roomId);
    }

    /**
     * Pop the client at roomId in this queue, and the queueLength minus by 1.
     *
     * @param roomId identifier of room to pop
     * @return the information of room with roomId, null if the room not in
     */
    public Client Pop(String roomId) {
        Client client = this.roomInfo.remove(roomId);
        if (client != null) {
            queueLength -= 1;
        }
        return client;
    }

    /**
     * Set attribute waitTime: the length of time slice.
     *
     * @param time length to set, by seconds
     */
    public void SetWaitTime(int time) {
        this.waitTime = time;
    }

    /**
     * When the room with roomId ran through a time slice,
     * this method is invoked by class Timer to implement RR.
     * This client will be put in waitClientQueue with its priority minus by 1.
     * Move the client with the highest priority client in waitClientQueue to this queue.
     *
     * @param roomId identifier of room with timeout
     */
    public void TimeOut(String roomId) {
        if (this instanceof WaitClientQueue) {
            return;
        }
        // move this client from serveQ to waitQ
        Client client = this.Pop(roomId);
        if (client.priority > 0) { // make sure that priority is always non-negative
            client.priority -= 1;
        }
        this.tother.Add(roomId, client);
        if (this.queueLength < 3) { // to avoid concurrency conflict
            // move the highest priority client from waitQ to serveQ
            String id = ((WaitClientQueue)this.tother).HasHigherPriority(-1);
            client = this.tother.Pop(id);
            this.Add(id, client);
        }
    }
}
