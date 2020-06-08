/**
 * This class sets timer for client in service.
 *
 * @author zyl
 * @since 8 June 2020
 */
public class Timer {
    /**
     * Set timer for client at roomId.
     * Invoke queue.TimeOut when timer ran out.
     *
     * @param roomId identifier of room
     * @param time time slice length
     * @param queue timer attched to the queue, and used for callback
     */
    public void TimeSet(String roomId, int time, Queue queue) {
        if (queue instanceof WaitClientQueue) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.TimeOut(roomId);
            }
        }).start();
    }
}
