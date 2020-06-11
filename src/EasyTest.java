import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is used for simple debug in scheduling.
 * Can be deleted together with settings.txt.
 */
public class EasyTest {
    public static void main(String[] args) {
        ServeClientQueue sQ = new ServeClientQueue();
        WaitClientQueue wQ = new WaitClientQueue();
        wQ.SetWaitTime(2000); // set time slice as 2s for test
        sQ.tother = wQ;
        wQ.tother = sQ;

        try {
            // read settings from file
            BufferedReader in = new BufferedReader(new FileReader("D:\\Project\\git-repository\\dtcbs-310f-zyl\\src\\settings.txt"));
            String line;
            while ((line = in.readLine()) != null) {
                String[] attr = line.split(" ");
                if (attr.length > 1) {
                    int cont = 0;
                    while (4*(cont+1) <= attr.length && attr[4*(cont+1)-1].equals("#")) {
                        cont++;
                    }
                    for (int i=0; i < cont; i++) {
                        int pos = 4 * i;
                        // each line has cont commands
                        // each command has 4 parts: [cate roomId speed #end]
                        int cate = Integer.parseInt(attr[pos]);
                        String roomId = attr[pos+1];
                        int speed = Integer.parseInt(attr[pos+2]);
                        switch (cate) {
                            case 0:{ // add request
                                Client client = new Client(
                                        speed,
                                        22,
                                        14
                                );
                                if (sQ.queueLength < 3) {
                                    sQ.Add(roomId, client);
                                } else {
                                    wQ.Add(roomId, client);
                                }
                            }break;
                            case 1:{ // changeSpeed
                                sQ.ChangeSpeed(roomId, speed);
                                wQ.ChangeSpeed(roomId, speed);
                                wQ.TimeOut(roomId);
                            }break;
                            case 2:{ // shutdown
                                sQ.Pop(roomId);
                                wQ.Pop(roomId);
                            }
                        }
                    }
                }
                System.out.println("=============================================");
                for (String roomId : sQ.roomInfo.keySet()) {
                    System.out.printf("S: %s [%d]\t", roomId, sQ.Get(roomId).fanSpeed);
                }
                System.out.println();
                for (String roomId : wQ.roomInfo.keySet()) {
                    System.out.printf("W: %s [%d]\t", roomId, wQ.Get(roomId).fanSpeed);
                }
                System.out.println();
                System.out.println("=============================================");
                Thread.sleep(1000);
            }
            in.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
