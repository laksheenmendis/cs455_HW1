package cs455.overlay.util;

import cs455.overlay.node.Registry;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import static java.lang.Thread.sleep;

/*
    This thread is used to keep track of the number of traffic summaries received from
    messaging nodes and display the overall summary, when all traffic summaries have arrived.
 */
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

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.log(Level.INFO,"[TrafficSummaryTracker_run] Received traffic summaries from all nodes");
        StatisticsCollectorAndDisplay.printTrafficSummary(registry.getTrafficSummaries());
    }
}
