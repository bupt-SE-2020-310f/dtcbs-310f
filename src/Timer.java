/**
 * This class sets timer for client in service.
 *
 * @author zyl
 * @since 8 June 2020
 */
public class Timer {
    Thread timeThread;
    int waitTime;
    String roomId;

    /**
     * Initialize the timer for client at roomId.
     *
     * @param rmId identifier of room
     * @param time time slice length
     * @param queue timer attched to the queue, and used for callback
     */
    public Timer(String rmId, int time, Queue queue) {
        waitTime = time;
        roomId = rmId;
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    synchronized (Queue.QLOCK) {
                        System.out.println(rmId + " timer out");
                        ((WaitClientQueue)queue).TimeOut(roomId);
                    }
                } catch (InterruptedException ignored) {

                }
            }
        }, "timer " + roomId);
    }

    /**
     * Start timer
     */
    public void TimeSet() {
        this.timeThread.start();
    }

    /**
     * Cancel timer
     */
    public void TimeCancel() {
        this.timeThread.interrupt();
        try {
            this.timeThread.join();
        } catch (InterruptedException ignored) {

        }
        System.out.println(roomId + "timer canceled");
    }
}
