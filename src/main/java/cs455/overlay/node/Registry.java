package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.util.TrafficSummaryTracker;
import cs455.overlay.wireformats.*;
import cs455.overlay.wireformats.RegistrySendsNodeManifest.NodeInfo;
import org.apache.log4j.Level;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static java.lang.Thread.sleep;

public class Registry implements Node, Runnable {

    //this map stores details of the IP address+Port(key) and assigned ID(value) of messaging nodes
    private HashMap<String, Integer> ipIDMap;
    //this map stores details of assignedID and socket details (with registry)
    private HashMap<Integer, Socket> idSocketMap;
    private static final int ID_UPPER_LIMIT = 127;
    private static final int ID_LOWER_LIMIT = 0;
    private static EventFactory eventFactory;
    private TCPServerThread serverThread;
    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Registry.class.getName());
    private TreeMap<Integer, NodeInfo[]> routingEntryTreeMap;
    // both variables below are used as loop variables, hence read and write should happen in main memory
    // there should only be 1 copy
    private volatile int taskFinishedReportCount = 0;
    private volatile List<OverlayNodeReportsTrafficSummary> trafficSummaries;

    private Registry() {
        ipIDMap = new HashMap<>();
        idSocketMap = new HashMap<>();
        trafficSummaries = new ArrayList<>();
    }

    public static void main(String[] args) {

        Registry registry = new Registry();
        int PORT_NUMBER = Integer.parseInt(args[0]);
        eventFactory = EventFactory.getInstance();

        try {
            registry.serverThread = new TCPServerThread(PORT_NUMBER, registry);
            Thread threadForServer = new Thread(registry.serverThread);
            threadForServer.start();

            InteractiveCommandParser cmdParser = new InteractiveCommandParser(registry);
            Thread cmdThread = new Thread(cmdParser);
            cmdThread.start();

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't start Registry on " + PORT_NUMBER + " " + e.getStackTrace());
            e.printStackTrace();
        }

    }

    @Override
    public void onEvent(Event event, Socket socket) {

        int messageType = event.getType();
        LOGGER.log(Level.INFO, "[Registry_onEvent] received " + messageType);

        switch (messageType) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerMessagingNode((OverlayNodeSendsRegistration) event, socket);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                readOverlaySetupStatus((NodeReportsOverlaySetupStatus) event, socket);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterMessagingNode((OverlayNodeSendsDeregistration) event, socket);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                readTaskFinishedMsg((OverlayNodeReportsTaskFinished) event, socket);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                readOverlayTrafficSummary((OverlayNodeReportsTrafficSummary) event, socket);
                break;
        }
    }

    @Override
    public void run() { //use this to trigger REGISTER_REQUESTS_TRAFFIC_SUMMARY message
            //may be start this thread once finishing TASK-initiate message
        while( ipIDMap.size() != taskFinishedReportCount )
        {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.log(Level.INFO, "[Registry_run] Nodes finished reporting task summaries");
        RegistryRequestsTrafficSummary trafficSummaryReq = (RegistryRequestsTrafficSummary) eventFactory.createEventByType(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);

        // send to all existing messages
        for(Map.Entry<Integer, Socket> entry : idSocketMap.entrySet())
        {
            try {
                TCPConnection.TCPSender sender = new TCPConnection.TCPSender(entry.getValue());
                sender.sendData(trafficSummaryReq.getBytes());
                LOGGER.log(Level.INFO, "[Registry_run] Traffic summary request successfully sent to Node ID " + entry.getKey());
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "[Registry_run] Failed to send Traffic summary request to Node ID " + entry.getKey() + " " + e.getStackTrace());
                e.printStackTrace();
            }
        }

        // Start a thread to track traffic summary responses and then generate the output on console
        TrafficSummaryTracker trafficSummaryTracker = new TrafficSummaryTracker(this);
        Thread summaryTrackerThread = new Thread(trafficSummaryTracker);
        summaryTrackerThread.start();
    }

    private synchronized void readOverlayTrafficSummary(OverlayNodeReportsTrafficSummary event, Socket socket) {
        trafficSummaries.add(event);
        LOGGER.log(Level.INFO, "[Registry_readOverlayTrafficSummary] Traffic Summary received from Node "+ event.getAssignedNodeID());
    }

    private void readOverlaySetupStatus(NodeReportsOverlaySetupStatus event, Socket socket) {
        if (event.getSuccessStatus() == -1) {
            LOGGER.log(Level.ERROR, event.getInfoString());
        } else {
            LOGGER.log(Level.INFO, event.getInfoString() + " at " + event.getSuccessStatus());
        }
    }

    private void readTaskFinishedMsg(OverlayNodeReportsTaskFinished event, Socket socket) {
        //check validity
        if ( idSocketMap.get(event.getNodeID()).equals(socket) )
        {
            LOGGER.log(Level.INFO, "[Registry_readTaskFinishedMsg] Received from "+ event.getNodeID());
            updateTaskFinishedCount();
        }
        else
        {
            LOGGER.log(Level.WARN, "[Registry_readTaskFinishedMsg] "+ event.getClass().getSimpleName() + " message is not from a valid Messaging Node");
        }
    }

    private synchronized void updateTaskFinishedCount()
    {
        LOGGER.log(Level.INFO, "[Registry_updateTaskFinishedCount] count updated");
        taskFinishedReportCount++;
    }

    private void deregisterMessagingNode(OverlayNodeSendsDeregistration event, Socket socket) {

        LOGGER.log(Level.INFO, "[Registry_deregisterMessagingNode] started ");

        //check for validity of request
        boolean inConnectionCache = TCPConnectionsCache.inCache(socket);

        String serverIPAddress = new String(event.getIpAddress());
        int serverPort = event.getPortNumber();

        boolean isRegistered = ipIDMap.containsKey(generateKey(serverIPAddress, serverPort));

        RegistryReportsDeregistrationStatus deregistrationResponse = null;
        if (inConnectionCache && isRegistered) //request is valid
        {
            deregistrationResponse = getDeregistrationResponse(event.getAssignedID());
            ipIDMap.remove(generateKey(serverIPAddress, serverPort));
            TCPConnectionsCache.removeEntry(socket);
        } else //request is invalid
        {
            deregistrationResponse = getDeregistrationResponse(-1);
        }

        try {
            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
            try {
                sender.sendData(deregistrationResponse.getBytes());
                LOGGER.log(Level.INFO, "[Registry_deregisterMessagingNode] sent");
            } catch (IOException e) {
                LOGGER.log(Level.ERROR,"[Registry_deregisterMessagingNode] Couldn't communicate to Messaging Node with ID " + event.getAssignedID() + " " + e.getStackTrace());
                e.printStackTrace();
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,"[Registry_deregisterMessagingNode] Error in creating TCP Sender " + e.getStackTrace());
            e.printStackTrace();
        }

    }

    private void registerMessagingNode(OverlayNodeSendsRegistration event, Socket socket) {

        LOGGER.log(Level.INFO,"[Registry_registerMessagingNode] started ");

        String serverIPAddress = new String(event.getIpAddress());
        int serverPort = event.getPortNumber();

        int ID = getID(serverIPAddress, serverPort);
        idSocketMap.put(ID, socket);

        try {
            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
            RegistryReportsRegistrationStatus registrationResponse = getRegistrationResponse(ID);
            sender.sendData(registrationResponse.getBytes());
            LOGGER.log(Level.INFO, "[Registry_registerMessagingNode] sent");
        } catch (IOException e) {
            ipIDMap.remove(generateKey(serverIPAddress, serverPort));
            idSocketMap.remove(ID);
            TCPConnectionsCache.removeEntry(socket);
            LOGGER.log(Level.ERROR,"[Registry_registerMessagingNode] Couldn't communicate to Messaging Node with ID " + ID +
                    "\nHence entries removed " + e.getStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * Generate a number between 0 and 127 and check the map until an unassigned ID is found
     * Further, insert the server IPAddress and ID into ipIDMap
     *
     * @return
     */
    private synchronized int getID(String serverIpAddress, int serverPort) {
        int ID;
        do {
            ID = (int) (Math.random() * ((ID_UPPER_LIMIT - ID_LOWER_LIMIT) + 1)) + ID_LOWER_LIMIT;
        }
        while (ipIDMap.containsValue(ID));

        ipIDMap.put(generateKey(serverIpAddress, serverPort), ID);
        return ID;
    }

    private RegistryReportsRegistrationStatus getRegistrationResponse(int ID) {
        RegistryReportsRegistrationStatus registrationStatus = (RegistryReportsRegistrationStatus) eventFactory.createEventByType(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
        registrationStatus.setSuccessStatus(ID);
        String info = Constants.REGISTRATION_SUCCESSFULL + "The number of messaging nodes currently constituting the overlay is " + ipIDMap.size();
        registrationStatus.setInfoString(info);
        LOGGER.log(Level.INFO,"[Registry_getRegistrationResponse] Registration Status response generated ");
        return registrationStatus;
    }

    private RegistryReportsDeregistrationStatus getDeregistrationResponse(int ID) {
        RegistryReportsDeregistrationStatus deregistrationStatus = (RegistryReportsDeregistrationStatus) eventFactory.createEventByType(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
        deregistrationStatus.setSuccessStatus(ID);
        deregistrationStatus.setInfoString(ID != -1 ? Constants.DEREGISTRATION_SUCCESSFULL : Constants.DEREGISTRATION_FAILED);
        LOGGER.log(Level.INFO,"[Registry_getDeregistrationResponse] Deregistration Status response generated ");
        return deregistrationStatus;
    }

    private String generateKey(String serverIPAddress, int serverPort) {
        return serverIPAddress + Constants.JOINING_CHARACTER + serverPort;
    }

    /**
     * This method is used to setup the overlay based on the number of routing table entries
     * Step1 : Calculate all hop distances based on no. of routing table entries
     * Step2 : Get all the assigned node IDs and sort
     * Step3 : For each messaging node ID,
     * Step3.1: find the connecting node IDs at calculated hop distances and generate NodeInfo
     * Step3.2: send node manifest
     *
     * @param noOfRoutingEntries
     */
    public void setupOverlay(int noOfRoutingEntries) {

        LOGGER.log(Level.INFO,"[Registry_setupOverlay] Setting up overlay started");
        List<Integer> hopsList = new ArrayList<>(noOfRoutingEntries);

        // calculate hop distances
        for (int r = 0; r < noOfRoutingEntries; r++) {
            hopsList.add((int) Math.pow(2, r));
        }

        Collection<Integer> nodeIDs = ipIDMap.values();     //assigned node IDs of all messaging nodes
        List<Integer> nodeIDList = new ArrayList<>(nodeIDs);
        Collections.sort(nodeIDList);           //sort node IDs

        routingEntryTreeMap = new TreeMap<>();

        int noOfNodes = nodeIDList.size();
        for (int i = 0; i < noOfNodes; i++) {
            int messagingNodeID = nodeIDList.get(i);
            NodeInfo[] nodeInfoArray = new NodeInfo[noOfRoutingEntries];
            int count = 0;
            for (int j : hopsList) {
                int hopNodeID;
                if (i + j >= noOfNodes) {
                    hopNodeID = nodeIDList.get(i + j - noOfNodes);
                } else {
                    hopNodeID = nodeIDList.get(i + j);
                }
                nodeInfoArray[count] = createNodeInfo(hopNodeID, j);
                count++;
            }
            boolean successfullySent = sendNodeManifest(messagingNodeID, noOfRoutingEntries, nodeInfoArray, noOfNodes, nodeIDList);

            if(successfullySent)    //store routing table details in a treeMap
            {
                routingEntryTreeMap.put(messagingNodeID, nodeInfoArray);
            }
        }
        LOGGER.log(Level.INFO,"[Registry_setupOverlay] Setting up overlay completed");
    }

    /**
     * This method is used to populate the NodeManifest message and send to the respective messaging node
     *
     * @param messagingNodeID
     * @param noOfRoutingEntries
     * @param nodeInfoArray
     * @param noOfNodes
     * @param nodeIDList
     */
    private boolean sendNodeManifest(int messagingNodeID, int noOfRoutingEntries, NodeInfo[] nodeInfoArray, int noOfNodes, List<Integer> nodeIDList) {

        LOGGER.log(Level.INFO,"[Registry_sendNodeManifest] Started forming Node Manifest to Node ID " + messagingNodeID);
        RegistrySendsNodeManifest nodeManifest = (RegistrySendsNodeManifest) eventFactory.createEventByType(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
        nodeManifest.setRoutingTableSize(noOfRoutingEntries);
        nodeManifest.setNodeInfoList(nodeInfoArray);
        nodeManifest.setNoOfNodeIDs(noOfNodes);
        int[] newlist = new int[noOfNodes];
        for (int i = 0; i < noOfNodes; i++) {
            newlist[i] = nodeIDList.get(i);
        }
        nodeManifest.setNodeIDs(newlist);

        // get socket to communicate
        Socket socket = idSocketMap.get(messagingNodeID);

        try {
            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
            sender.sendData(nodeManifest.getBytes());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,"[Registry_sendNodeManifest] Failed to send Node Manifest to Node ID " + messagingNodeID + " " + e.getStackTrace());
            e.printStackTrace();
            return false;
        }
        LOGGER.log(Level.INFO,"[Registry_sendNodeManifest] Node Manifest successfully sent to Node ID " + messagingNodeID);
        return true;
    }

    /**
     * Returns serverIP and serverPort information of a particular node ID
     *
     * @param hopNodeID
     * @return concatenated String of serverIP and serverPort
     */
    private String getHopNodeInfo(int hopNodeID) {
        String ipPortString = null;
        for (Map.Entry<String, Integer> entry : ipIDMap.entrySet()) {
            if (entry.getValue() == hopNodeID) {
                ipPortString = entry.getKey();
                break;
            }
        }
        return ipPortString;
    }

    private NodeInfo createNodeInfo(int hopNodeID, int distance) {
        String ipAndPort = getHopNodeInfo(hopNodeID);
        String[] arr = ipAndPort.split(Constants.JOINING_CHARACTER);
        return new NodeInfo(hopNodeID, arr[0].getBytes(), Integer.parseInt(arr[1]), distance);
    }

    /**
     * Generates RegistryRequestsTaskInitiate message and sends out to all registered Messaging Nodes
     * @param noOfMessages
     */
    public void initiateTasks(int noOfMessages) {

        RegistryRequestsTaskInitiate taskInitiate = (RegistryRequestsTaskInitiate) eventFactory.createEventByType(Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
        taskInitiate.setNoOfMessages(noOfMessages);

        //initialize/re-initialize the list
        setTrafficSummaries(new ArrayList<>());

        // send to all existing messages
        for(Map.Entry<Integer, Socket> entry : idSocketMap.entrySet())
        {
            try {
                TCPConnection.TCPSender sender = new TCPConnection.TCPSender(entry.getValue());
                sender.sendData(taskInitiate.getBytes());
                LOGGER.log(Level.INFO,"[Registry_initiateTasks] Task Initiate successfully sent to Node ID " + entry.getKey());
            } catch (IOException e) {
                LOGGER.log(Level.ERROR,"[Registry_initiateTasks] Failed to send Task Initiate to Node ID " + entry.getKey() + " " + e.getStackTrace());
                e.printStackTrace();
            }
        }

        // once sending task initiate is completed, a thread is started to keep track of
        // OVERLAY_NODE_REPORTS_TASK_FINISHED messages, inorder to send out REGISTRY_REQUESTS_TRAFFIC_SUMMARY
        // to all the messaging nodes, once all of them finishes reporting
        Thread taskFinishedMsgThread = new Thread(this);
        taskFinishedMsgThread.start();
    }

    // List downs information about each messaging node
    public void listMessagingNode() {

        StatisticsCollectorAndDisplay.printMessagingNodeLise(ipIDMap.entrySet());
    }

    public void listRoutingTables() {

        StatisticsCollectorAndDisplay.printRoutingTables(routingEntryTreeMap);
    }

    public HashMap<String, Integer> getIpIDMap() {
        return ipIDMap;
    }

    public void setIpIDMap(HashMap<String, Integer> ipIDMap) {
        this.ipIDMap = ipIDMap;
    }

    public synchronized List<OverlayNodeReportsTrafficSummary> getTrafficSummaries() {
        return trafficSummaries;
    }

    public synchronized void setTrafficSummaries(List<OverlayNodeReportsTrafficSummary> trafficSummaries) {
        this.trafficSummaries = trafficSummaries;
    }
}
