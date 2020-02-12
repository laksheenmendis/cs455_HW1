package cs455.overlay.util;

import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
    Used to collect various statistics and display them
 */
public class StatisticsCollectorAndDisplay {

    private static void printSeparator()
    {
        System.out.println("\n\n\n");
        System.out.println("***********************************************************************************************");
        System.out.println("\n\n\n");
    }

    private static void printLineSeperator1()
    {
        System.out.println("--------------------------------------------------");
    }

    private static void printLineSeperator2()
    {
        System.out.println("-------------------------------------------------------------");
    }

    private static void printLineSeperator3()
    {
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    public static void printMessagingNodeLise(Set<Map.Entry<String,Integer>> entries)
    {
        printSeparator();
        printLineSeperator1();
        String hostname= "Hostname";
        String portNo ="Port-number";
        String nodeID = "Node ID";

        String s = String.format("|%-21s|%-15s|%-10s|",hostname,portNo, nodeID);
        System.out.println(s);
        printLineSeperator1();

        for(Map.Entry<String, Integer> entry :entries )
        {
            String [] arr = entry.getKey().split(Constants.JOINING_CHARACTER);
            String ip = arr[0];
            String port = arr[1];
            int id = entry.getValue();

            String s1 = String.format("|%21s|%15s|%10d|",ip,port, id);
            System.out.println(s1);
            printLineSeperator1();
        }
    }

    public static void printRoutingTables(TreeMap<Integer, RegistrySendsNodeManifest.NodeInfo[]> routingEntryTreeMap) {

        printSeparator();

        for(Map.Entry<Integer, RegistrySendsNodeManifest.NodeInfo[]> entry: routingEntryTreeMap.entrySet())
        {
            String heading = "Routing table of messaging node " + entry.getKey() + "\n";
            System.out.println(heading);
            printRoutingEntry(entry.getValue());
            System.out.println("\n\n\n");
        }
    }

    private static void printRoutingEntry(RegistrySendsNodeManifest.NodeInfo[] nodeInfos)
    {
        printLineSeperator2();
        String distance = "Distance";
        String hostname= "Hostname";
        String portNo ="Port-number";
        String nodeID = "Node ID";

        String s = String.format("|%-10s|%-21s|%-15s|%-10s|",distance,hostname,portNo, nodeID);
        System.out.println(s);
        printLineSeperator2();

        for(RegistrySendsNodeManifest.NodeInfo node :nodeInfos )
        {
            String s1 = String.format("|%10d|%21s|%15s|%10d|",node.get_distance(), node.getIPAddress(), node.getPortNumber(), node.getNodeID());
            System.out.println(s1);
            printLineSeperator2();
        }
    }

    public static void printMNCounters(int sendTracker, int receiveTracker, int relayTracker, long sendSummation, long receiveSummation) {

        printSeparator();

        String s1 = String.format("Number of messages sent : %,d", sendTracker);
        String s2 = String.format("Number of messages received : %,d", receiveTracker);
        String s3 = String.format("Number of messages relayed : %,d", relayTracker);
        String s4 = String.format("Summation of messages sent : %,d", sendSummation);
        String s5 = String.format("Summation of messages received : %,d", receiveSummation);

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println(s5);
    }

    public static void printTrafficSummary(List<OverlayNodeReportsTrafficSummary> trafficSummaries) {

        printSeparator();

        int totPckSent = 0;
        int totPckRcvd = 0;
        int totPckRlyd = 0;
        long totalSentSummation = 0L;
        long totalRcvdSummation = 0L;

        String empty = "          ";
        String pckSent = "Packets Send";
        String pckRcvd = "Packets Received";
        String pckRlyd = "Packets Relayed";
        String sumSentH = "Sum Values Sent";
        String sumRcvdH = "Sum Values Received";

        printLineSeperator3();
        String s = String.format("|%-10s|%-12s|%-17s|%-16s|%-16s|%-20s|",empty, pckSent, pckRcvd, pckRlyd, sumSentH, sumRcvdH);
        System.out.println(s);
        printLineSeperator3();

        for(OverlayNodeReportsTrafficSummary trafficSummary : trafficSummaries)
        {
            totPckSent += trafficSummary.getTotalPacketsSent();
            totPckRcvd += trafficSummary.getTotalPacketsReceived();
            totPckRlyd += trafficSummary.getTotalPacketsRelayed();
            totalSentSummation += trafficSummary.getSumOfPacketDataSent();
            totalRcvdSummation += trafficSummary.getSumOfPacketDataReceived();

            String node = "Node " + trafficSummary.getAssignedNodeID();

            String s1 = String.format("|%10s|%,12d|%,17d|%,16d|%,16d|%,20d|",node, trafficSummary.getTotalPacketsSent(), trafficSummary.getTotalPacketsReceived(), trafficSummary.getTotalPacketsRelayed(), trafficSummary.getSumOfPacketDataSent(), trafficSummary.getSumOfPacketDataReceived());
            System.out.println(s1);
            printLineSeperator3();
        }

        String sumS = "Sum";
        String s2 = String.format("|%10s|%,12d|%,17d|%,16d|%,16d|%,20d|",sumS, totPckSent, totPckRcvd, totPckRlyd, totalSentSummation, totalRcvdSummation);
        System.out.println(s2);
        printLineSeperator3();


    }
}
