package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection {

    public static class TCPSender{
        private Socket socket;
        private DataOutputStream dout;
        static Logger LOGGER = Logger.getLogger(TCPSender.class.getName());

        public TCPSender(Socket socket) throws IOException {
            this.socket = socket;
            dout = new DataOutputStream(socket.getOutputStream());
        }

        public void sendData(byte[] dataToSend) throws IOException {
            int dataLength = dataToSend.length;
            dout.writeInt(dataLength);
            dout.write(dataToSend, 0, dataLength);
            dout.flush();
            LOGGER.log(Level.INFO,"[TCPSender_sendData] data sent ");
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
                    if (event != null) {
                        System.out.println(event.getClass().getSimpleName() + " Message received");
                    } else {
                        System.out.println("\nEVENT IS NULL\n");
                    }

                    LOGGER.log(Level.INFO,"[TCPReceiverThread_run] " + event.getClass().getSimpleName() + " event received at " + node.getClass().getSimpleName());
                    node.onEvent(event, socket);

                } catch (EOFException ef) {
                    LOGGER.log(Level.ERROR,"[TCPReceiverThread_run] EOFException at " + node.getClass().getSimpleName() + ef.getStackTrace());
                    ef.printStackTrace();
                }catch (SocketException se) {
                    LOGGER.log(Level.ERROR,"[TCPReceiverThread_run] SocketException at " + node.getClass().getSimpleName() + se.getStackTrace());
                    se.printStackTrace();
                } catch (IOException ioe) {
                    LOGGER.log(Level.ERROR,"[TCPReceiverThread_run] IOException " +  node.getClass().getSimpleName() + ioe.getStackTrace());
                    ioe.printStackTrace();
                }
            }
        }
    }
}
