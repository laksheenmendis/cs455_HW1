package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event {

    private int messageType;
    private byte[] ipAddress;
    private int portNumber;

    public OverlayNodeSendsRegistration(int messageType) {
        this.messageType = messageType;
    }

    public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readInt();
        int lengthOfIP = din.readInt();
        ipAddress = new byte[lengthOfIP];
        din.readFully(ipAddress);
        portNumber = din.readInt();
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt( messageType );
        int ipAddressLength = ipAddress.length;
        dout.writeInt(ipAddressLength);
        dout.write(ipAddress);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
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
}
