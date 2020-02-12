package cs455.overlay.transport;

import cs455.overlay.node.Node;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable {

    private ServerSocket serverSocket;
    private final int STARTING_PORT_NUMBER = 49152;
    private final int ENDING_PORT_NUMBER = 65535;
    private static Logger LOGGER = Logger.getLogger(TCPServerThread.class.getName());
    private TCPConnection.TCPSender sender;
    private Node node;

    public TCPServerThread(int port, Node node) throws IOException {
        this.node = node;
        if (port != -1) { //Registry starting server
            this.serverSocket = new ServerSocket(port);
        } else {    //Messaging Node starting server
            this.serverSocket = getServerSocket();
        }
        LOGGER.log(Priority.INFO,"Listening on port " + serverSocket.getLocalPort());
    }

    @Override
    public void run() {

        while (true) {

            try {
                // incoming connections from other Messaging Nodes in the overlay
                Socket socket = serverSocket.accept();

                //save the connection details
                TCPConnectionsCache.add(socket);

                // there will be receiverThreads equal to the number of routingTable Entries at each Messaging Node
                TCPConnection.TCPReceiverThread receiver = new TCPConnection.TCPReceiverThread(socket, this.node);
                Thread thread = new Thread(receiver);
                thread.start();

            } catch (EOFException ef) {
                LOGGER.log(Priority.ERROR,"[TCPServerThread_run] EOFException at " + node.getClass().getSimpleName() + ef.getStackTrace());
                ef.printStackTrace();
            } catch (IOException e) {
                LOGGER.log(Priority.ERROR,"[TCPServerThread_run] Unable to accept incoming connections " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds an available port in the range and creates a Server Socket
     * which is bound to the provided port
     *
     * @return A new Server Socket
     * @throws IOException When no free port is found
     */
    private ServerSocket getServerSocket() throws IOException {
        for (int port = STARTING_PORT_NUMBER; port <= ENDING_PORT_NUMBER; port++) {
            try {
                return new ServerSocket(port);
            } catch (IOException ex) {
                continue; // try next port
            }
        }
        throw new IOException("No free port found");
    }

    public int getServerPort()
    {
        return serverSocket.getLocalPort();
    }

    public void terminateServer() throws IOException
    {
        serverSocket.close();
    }
}
