package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event {

    private char messageType;
    private int assignedNodeID;
    private int totalPacketsSent; // only the ones that were started/initiated by the node
    private int totalPacketsRelayed; //received from a different node and forwarded
    private int totalPacketsReceived; //packets with this node as final destination
    private long sumOfPacketDataSent; //only the ones that were started by the node
    private long sumOfPacketDataReceived; //only packets that had this node as final destination

    public OverlayNodeReportsTrafficSummary() {
        this.messageType = getType();
    }

    public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readChar();
        this.assignedNodeID = din.readInt();
        this.totalPacketsSent = din.readInt();
        this.totalPacketsRelayed = din.readInt();
        this.totalPacketsReceived = din.readInt();
        this.sumOfPacketDataSent = din.readLong();
        this.sumOfPacketDataReceived = din.readLong();
        baInputStream.close();
        din.close();
    }

    @Override
    public char getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeChar(messageType);
        dout.writeInt(assignedNodeID);
        dout.writeInt(totalPacketsSent);
        dout.writeInt(totalPacketsRelayed);
        dout.writeInt(totalPacketsReceived);
        dout.writeLong(sumOfPacketDataSent);
        dout.writeLong(sumOfPacketDataReceived);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
