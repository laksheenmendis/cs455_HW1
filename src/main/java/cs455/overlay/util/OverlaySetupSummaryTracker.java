package cs455.overlay.util;

import cs455.overlay.node.Registry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import static java.lang.Thread.sleep;

/**
 * This thread class is used to keep track of NODE_REPORTS_OVERLAY_SETUP_STATUS messages sent by messaging nodes
 * and then print out that the registry is ready to initiate tasks
 */
public class OverlaySetupSummaryTracker implements Runnable {
    private Registry registry;
    private static Logger LOGGER = Logger.getLogger(OverlaySetupSummaryTracker.class.getName());

    public OverlaySetupSummaryTracker(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {

        while(registry.getOverlaySetupCount() != registry.getIpIDMap().size())
        {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOGGER.log(Level.INFO,"[OverlaySetupSummaryTracker_run] Received overlay setup statuses from all nodes");
        StatisticsCollectorAndDisplay.printRegistryReadyToStart();
    }
}
