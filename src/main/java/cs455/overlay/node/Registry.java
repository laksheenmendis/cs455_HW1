package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Registry implements Node {

    //this map stores details of the IP addresses and ports of messaging nodes
    private HashMap<String, Integer> ipPortMap;
    //this map stores details of the IP addresses and assigned ID of messaging nodes
    private HashMap<String, Integer> ipIDMap;

    private static final int ID_UPPER_LIMIT = 128;
    private static final int ID_LOWER_LIMIT = 0;
    private static EventFactory eventFactory;
    private TCPServerThread serverThread;
    private final String JOINING_CHARACTER = "#";
    static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Registry.class.getName());

    private Registry() {
        ipPortMap = new HashMap<>();
        ipIDMap = new HashMap<>();
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
            LOGGER.info("Couldn't start Registry on " + PORT_NUMBER);
            e.printStackTrace();
        }

    }

    @Override
    public void onEvent(Event event, Socket socket) throws IOException {

        char messageType = event.getType();
        LOGGER.info("[Registry_onEvent] received " + messageType) ;

        switch (messageType) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerMessagingNode((OverlayNodeSendsRegistration) event, socket);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                readOverlaySetupStatus((NodeReportsOverlaySetupStatus) event, socket);
                //TODO
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterMessagingNode((OverlayNodeSendsDeregistration) event, socket);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                readTaskFinishedMsg((OverlayNodeReportsTaskFinished) event, socket);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                readOverlayTrafficSummary((OverlayNodeReportsTrafficSummary)event, socket);
                break;
            //TODO overlay node send data

        }
    }

    private void readOverlayTrafficSummary(OverlayNodeReportsTrafficSummary event, Socket socket) {

    }

    private void readOverlaySetupStatus(NodeReportsOverlaySetupStatus event, Socket socket) {

    }

    private void readTaskFinishedMsg(OverlayNodeReportsTaskFinished event, Socket socket) {

    }

    private void deregisterMessagingNode(OverlayNodeSendsDeregistration event, Socket socket) {

        //TODO make sure to do the checks

    }

    private void registerMessagingNode(OverlayNodeSendsRegistration event, Socket socket) {

        LOGGER.info("[Registry_registerMessagingNode] started ");

        String serverIPAddress = new String(event.getIpAddress());
        int serverPort = event.getPortNumber();

        int ID = getID( serverIPAddress, serverPort );

        try {
            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
            RegistryReportsRegistrationStatus registrationResponse = getRegistrationResponse( ID );

            try {
                sender.sendData(registrationResponse.getBytes());
            } catch (IOException e) {
                ipIDMap.remove(serverIPAddress + JOINING_CHARACTER + serverPort);
                TCPConnectionsCache.removeEntry(socket);
                LOGGER.info("[Registry_registerMessagingNode] Couldn't communicate to Messaging Node with ID " + ID +
                        "\nHence entries removed");
                e.printStackTrace();
            }
        } catch (IOException e) {
            LOGGER.info("[Registry]_[registerMessagingNode] Error in creating TCP Sender " + e.getMessage());
        }
    }

    /**
     * Generate a number between 0 and 127 and check the map until an unassigned ID is found
     * Further, insert the server IPAddress and ID into ipIDMap
     * @return
     */
    private synchronized int getID(String serverIpAddress, int serverPort) {
        int ID;
        do {
            int random = (int) Math.random();
            ID = random % (ID_UPPER_LIMIT - ID_LOWER_LIMIT) + ID_LOWER_LIMIT;
        }
        while (ipIDMap.containsValue(ID));

        ipIDMap.put(serverIpAddress + JOINING_CHARACTER + serverPort, ID);
        return ID;
    }

    private RegistryReportsRegistrationStatus getRegistrationResponse(int ID) {
        RegistryReportsRegistrationStatus registrationStatus = (RegistryReportsRegistrationStatus) eventFactory.createEventByType(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
        registrationStatus.setSuccessStatus(ID);
        registrationStatus.setInfoString(Protocol.REGISTRATION_SUCCESSFULL);
        LOGGER.info("[Registry_getRegistrationResponse] Registration Status generated ");
        return registrationStatus;
    }

}
