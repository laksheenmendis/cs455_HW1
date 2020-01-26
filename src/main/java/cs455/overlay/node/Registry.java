package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Registry implements Node {

    private static Registry instance;
    private int registryPort;
    //this map stores details of the IP addresses and ports of messaging nodes
    private HashMap<String, Integer> ipPortMap = new HashMap<>();
    //this map stores details of the IP addresses and assigned ID of messaging nodes
    private HashMap<String, Integer> ipIDMap = new HashMap<>();

    private Registry() {

    }

    public static Registry getInstance()
    {
        if(instance == null)
        {
            instance = new Registry();
        }
        return instance;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    public static void main(String[] args) {
        Registry ins = Registry.getInstance();

        TCPServerThread serverThread = new TCPServerThread();
        try {
            ServerSocket ss = serverThread.getServerSocket();
            ins.registryPort = ss.getLocalPort();
            System.out.println("Server is listening on port "+ ins.registryPort);

            Socket socket = ss.accept();
            System.out.println("Messaging Node connected");

            TCPConnection.TCPReceiverThread receiver = new TCPConnection.TCPReceiverThread(socket);
            receiver.run();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                //TODO
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                //TODO
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                //TODO
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                //TODO
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                //TODO
                break;
            //TODO overlay node send data

        }
    }
}
