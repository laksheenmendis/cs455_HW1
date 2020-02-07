package cs455.overlay.routing;

import java.util.HashMap;

public class RoutingTable {

    //(key,value) -> (nodeID, routingEntry)
    public static HashMap<Integer, RoutingEntry> routingMap = new HashMap<>();

    public RoutingTable() {
        //TODO setup the routing table here
    }

    public HashMap<Integer, RoutingEntry> getRoutingMap() {
        return routingMap;
    }

    public void setRoutingMap(HashMap<Integer, RoutingEntry> routingMap) {
        this.routingMap = routingMap;
    }

    /**
     * Searches the routingMap and finds the most suitable node to forward the message
     * @param destinationID
     * @return
     */
    public static int getForwardingRoutingNode(int destinationID)
    {
        int forwardingNode = -1;
        for(int nodeID : routingMap.keySet())
        {
            if(forwardingNode == -1 || ! (forwardingNode <= destinationID) || (forwardingNode < nodeID && nodeID <= destinationID))
            {
                forwardingNode = nodeID;
            }
        }
        return routingMap.get(forwardingNode).getNodeID();
    }
}
