package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MessagingNode implements Node, Runnable {

    private Socket socket;
    private static EventFactory eventFactory;
    private int sendTracker = 0;        //number of data packets that were sent by that node
    private int receiveTracker = 0;     //number of packets that were received
    private int relayTracker = 0;       //number of packets that a node relays
    private long sendSummation = 0L;    //sums the values of the random numbers that are sent
    private long receiveSummation = 0L; //sums values of the payloads that are received
    private RoutingTable routingTable;
    private int port;
    private TCPServerThread serverThread;
    private static Logger LOGGER = Logger.getLogger(MessagingNode.class.getName());
    private int ID; //assigned ID by Registry
    private Thread serverT;
    private int [] allNodeIDs;
    // this map is used to store connections to other messaging nodes
    private Map<Integer, Socket> nodeSocketMap;
//    private static MessagingNode messagingNode;

    public static void main(String[] args) {

        String REGISTRY_HOST;
        int REGITRY_PORT;

        try {

            if( args.length != 2 )
            {
                LOGGER.info("[MessagingNode_main] 2 arguments should required, "+ args.length +"argument(s) found.");
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

                try
                {
                    messagingNode.sendRegisterEvent();

                    TCPConnection.TCPReceiverThread receiverFromRegistry = new TCPConnection.TCPReceiverThread(messagingNode.socket, messagingNode);
                    Thread registryReceiver = new Thread(receiverFromRegistry);
                    registryReceiver.start();
                }
                catch (UnknownHostException eh)
                {
                    LOGGER.info("[MessagingNode_main] Registration Failed " + eh.getMessage());
                    eh.printStackTrace();
                    messagingNode.serverT.stop();
                }
            }
            catch (IOException e1)
            {
                LOGGER.info("[MessagingNode_main] Server not started " + e1.getMessage());
                e1.printStackTrace();
            }
        } catch (IOException e) {
            LOGGER.info("[MessagingNode_main] couldn't connect to Registry " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


    }

    @Override
    public void onEvent(Event event, Socket socket) {

        char messageType = event.getType();

        switch (messageType){
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                readRegisterResponse((RegistryReportsRegistrationStatus)event);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                readDeRegisterResponse((RegistryReportsDeregistrationStatus)event);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                readNodeManifestFromRegistry((RegistrySendsNodeManifest)event);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                readTaskInitiateRequest((RegistryRequestsTaskInitiate)event);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                readTrafficSummaryRequest((RegistryRequestsTrafficSummary)event);
                break;

        }
    }

    private void readTrafficSummaryRequest(RegistryRequestsTrafficSummary event) {
    }

    private void readTaskInitiateRequest(RegistryRequestsTaskInitiate event) {
    }

    /**
     * This includes various tasks carried out by Messaging Node when it receives RegistrySendsNodeManifest
     * Step 1: Routing table is setup
     * Step 2: Initiate connections with other relevant messaging nodes
     * Step 3: Call sendOverlaySetupStatus
     * @param event
     */
    private void readNodeManifestFromRegistry(RegistrySendsNodeManifest event) {

        LOGGER.info("[MessagingNode_readNodeManifestFromRegistry] Routing table creation started at Node : " + this.ID);
        int noOfRoutingEntries = event.getRoutingTableSize();
        List<Integer> hopsList = new ArrayList<>(noOfRoutingEntries);

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
            RoutingEntry routingEntry = nodeInfoToRoutingEntry.apply(nodeInfos[j]);
            successStatus = initiateConnectionWithNode(nodeInfos[j], routingEntry);

            if(!successStatus)
            {
                sb.append(this.ID +" to " + nodeInfos[j].getNodeID() + " unsuccessfull\n");
            }
        }
        LOGGER.info("[MessagingNode_readNodeManifestFromRegistry] Routing table creation finished at Node : " + this.ID);

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

        try {
            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(this.socket);
            sender.sendData(overlaySetupStatus.getBytes());
            LOGGER.info("[MessagingNode_sendOverlaySetupStatus] Overlay setup status message sent from Node ID "+ this.ID);
        } catch (IOException e) {
            LOGGER.info("[MessagingNode_sendOverlaySetupStatus] couldn't send the overlay setup status from node ID " + this.ID + e.getMessage());
            e.printStackTrace();
        }
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
            LOGGER.info("[MessagingNode_initiateConnectionWithNode] Messaging Node " + this.ID + " connected to Node "+ nodeInfo.getNodeID());
        } catch (Exception e) {
            LOGGER.info("[MessagingNode_initiateConnectionWithNode] Messaging Node " + this.ID + " failed to connect to Node "+ nodeInfo.getNodeID() +" "+ e.getMessage());
            e.printStackTrace();
        }
        finally {
            return successful;
        }
    }

    /**
     * This function is used to map information from NodeInfo from Manifest message to a routing entry
     */
    private Function<RegistrySendsNodeManifest.NodeInfo, RoutingEntry> nodeInfoToRoutingEntry= (RegistrySendsNodeManifest.NodeInfo info)->
    {
        RoutingEntry routingEntry = new RoutingEntry();
        routingEntry.setDistance(info.get_distance());
        routingEntry.setNodeID(info.getNodeID());
        routingEntry.setIpAddress(info.getIpAddress());
        routingEntry.setPortNumber(info.getPortNumber());
        return routingEntry;
    };

    private void readDeRegisterResponse(RegistryReportsDeregistrationStatus event) {
        int status = event.getSuccessStatus();
        if( status != -1 && status == this.ID)
        {
            LOGGER.info("[MessagingNode_readDeRegisterResponse] Deregistration successful");
            try {
                this.socket.close();
                this.serverThread.terminateServer();
            } catch (IOException e) {
                LOGGER.info("[MessagingNode_readDeRegisterResponse] " + e.getMessage());
                e.printStackTrace();
                this.serverT.stop();
            }
        }
        else // error in deregistration
        {
            LOGGER.info("[MessagingNode_readDeRegisterResponse] Deregistration unsuccessful " + event.getInfoString());
        }
    }

    private void readRegisterResponse(RegistryReportsRegistrationStatus event) {
        int status = event.getSuccessStatus();
        if( status != -1)
        {
            LOGGER.info("[MessagingNode_readRegisterResponse] Assigned ID to messaging node is " + status);
            this.ID = status;
        }
        else // error in registration
        {
            LOGGER.info("[MessagingNode_readRegisterResponse] Registry returned -1 " +event.getInfoString());
            try {
                this.socket.close();
            } catch (IOException e) {
                LOGGER.info("[MessagingNode_readRegisterResponse] " + e.getMessage());
                e.printStackTrace();
                this.serverT.stop();
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
        e1.setIpAddress(this.serverThread.getServerAddress());
        e1.setPortNumber(this.serverThread.getServerPort());
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
            LOGGER.info("[MessagingNode_sendEvent] " + event.getClass().getSimpleName() +" Request sent");
        } catch (IOException e) {
            LOGGER.info("[MessagingNode_sendEvent] " + event.getClass().getSimpleName() + " Request sending failed " + e.getMessage());
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
        e1.setIpAddress(this.serverThread.getServerAddress());
        e1.setAssignedID(this.ID);
        e1.setPortNumber(this.serverThread.getServerPort());
        return e1;
    }

    /**
     * Reset counters associated with traffic
     */
    // TODO call once node sends OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
    private void resetCounters()
    {
        sendTracker = 0;
        receiveTracker = 0;
        relayTracker = 0;
        sendSummation = 0L;
        receiveSummation = 0L;
    }
}
