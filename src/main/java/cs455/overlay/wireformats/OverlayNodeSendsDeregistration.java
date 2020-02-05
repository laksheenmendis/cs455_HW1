package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration implements Event {

    private char messageType;
    private byte[] ipAddress;
    private int portNumber;
    private int assignedID;

    public OverlayNodeSendsDeregistration() {
        this.messageType = getType();
    }

    public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readChar();
        int lengthOfIP = din.readInt();
        this.ipAddress = new byte[lengthOfIP];
        din.readFully(this.ipAddress, 0, lengthOfIP);
        this.portNumber = din.readInt();
        this.assignedID = din.readInt();
        baInputStream.close();
        din.close();
    }

    @Override
    public char getType() {
        return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeChar( messageType );
        int ipAddressLength = ipAddress.length;
        dout.writeInt(ipAddressLength);
        dout.write(ipAddress, 0, ipAddressLength);
        dout.writeInt(portNumber);
        dout.writeInt(assignedID);
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

    public int getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }
}
