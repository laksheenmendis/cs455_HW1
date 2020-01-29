package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServerThread {

    private ServerSocket serverSocket;
    private final int  STARTING_PORT_NUMBER = 49152;
    private final int ENDING_PORT_NUMBER = 65535;

    /**
     * Finds an available port in the range and creates a Server Socket
     * which is bound to the provided port
     * @return
     * @throws IOException
     */
    public ServerSocket getServerSocket() throws IOException
    {
        for (int port=STARTING_PORT_NUMBER; port <= ENDING_PORT_NUMBER; port++) {
            try {
                return new ServerSocket(port);
            } catch (IOException ex) {
                continue; // try next port
            }
        }
        throw new IOException("No free port found");
    }

}
