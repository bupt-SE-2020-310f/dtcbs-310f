import struct.RoomState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for simple debug in scheduling.
 * Can be deleted together with settings.txt.
 */
public class EasyTest {
    public final static int timeSlice = 2000; // set time slice as 2s for test
    public final static String settingsPath = "src/settings.txt"; // path of settings file
    public final static int fields = 6; //
    public final static String[] speedText = {"低","中","高"};
    public static ServeClientQueue sQ = new ServeClientQueue();
    public static WaitClientQueue wQ = new WaitClientQueue();

    public static void main(String[] args) {
        sQ.tother = wQ;
        wQ.tother = sQ;
        wQ.SetWaitTime(timeSlice);
        // print table header
        int cn = 1;
        System.out.print("\t");
        for (int i = 0; i < 5; i++) {
            System.out.print("|\t\t\t\t房间" + (i+1) + "\t\t\t\t");
        }
        System.out.print("|\n\t");
        for (int i = 0; i < 5; i++) {
            System.out.print("|\t当前\t|\t目标\t|\t风速\t");
        }
        try {
            // read settings from file
            BufferedReader in = new BufferedReader(new FileReader(settingsPath));
            String line;
            System.out.print("|");
            while ((line = in.readLine()) != null) {
                System.out.print("\n" + cn + "\t");
                String[] stateStr = new String[5];
                synchronized (Queue.QLOCK) {
                    String[] attr = line.split(" ");
                    if (attr.length > 1) {
                        int cont = 0;
                        while (fields*(cont+1) <= attr.length && attr[fields*(cont+1)-1].equals("#")) {
                            cont++;
                        }
                        for (int i=0; i < cont; i++) {
                            int pos = fields * i;
                            // each line has cont commands
                            // each command has 6 parts: [cate roomId speed targetT currentT #end]
                            int cate = Integer.parseInt(attr[pos]);
                            String roomId = attr[pos+1];
                            int speed = Integer.parseInt(attr[pos+2]);
                            int targetTemp = Integer.parseInt(attr[pos+3]);
                            float currentTemp = Float.parseFloat(attr[pos+4]);
                            switch (cate) {
                                case 0:{ // add request
                                    Client client = new Client(
                                            speed,
                                            targetTemp,
                                            currentTemp
                                    );
                                    if (sQ.queueLength < 3) {
                                        sQ.Add(roomId, client);
                                    } else {
                                        String id = sQ.HasLowerPriority(speed);
                                        if (id != null) {
                                            Client client1 = sQ.Pop(id);
                                            sQ.Add(roomId, client);
                                            wQ.Add(id, client1);
                                        } else {
                                            wQ.Add(roomId, client);
                                        }
                                    }
                                }break;
                                case 1:{ // changeSpeed
                                    if (sQ.IsIn(roomId)) {
                                        sQ.ChangeSpeed(roomId, speed);
                                        String id = wQ.HasHigherPriority(speed);
                                        sQ.Exchange(roomId, id);
                                    } else if (wQ.IsIn(roomId)){
                                        wQ.ChangeSpeed(roomId, speed);
                                        String id = sQ.HasLowerPriority(speed);
                                        wQ.Exchange(roomId, id);
                                    }
                                }break;
                                case 2:{ // shutdown
                                    stateStr[Integer.parseInt(roomId)-1] = "|\t\t\t\t关机\t\t\t\t";
                                    if (sQ.IsIn(roomId)) {
                                        sQ.Pop(roomId);
                                        String id = wQ.HasHighestPriority();
                                        if (id != null) {
                                            sQ.Add(id, wQ.Pop(id));
                                        }
                                    }else if (wQ.IsIn(roomId)) {
                                        wQ.Pop(roomId);
                                    }
                                }break;
                                case 3:{ // change target temperature
                                    if (targetTemp <= 25 && targetTemp >= 18) {
                                        if (sQ.IsIn(roomId)) {
                                            sQ.ChangeTemp(roomId, targetTemp);
                                        } else if (wQ.IsIn(roomId)){
                                            wQ.ChangeSpeed(roomId, targetTemp);
                                        }
                                    }
                                }break;
                            }
                        }
                    }
                    //check sQ's states
                    List<String> sKeys = new ArrayList<>(sQ.roomInfo.keySet());
                    for (String roomId : sKeys) {
                        Client client = sQ.Get(roomId);
                        int speed = client.fanSpeed;
                        int targetT = client.targetTemp;
                        float currentT = client.currentTemp;
                        if (targetT - currentT < 0.33 || targetT <= currentT) {
                            sQ.Pop(roomId);
                            stateStr[Integer.parseInt(roomId)-1] = "|\t\t\t\t回温\t\t\t\t";
                            String id = wQ.HasHighestPriority();
                            if (id != null) {
                                Client client1 = wQ.Pop(id);
                                sQ.Add(id, client1);
                                stateStr[Integer.parseInt(id)-1] = String.format("|\tS%.2f\t|\t%d\t\t|\t%s\t\t", client1.currentTemp, client1.targetTemp, speedText[client1.fanSpeed]);
                            }
                        } else {
                            client.currentTemp += (float) 1 / (3-speed);
                            stateStr[Integer.parseInt(roomId)-1] = String.format("|\tS%.2f\t|\t%d\t\t|\t%s\t\t", client.currentTemp, targetT, speedText[speed]);
                        }
                    }
                    //check wQ's states
                    List<String> wKeys = new ArrayList<>(wQ.roomInfo.keySet());
                    for (String roomId : wKeys) {
                        Client client = wQ.Get(roomId);
                        int speed = client.fanSpeed;
                        int targetT = client.targetTemp;
                        float currentT = client.currentTemp;
                        stateStr[Integer.parseInt(roomId)-1] = String.format("|\tW%.2f\t|\t%d\t\t|\t%s\t\t", currentT, targetT, speedText[speed]);
                        if (wQ.Get(roomId).timer != null) {
                            wQ.Get(roomId).timer.waitTime -= timeSlice/2;
                        }
                    }
                    // print states
                    for (String state : stateStr) {
                        if (state == null) {
                            System.out.print("|\t\t\t\t\t\t\t\t\t");
                        } else {
                            System.out.print(state);
                        }
                    }
                    System.out.print("|");
                }
                Thread.sleep(timeSlice/2);
                cn++;
            }
            in.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
