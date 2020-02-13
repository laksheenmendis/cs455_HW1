package cs455.overlay.transport;

import cs455.overlay.util.Constants;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPConnectionsCache {

    private static Map<String, Socket> connectionsCache = new HashMap<>();

    public static void add( Socket socket)
    {
        connectionsCache.put( generateKey(socket), socket);
    }

    public static Socket retreive(String ipAddress)
    {
        return connectionsCache.get(ipAddress);
    }

    public static void removeEntry(Socket socket) {
        connectionsCache.remove( generateKey(socket));
    }

    private static String generateKey(Socket socket)
    {
       return new String(socket.getInetAddress().getAddress()) + Constants.JOINING_CHARACTER +  socket.getPort();
    }

    public static boolean inCache(Socket socket) {

        return connectionsCache.containsKey(generateKey(socket));
    }
}
