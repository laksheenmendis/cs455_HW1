package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection {

    public static class TCPSender{
        private Socket socket;
        private DataOutputStream dout;

        public TCPSender(Socket socket) throws IOException {
            this.socket = socket;
            dout = new DataOutputStream(socket.getOutputStream());
        }

        public void sendData(byte[] dataToSend) throws IOException {
            int dataLength = dataToSend.length;
            dout.writeInt(dataLength);
            dout.write(dataToSend, 0, dataLength);
            dout.flush();
        }
    }

    public static class TCPReceiverThread extends Thread{
        private Socket socket;
        private DataInputStream din;

        public TCPReceiverThread(Socket socket) throws IOException {
            this.socket = socket;
            din = new DataInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            int dataLength;
            while (socket != null) {
                try {
                    dataLength = din.readInt();
                    byte[] data = new byte[dataLength];
                    din.readFully(data, 0, dataLength);

                    for (byte b:data) {
                        // convert byte into character
                        char c = (char)b;
                        // print the character
                        System.out.print(c);
                    }

                } catch (SocketException se) {
                    System.out.println(se.getMessage());
                    break;
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage()) ;
                    break;
                }
            }
        }

    }
}
