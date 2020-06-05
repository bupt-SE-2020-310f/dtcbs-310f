/**
 * This class mantains the relation between server
 * and clients waiting for the service.
 *
 * @author zyl
 * @since 5 June 2020
 */
public class WaitClientQueue extends Queue {

    /**
     * Get an identifier of room,
     * this room has the highest priority and higher than level.
     *
     * @param level a limit priority
     * @return room identifier normally, null if no such room exits.
     */
    public String HasHigherPriority(int level) {
        return null;
    }
}
