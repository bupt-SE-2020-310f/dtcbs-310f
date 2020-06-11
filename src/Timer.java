/**
 * This class sets timer for client in service.
 *
 * @author zyl
 * @since 8 June 2020
 */
public class Timer {
    Thread timeThread;

    /**
     * Initialize the timer for client at roomId.
     *
     * @param roomId identifier of room
     * @param time time slice length
     * @param queue timer attched to the queue, and used for callback
     */
    public Timer(String roomId, int time, Queue queue) {
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    queue.TimeOut(roomId);
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
    }
}
