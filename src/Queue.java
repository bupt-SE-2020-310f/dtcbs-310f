

import struct.RoomState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class is the base class of WaitClientQueue and ServeClientQueue.
 * and offers some basic operations of these tow queues.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class Queue {
    int queueLength;
    Map<String, Client> roomInfo;
    Queue tother;
    int waitTime;
    public final static String QLOCK = "QLOCK";

    public Queue() {
        this.roomInfo = new LinkedHashMap<>();
        this.waitTime = 2 * 60 * 1000; // 2min
    }

    /**
     * Check whether the room associated with roomId is binded a client in this queue or not.
     *
     * @param roomId identifier of room
     * @return true if in, false else
     */
    public boolean IsIn(String roomId) {
        return this.roomInfo.containsKey(roomId);
    }

    /**
     * Bind the room associated with roomId with a client,
     * and add the client to self.roomInfo.
     * Self.queueLength plus 1.
     * Overrided by subclass
     */
    public void Add(String roomId, Client client) {

    }

    /**
     * Get the client binded on the room associated with roomId.
     *
     * @param roomId identifier of room to get
     * @return  the client, null if no such client exists
     */
    public Client Get(String roomId) {
        return this.roomInfo.get(roomId);
    }

    /**
     * Pop the client at roomId in this queue, and the queueLength minus by 1.
     *
     * @param roomId identifier of room to pop
     * @return the client, null if no such client exists
     */
    public  Client Pop(String roomId) {
        Client client = this.roomInfo.remove(roomId);
        if (client != null) {
            queueLength -= 1;
        }
        return client;
    }

    /**
     * Exchange client1 with client2.
     *
     * @param rmId1 identifier of room1
     * @param rmId2 identifier of room2
     */
    public void Exchange(String rmId1, String rmId2) {
        if (!this.IsIn(rmId1) || !this.tother.IsIn(rmId2)) {
            return;
        }
        Client client1 = this.Pop(rmId1);
        Client client2 = this.tother.Pop(rmId2);
        this.Add(rmId2, client2);
        this.tother.Add(rmId1, client1);
    }

    /**
     * Get the roomId for the room,
     * the room is binded with the client that has the lowest priority.
     * When some clients have equal priority, select the first one.
     * Priority can be represented by fanSpeed here.
     *
     * @return identifier of target room, null if no such room exists
     */
    public String HasLowestPriority() {
        int low = 3;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.Get(roomId);
            if (client.state != Client.SHUTDOWN && client.state != Client.STANDBY && client.priority < low) {
                low = client.priority;
                id = roomId;
            }
        }
        return id;
    }

    /**
     * Get the roomId for target room,
     * the room is binded with the client that has the highest priority.
     * When some clients have equal priority, select the first one.
     * Priority can be represented by fanSpeed here.
     *
     * @return identifier of target room
     */
    public String HasHighestPriority() {
        int high = Client.P_LAST;
        String id = null;
        for (String roomId : this.roomInfo.keySet()) {
            Client client = this.Get(roomId);
            if (client.state != Client.SHUTDOWN && client.state != Client.STANDBY && client.priority > high) {
                high = client.priority;
                id = roomId;
            }
        }
        return id;
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
     * Get state information list of rooms associated with listRoomId.
     *
     * @param listRoomId the list of roomId
     * @return the list of room information;
     *          if room has not got a client in a queue, its information is null
     */
    public List<RoomState> CheckRoomState(List<String> listRoomId) {
        List<RoomState> roomStateList = new ArrayList<>();
        for (String roomId : listRoomId) {
            if (this.IsIn(roomId)) {
                roomStateList.add(this.Get(roomId).GetRoomState());
            } else if (this.tother.IsIn(roomId)) {
                roomStateList.add(this.tother.Get(roomId).GetRoomState());
            } else {
                roomStateList.add(null);
            }
        }
        return roomStateList;
    }

    public List<RoomState> CheckRoomState() {
        List<RoomState> roomStateList = new ArrayList<>();
        for (String roomId : this.roomInfo.keySet()) {
            roomStateList.add(this.Get(roomId).GetRoomState());
        }
        for (String roomId : this.tother.roomInfo.keySet()) {
            roomStateList.add(this.tother.Get(roomId).GetRoomState());
        }
        return roomStateList;
    }

    /**
     * Change target fanSpeed  as well as feeRate of target room.
     * Put the client into tail of queue and make a record.
     *
     * @param roomId identifier of room
     * @param speed target fan speed
     */
    public void ChangeSpeed(String roomId, int speed) {
        if (IsIn(roomId)) {
            Client client = this.Pop(roomId);
            this.Add(roomId, client);
            client.Enable(speed,1); // change fanSpeed and feeRate, and insert a record
        }
    }
}
