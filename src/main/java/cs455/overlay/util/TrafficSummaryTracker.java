package cs455.overlay.util;

import cs455.overlay.node.Registry;
import org.apache.log4j.Logger;
import static java.lang.Thread.sleep;

public class TrafficSummaryTracker implements Runnable {

    private Registry registry;
    private static Logger LOGGER = Logger.getLogger(TrafficSummaryTracker.class.getName());

    public TrafficSummaryTracker(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {

        while(registry.getTrafficSummaries().size() != registry.getIpIDMap().size())
        {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("[TrafficSummaryTracker_run] Received traffic summaries from all nodes");
        StatisticsCollectorAndDisplay.printTrafficSummary(registry.getTrafficSummaries());
    }
}
