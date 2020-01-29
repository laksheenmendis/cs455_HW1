package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessagingNode implements Node {

    private Socket socket;
    private static EventFactory eventFactory;
    private int sendTracker = 0;        //number of data packets that were sent by that node
    private int receiveTracker = 0;     //number of packets that were received
    private int relayTracker = 0;       //number of packets that a node relays
    private long sendSummation = 0L;    //sums the values of the random numbers that are sent
    private long receiveSummation = 0L; //sums values of the payloads that are received

    public static void main(String[] args) {

        String REGISTRY_HOST = "";
        int REGITRY_PORT;

        try {
            REGISTRY_HOST = args[0];
            REGITRY_PORT = Integer.parseInt(args[1]);

            MessagingNode messagingNode = new MessagingNode();
            messagingNode.socket = new Socket(REGISTRY_HOST, REGITRY_PORT);

            eventFactory = EventFactory.getInstance();

            TCPServerThread serverThread = new TCPServerThread();
            ServerSocket ss = serverThread.getServerSocket();

            // TODO generate registration event
//            Event registerEvent = messagingNode.register(ss.getLocalPort());

            String s = "Hello from Messaging Node";

            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(messagingNode.socket);
            sender.sendData(s.getBytes());

            messagingNode.socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onEvent(Event event) {

        switch (event.getType()){
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                //TODO
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                //TODO
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                //TODO
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                //TODO
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                //TODO
                break;

        }
    }

    /**
     * Populates the register event to be sent to the registry
     * @param portNumber
     * @return
     */
    private Event register(int portNumber)
    {
        OverlayNodeSendsRegistration e1 = (OverlayNodeSendsRegistration)eventFactory.createEvent(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
        try {
            InetAddress adr = InetAddress.getLocalHost();
            byte ipAdress[] = adr.getAddress();

            e1.setIpAddress(ipAdress);
            e1.setPortNumber(portNumber);

        } catch (UnknownHostException e) {
            System.out.println(e.getStackTrace());
        }
        return e1;
    }
}
