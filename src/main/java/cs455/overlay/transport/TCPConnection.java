package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import org.apache.log4j.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection {

    public static class TCPSender{
        private Socket socket;
        private DataOutputStream dout;
        static Logger LOGGER = Logger.getLogger(TCPReceiverThread.class.getName());

        public TCPSender(Socket socket) throws IOException {
            this.socket = socket;
            dout = new DataOutputStream(socket.getOutputStream());
        }

        public void sendData(byte[] dataToSend) throws IOException {
            int dataLength = dataToSend.length;
            dout.writeInt(dataLength);
            dout.write(dataToSend, 0, dataLength);
            dout.flush();
            LOGGER.info("[TCPSender_sendData] data sent ");
        }
    }

    public static class TCPReceiverThread implements Runnable{
        private Socket socket;
        private DataInputStream din;
        private byte[] data;
        private Node node;
        static Logger LOGGER = Logger.getLogger(TCPReceiverThread.class.getName());

        public TCPReceiverThread(Socket socket, Node node) throws IOException {
            this.socket = socket;
            this.din = new DataInputStream(socket.getInputStream());
            this.node = node;
        }

        @Override
        public void run() {
            int dataLength;
            while (socket != null) {
                try {
                    dataLength = din.readInt();
                    data = new byte[dataLength];
                    din.readFully(data, 0, dataLength);

                    Event event = EventFactory.getInstance().createEvent(data);
                    LOGGER.info("[TCPReceiverThread]_[run] " + event.getClass().getSimpleName() + " event received at " + node.getClass().getSimpleName());
                    node.onEvent(event, socket);

                } catch (SocketException se) {
                    LOGGER.info("[TCPReceiverThread]_[run] SocketException " + se.getMessage());
                    se.printStackTrace();
                    break;
                } catch (IOException ioe) {
                    LOGGER.info("[TCPReceiverThread]_[run] IOException " + ioe.getMessage());
                    ioe.printStackTrace();
                    break;
                }
            }
        }
    }
}
