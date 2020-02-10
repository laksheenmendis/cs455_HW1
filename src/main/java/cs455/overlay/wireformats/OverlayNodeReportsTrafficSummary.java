package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event {

    private int messageType;
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

        this.messageType = din.readInt();
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
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt(messageType);
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

    public int getAssignedNodeID() {
        return assignedNodeID;
    }

    public void setAssignedNodeID(int assignedNodeID) {
        this.assignedNodeID = assignedNodeID;
    }

    public void setTrafficDetails(int pckSent, int pckRlyd, int pckRcvd, long sumDataSent, long sumDataRcvd)
    {
        this.totalPacketsSent = pckSent;
        this.totalPacketsRelayed = pckRlyd;
        this.totalPacketsReceived = pckRcvd;
        this.sumOfPacketDataSent = sumDataSent;
        this.sumOfPacketDataReceived = sumDataRcvd;
    }

    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public void setTotalPacketsSent(int totalPacketsSent) {
        this.totalPacketsSent = totalPacketsSent;
    }

    public int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }

    public void setTotalPacketsRelayed(int totalPacketsRelayed) {
        this.totalPacketsRelayed = totalPacketsRelayed;
    }

    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public void setTotalPacketsReceived(int totalPacketsReceived) {
        this.totalPacketsReceived = totalPacketsReceived;
    }

    public long getSumOfPacketDataSent() {
        return sumOfPacketDataSent;
    }

    public void setSumOfPacketDataSent(long sumOfPacketDataSent) {
        this.sumOfPacketDataSent = sumOfPacketDataSent;
    }

    public long getSumOfPacketDataReceived() {
        return sumOfPacketDataReceived;
    }

    public void setSumOfPacketDataReceived(long sumOfPacketDataReceived) {
        this.sumOfPacketDataReceived = sumOfPacketDataReceived;
    }
}
