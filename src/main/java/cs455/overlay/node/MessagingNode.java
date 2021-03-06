package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.Converter;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class MessagingNode implements Node {

    private Socket socket;
    private static EventFactory eventFactory;
    private int sendTracker = 0;        //number of data packets that were sent by that node
    private volatile int receiveTracker = 0;     //number of packets that were received
    private int relayTracker = 0;       //number of packets that a node relays
    private long sendSummation = 0L;    //sums the values of the random numbers that are sent
    private long receiveSummation = 0L; //sums values of the payloads that are received
    private TCPServerThread serverThread;
    private static Logger LOGGER = Logger.getLogger(MessagingNode.class.getName());
    private int ID; //assigned ID by Registry
    private Thread serverT;
    private int[] allNodeIDs;
    // this map is used to store connections to other messaging nodes
    private Map<Integer, Socket> nodeSocketMap;
    private Thread registryReceiver;

    public static void main(String[] args) {

        String REGISTRY_HOST;
        int REGITRY_PORT;

        try {

            if (args.length != 2) {
                LOGGER.info("[MessagingNode_main] 2 arguments should required, " + args.length + "argument(s) found.");
                return;
            }

            REGISTRY_HOST = args[0];
            REGITRY_PORT = Integer.parseInt(args[1]);

            MessagingNode messagingNode = new MessagingNode();
            messagingNode.socket = new Socket(REGISTRY_HOST, REGITRY_PORT);
            LOGGER.info("[MessagingNode_main] connected to Registry");

            eventFactory = EventFactory.getInstance();

            try {
                messagingNode.serverThread = new TCPServerThread(-1, messagingNode);

                messagingNode.serverT = new Thread(messagingNode.serverThread);
                messagingNode.serverT.start();

                InteractiveCommandParser cmdParser = new InteractiveCommandParser(messagingNode);
                Thread cmdThread = new Thread(cmdParser);
                cmdThread.start();

                messagingNode.sendRegisterEvent();

                // receiver which is connected with Registry
                TCPConnection.TCPReceiverThread receiverFromRegistry = new TCPConnection.TCPReceiverThread(messagingNode.socket, messagingNode);
                messagingNode.registryReceiver = new Thread(receiverFromRegistry);
                messagingNode.registryReceiver.start();
            } catch (IOException e1) {
                LOGGER.info("[MessagingNode_main] Server not started " + e1.getStackTrace());
                e1.printStackTrace();
            }
        } catch (IOException e) {
            LOGGER.info("[MessagingNode_main] couldn't connect to Registry " + e.getStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event, Socket socket) {

        int messageType = event.getType();

        switch (messageType) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                readRegisterResponse((RegistryReportsRegistrationStatus) event);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                readDeRegisterResponse((RegistryReportsDeregistrationStatus) event);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                readNodeManifestFromRegistry((RegistrySendsNodeManifest) event);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                taskInitiate((RegistryRequestsTaskInitiate) event);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                sendTrafficSummaryResponse();
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                receiveOrRelay((OverlayNodeSendsData) event);
                break;
        }
    }

    /**
     * This method checks the incoming event and decides next action
     *  1. Checks whether the destination is this
     *  2. If not it needs to be forwarded after altering dissemination trace
     * @param event
     */
    private void receiveOrRelay(OverlayNodeSendsData event) {
        LOGGER.log(Level.INFO,"[MessagingNode_receiveOrRelay] Started");
        if( event.getDestinationID() == this.ID )
        {
            this.updateReceiveTrackers(event.getPayload());
            LOGGER.log(Level.INFO, "THIS IS THE DESTINATION");
        }
        else
        {
            int [] arr = event.getDisseminationTrace();
            LOGGER.log(Level.INFO,"[MessagingNode_receiveOrRelay] DISSEMINATION TRACE length " + arr.length);
            for(int i = 0; i < arr.length; i++ )
            {
                LOGGER.log(Level.ERROR,arr[i] + ",");
            }

            int [] arrNew = new int[arr.length + 1];

            arrNew = Arrays.copyOf(arr, arrNew.length);
            arrNew[arrNew.length-1] = this.ID;
            event.setDisseminationTrace(arrNew);


            this.updateRelayTracker(event);

            int forwardingID = RoutingTable.getForwardingRoutingNode(event.getDestinationID());

            Socket socket = this.nodeSocketMap.get(forwardingID);
            if(socket != null)
            {
                try {
                    TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
                    sender.sendData(event.getBytes());
                    LOGGER.log(Level.INFO,"[MessagingNode_receiveOrRelay] Relayed message. Destination is " + event.getDestinationID() + ", forwarding to :" + forwardingID);
                    LOGGER.log(Level.INFO, "[MessagingNode_receiveOrRelay] LENGTH OF THE EVENT IS "+ event.getBytes().length );
                } catch (IOException e) {
                    LOGGER.log(Level.ERROR,"[MessagingNode_receiveOrRelay] Node "+ this.ID + " failed to relay message" + e.getStackTrace());
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void updateReceiveTrackers(int receivedPayload)
    {
        this.receiveTracker += 1;
        this.receiveSummation += receivedPayload;
    }

    private synchronized void updateRelayTracker(OverlayNodeSendsData event)
    {
        this.relayTracker += 1;
    }

    /**
     * This method will send out the traffic summary response to messaging node
     */
    private synchronized void sendTrafficSummaryResponse() {
        OverlayNodeReportsTrafficSummary trafficSummary = (OverlayNodeReportsTrafficSummary) eventFactory.createEventByType(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
        trafficSummary.setAssignedNodeID(this.ID);
        trafficSummary.setTrafficDetails(this.sendTracker, this.relayTracker, this.receiveTracker, this.sendSummation, this.receiveSummation);

        sendEvent(trafficSummary);
        this.resetCounters(); //after successful sending, we need to reset the counters
    }

    /**
     * This method will send out the required number of messages to randomly picked
     * Messaging Nodes (except itself)
     *
     * @param event
     */
    private void taskInitiate(RegistryRequestsTaskInitiate event) {

        LOGGER.log(Level.INFO,"[MessagingNode_taskInitiate] Started at Node " + this.ID);
        for( int i = 0; i < event.getNoOfMessages(); i++ )
        {
            int destinationID = getRandomDestinationID();
            if( destinationID == -1 )
            {
                LOGGER.log(Level.ERROR,"[MessagingNode_taskInitiate] Couldn't find a valid destination ID");
            }
            else
            {
                OverlayNodeSendsData nodeSendsData = (OverlayNodeSendsData) eventFactory.createEventByType(Protocol.OVERLAY_NODE_SENDS_DATA);
                nodeSendsData.setDestinationID(destinationID);
                nodeSendsData.setSourceID(this.ID);
                int payload = new Random().nextInt();
                nodeSendsData.setPayload(payload);
                nodeSendsData.setDisseminationTrace(new int[0]);

                int forwardingNodeID = RoutingTable.getForwardingRoutingNode(destinationID);
                LOGGER.log(Level.INFO,"[MessagingNode_taskInitiate] In task initiate, destination is "+ destinationID + ", forwarding to " + forwardingNodeID);

                TCPConnection.TCPSender sender;
                Socket savedSocket = this.nodeSocketMap.get(forwardingNodeID);
                try {
                    LOGGER.log(Level.INFO, "[MessagingNode_taskInitiate] LENGTH OF THE EVENT IS "+ nodeSendsData.getBytes().length);
                    sender = new TCPConnection.TCPSender(savedSocket);
                    sender.sendData(nodeSendsData.getBytes());
                    this.sendTracker += 1;
                    this.sendSummation += payload;
                } catch (IOException e) {
                    LOGGER.log(Level.ERROR,"[MessagingNode_taskInitiate] Sending data from " + this.ID + " failed " + e.getStackTrace());
                    e.printStackTrace();
                }
            }
        }
        LOGGER.log(Level.INFO,"[MessagingNode_taskInitiate] Node " + this.ID + " sent " + this.sendTracker + " number of messages");

        // once messages are sent, need to report it to the registry
        OverlayNodeReportsTaskFinished taskFinished = (OverlayNodeReportsTaskFinished) eventFactory.createEventByType(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
        taskFinished.setIpAddress(this.socket.getLocalAddress().getHostAddress().getBytes());
        taskFinished.setNodeID(this.ID);
        taskFinished.setPortNumber(this.serverThread.getServerPort());

        sendEvent(taskFinished);
    }

    /**
     * This method should pick a random ID (except itself)
     * @return randomly picked destination ID
     */
    private int getRandomDestinationID(){

        if( this.allNodeIDs.length > 1 )
        {
            int randomIndex ;
            do{
                Random generator = new Random();
                randomIndex = generator.nextInt(this.allNodeIDs.length);
            }
            while (this.allNodeIDs[randomIndex] == this.ID);

            LOGGER.log(Level.INFO,"[MessagingNode_getRandomDestinationID] Random destination created at Node "+this.ID);
            return this.allNodeIDs[randomIndex];
        }
        else
        {
            LOGGER.log(Level.ERROR,"[MessagingNode_getRandomDestinationID] There isn't sufficient number of nodes in the overlay");
            return -1;
        }

    }

    /**
     * This includes various tasks carried out by Messaging Node when it receives RegistrySendsNodeManifest
     * Step 1: Routing table is setup
     * Step 2: Initiate connections with other relevant messaging nodes
     * Step 3: Call sendOverlaySetupStatus
     * @param event
     */
    private void readNodeManifestFromRegistry(RegistrySendsNodeManifest event) {

        LOGGER.log(Level.INFO,"[MessagingNode_readNodeManifestFromRegistry] Routing table creation started at Node : " + this.ID);
        int noOfRoutingEntries = event.getRoutingTableSize();
        List<Integer> hopsList = new ArrayList<>(noOfRoutingEntries);

        nodeSocketMap = new HashMap<>();

        // calculate hop distances
        for(int r=0; r<noOfRoutingEntries; r++)
        {
            hopsList.add((int)Math.pow(2,r));
        }

        boolean successStatus;

        StringBuilder sb = new StringBuilder();
        RegistrySendsNodeManifest.NodeInfo[] nodeInfos = event.getNodeInfoList();
        for(int j=0; j< noOfRoutingEntries; j++)
        {
            nodeInfos[j].set_distance(hopsList.get(j));
//            RoutingEntry routingEntry = nodeInfoToRoutingEntry.apply(nodeInfos[j]);
            RoutingEntry routingEntry = Converter.nodeInfoToRoutingEntry.apply(nodeInfos[j]);
            successStatus = initiateConnectionWithNode(nodeInfos[j], routingEntry);

            if(!successStatus)
            {
                sb.append("Connection from " + this.ID +" to " + nodeInfos[j].getNodeID() + " unsuccessfull\n");
                LOGGER.log(Level.ERROR, "Connection from " + this.ID +" to " + nodeInfos[j].getNodeID() + " unsuccessfull\n");
            }
        }
        LOGGER.log(Level.INFO,"[MessagingNode_readNodeManifestFromRegistry] Routing table creation finished at Node : " + this.ID);

        this.allNodeIDs = event.getNodeIDs();

        sendOverlaySetupStatus( sb.toString() );

    }

    /**
     * This message handles sending overlay setup status to the registry
     * @param failureMsg
     */
    private void sendOverlaySetupStatus(String failureMsg) {

        NodeReportsOverlaySetupStatus overlaySetupStatus = (NodeReportsOverlaySetupStatus) eventFactory.createEventByType(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);

        if( failureMsg.isEmpty() )   //no failures
        {
            overlaySetupStatus.setSuccessStatus(this.ID);
            overlaySetupStatus.setInfoString(Constants.SETUP_OVERLAY_SUCCESSFULL);
        }
        else // failures occured
        {
            overlaySetupStatus.setSuccessStatus(-1);
            overlaySetupStatus.setInfoString(failureMsg);
        }

        sendEvent(overlaySetupStatus);
    }

    /**
     * This method is used to create new connections with other messaging nodes
     * as specified in Node Manifest message
     * @param nodeInfo
     * @param entry
     * @return
     */
    private boolean initiateConnectionWithNode(RegistrySendsNodeManifest.NodeInfo nodeInfo, RoutingEntry entry) {

        boolean successful = false;
        Socket socket;
        try {
            socket = new Socket(new String(nodeInfo.getIpAddress()), nodeInfo.getPortNumber());
            this.nodeSocketMap.put(nodeInfo.getNodeID(), socket);
            RoutingTable.routingMap.put(nodeInfo.getNodeID(), entry);
            successful = true;
            LOGGER.log(Level.INFO,"[MessagingNode_initiateConnectionWithNode] Messaging Node " + this.ID + " connected to Node "+ nodeInfo.getNodeID());
        } catch (Exception e) {
            LOGGER.log(Level.ERROR,"[MessagingNode_initiateConnectionWithNode] Messaging Node " + this.ID + " failed to connect to Node "+ nodeInfo.getNodeID() +" "+ e.getStackTrace());
            e.printStackTrace();
        }
        finally {
            return successful;
        }
    }

    private void readDeRegisterResponse(RegistryReportsDeregistrationStatus event) {
        int status = event.getSuccessStatus();
        if( status != -1 && status == this.ID)
        {
            LOGGER.log(Level.INFO,"[MessagingNode_readDeRegisterResponse] Node " + this.ID + " Deregistration successful");
            System.exit(0);
        }
        else // error in deregistration
        {
            LOGGER.log(Level.ERROR,"[MessagingNode_readDeRegisterResponse] Node " + this.ID + " " + event.getInfoString());
        }
    }

    private void readRegisterResponse(RegistryReportsRegistrationStatus event) {
        int status = event.getSuccessStatus();
        if( status != -1)
        {
            LOGGER.log(Level.INFO,"[MessagingNode_readRegisterResponse] Assigned ID to messaging node is " + status);
            this.ID = status;
        }
        else // error in registration
        {
            LOGGER.log(Level.ERROR,"[MessagingNode_readRegisterResponse] Registry returned -1 " +event.getInfoString());
            try {
                this.socket.close();
                this.serverThread.terminateServer();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR,"[MessagingNode_readRegisterResponse] " + e.getStackTrace());
                e.printStackTrace();
            }
        }
    }

    /**
     * Populates the register event to be sent to the registry
     * @return
     */
    private OverlayNodeSendsRegistration getRegisterEvent()
    {
        OverlayNodeSendsRegistration e1 = (OverlayNodeSendsRegistration) eventFactory.createEventByType(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
        e1.setIpAddress(this.socket.getLocalAddress().getHostAddress().getBytes());
        e1.setPortNumber(this.serverThread.getServerPort());
        LOGGER.log(Level.INFO,"Register server address " + this.socket.getLocalAddress().getHostAddress() + " and port " + e1.getPortNumber());
        return e1;
    }

    /**
     * Handles sending of events
     * @param event
     */
    private void sendEvent(Event event)
    {
        try {
            TCPConnection.TCPSender senderToRegistry = new TCPConnection.TCPSender(this.socket);
            senderToRegistry.sendData(event.getBytes());
            LOGGER.log(Level.INFO,"[MessagingNode_sendEvent] " + event.getClass().getSimpleName() +" sent");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,"[MessagingNode_sendEvent] " + event.getClass().getSimpleName() + " Request sending failed " + e.getStackTrace());
            e.printStackTrace();
        }
    }

    private void sendRegisterEvent() {
        Event registerEvent = getRegisterEvent();
        sendEvent(registerEvent);
    }

    public void sendDeregisterEvent() {
        Event deregisterEvent = getDeregisterEvent();
        sendEvent(deregisterEvent);
    }

    /**
     * Populates the deregister event to be sent to the registry
     * @return
     */
    private OverlayNodeSendsDeregistration getDeregisterEvent()
    {
        OverlayNodeSendsDeregistration e1 = (OverlayNodeSendsDeregistration) eventFactory.createEventByType(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
        e1.setIpAddress(this.socket.getLocalAddress().getHostAddress().getBytes());
        e1.setAssignedID(this.ID);
        e1.setPortNumber(this.serverThread.getServerPort());
        return e1;
    }

    /**
     * Reset counters associated with traffic
     */
    private void resetCounters()
    {
        sendTracker = 0;
        receiveTracker = 0;
        relayTracker = 0;
        sendSummation = 0L;
        receiveSummation = 0L;
    }

    /**
     * This method calls another function which handles printing of statistics
     */
    public synchronized void printCountersAndDiagnostics() {
        StatisticsCollectorAndDisplay.printMNCounters(sendTracker, receiveTracker, relayTracker, sendSummation, receiveSummation);

    }
}
