package cs455.overlay.wireformats;

import cs455.overlay.util.Converter;
import java.io.*;

public class RegistrySendsNodeManifest implements Event {

    private char messageType;
    private int routingTableSize;
    private NodeInfo[] nodeInfoList;
    private int noOfNodeIDs;
    private int [] nodeIDs;

    public RegistrySendsNodeManifest() {
        this.messageType = getType();
    }

    public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException
    {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readChar();
        this.routingTableSize = din.readInt();
        this.nodeInfoList = new NodeInfo[routingTableSize];
        for(int i = 0; i < routingTableSize; ++i)
        {
            int byteArrLength = din.readInt();
            byte [] mByteArray = new byte[byteArrLength];
            din.readFully(mByteArray, 0, byteArrLength);
            this.nodeInfoList[i] = new NodeInfo(mByteArray);
        }
        this.noOfNodeIDs = din.readInt();
        int byteArrLength = din.readInt();
        byte [] byteArr = new byte[byteArrLength];
        din.readFully(byteArr, 0, byteArrLength);
        this.nodeIDs = Converter.byteArrayToIntegerArray(noOfNodeIDs, byteArr);
        baInputStream.close();
        din.close();
    }

    @Override
    public char getType() {
        return Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte [] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeChar(messageType);
        dout.writeInt(routingTableSize);
        for(int i = 0; i<routingTableSize; ++i)
        {
            byte[] nodeInfoBytes = nodeInfoList[i].getBytes();
            dout.writeInt(nodeInfoBytes.length);
            dout.write(nodeInfoBytes, 0, nodeInfoBytes.length);
        }
        dout.writeInt(noOfNodeIDs);
        byte [] array = Converter.integerArrayToByteArray(nodeIDs);
        int byteArrLength = array.length;
        dout.writeInt(byteArrLength);
        dout.write(array, 0, byteArrLength);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getRoutingTableSize() {
        return routingTableSize;
    }

    public void setRoutingTableSize(int routingTableSize) {
        this.routingTableSize = routingTableSize;
    }

    public NodeInfo[] getNodeInfoList() {
        return nodeInfoList;
    }

    public void setNodeInfoList(NodeInfo[] nodeInfoList) {
        this.nodeInfoList = nodeInfoList;
    }

    public int getNoOfNodeIDs() {
        return noOfNodeIDs;
    }

    public void setNoOfNodeIDs(int noOfNodeIDs) {
        this.noOfNodeIDs = noOfNodeIDs;
    }

    public int[] getNodeIDs() {
        return nodeIDs;
    }

    public void setNodeIDs(int[] nodeIDs) {
        this.nodeIDs = nodeIDs;
    }

    public static class NodeInfo
    {
        private int nodeID;
        private byte[] ipAddress;
        private int portNumber;
        private int _distance; //this is not sent in the RegistrySendsNodeManifest

        public NodeInfo() {
        }

        public NodeInfo(int nodeID, byte[] ipAddress, int portNumber) {
            this.nodeID = nodeID;
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
        }

        public NodeInfo(byte [] marshalledBytes) throws IOException{

            ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            this.nodeID = din.readInt();
            int arrLength = din.readInt();
            this.ipAddress = new byte[arrLength];
            din.readFully(ipAddress, 0, arrLength);
            this.portNumber = din.readInt();
            din.close();
            baInputStream.close();
        }

        public byte[] getBytes() throws IOException
        {
            byte [] marshalledBytes = null;
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
            dout.writeInt(nodeID);
            int arrLength = ipAddress.length;
            dout.writeInt(arrLength);
            dout.write(ipAddress, 0, arrLength);
            dout.writeInt(portNumber);
            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            dout.close();
            baOutputStream.close();
            return marshalledBytes;
        }

        public int getNodeID() {
            return nodeID;
        }

        public void setNodeID(int nodeID) {
            this.nodeID = nodeID;
        }

        public byte[] getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(byte[] ipAddress) {
            this.ipAddress = ipAddress;
        }

        public int getPortNumber() {
            return portNumber;
        }

        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber;
        }

        public int get_distance() {
            return _distance;
        }

        public void set_distance(int _distance) {
            this._distance = _distance;
        }

        public String getIPAddress()
        {
            return ipAddress[0] + "." + ipAddress[1] + "." + ipAddress[2] + "." + ipAddress[3];
        }
    }
}
