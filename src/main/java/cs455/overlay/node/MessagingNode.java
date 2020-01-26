package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessagingNode implements Node {



    public static void main(String[] args) {

        try {
            //TODO this is wrong
            int registryPort = Registry.getInstance().getRegistryPort();

            String s = "Hello from Messaging Node";

            Socket socket = new Socket("localhost", registryPort);

            TCPConnection.TCPSender sender = new TCPConnection.TCPSender(socket);
            sender.sendData(s.getBytes());

            socket.close();

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
}
