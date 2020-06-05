import struct.RoomState;

import java.util.List;

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
    List<RoomState> roomInfo;
    Queue tother;
    int waitTime;

    /**
     * Add request from rooms to current queue.
     * The message is stored in self.roomInfo
     * and self.length plus 1.
     *
     * @param roomId identifier of room
     * @param speed target fan speed
     * @param temp target temperature
     * @param curTemp current temperature
     * @return  true if the request is added successfully, false else
     */
    public boolean Add(String roomId, int speed, int temp, float curTemp) {
        return false;
    }

    /**
     * Change fan speed of rooms in queue.
     *
     * @param roomId identifier of room
     * @param speed targer fan speed
     */
    public void ChangeSpeed(String roomId, int speed) {

    }

    /**
     * Exchange rmId1 in this queue with rmId2 in the tother queue.
     *
     * @param rmId1 identifier of room in this queue
     * @param rmId2 identifier of room in the tother queue
     */
    public void Exchange(String rmId1, String rmId2) {

    }

    /**
     * Check whether room at roomId is in this queue.
     *
     * @param roomId identifier of room
     * @return true if room in, false else
     */
    public boolean IsIn(String roomId) {
        return false;
    }

    /**
     * Pop the room at roomId in this queue.
     *
     * @param roomId identifier of room to pop
     */
    public void Pop(String roomId) {

    }

    /**
     * Set attribute waitTime,
     * the length of time slice.
     *
     * @param time length to set, in seconds
     */
    public void setWaitTime(int time) {

    }

    /**
     * When the timer room at roomId ran out,
     * invoked by Timer class, execute relative operations.
     *
     * @param roomId identifier of room with timeout
     */
    public void TimeOut(String roomId) {

    }
}
