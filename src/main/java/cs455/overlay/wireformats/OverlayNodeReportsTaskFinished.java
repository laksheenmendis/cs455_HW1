package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event {

    private int messageType;
    private byte[] ipAddress;
    private int portNumber;
    private int nodeID;

    public OverlayNodeReportsTaskFinished() {
        this.messageType = getType();
    }

    public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.messageType = din.readInt();
        int arrLength = din.readInt();
        din.readFully(this.ipAddress, 0, arrLength);
        this.portNumber = din.readInt();
        this.nodeID = din.readInt();
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte [] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt(messageType);
        int length = ipAddress.length;
        dout.writeInt(length);
        dout.write(ipAddress, 0, length);
        dout.writeInt(portNumber);
        dout.writeInt(nodeID);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
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

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }
}
