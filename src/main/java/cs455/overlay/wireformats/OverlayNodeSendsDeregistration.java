package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration implements Event {

    private int messageType;
    private byte[] ipAddress;
    private int portNumber;
    private int assignedID;

    public OverlayNodeSendsDeregistration(int messageType) {
        this.messageType = messageType;
    }

    public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readInt();
        int lengthOfIP = din.readInt();
        ipAddress = new byte[lengthOfIP];
        din.readFully(ipAddress);
        portNumber = din.readInt();
        assignedID = din.readInt();
        din.close();
        baInputStream.close();
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt( messageType );
        int ipAddressLength = ipAddress.length;
        dout.writeInt(ipAddressLength);
        dout.write(ipAddress);
        dout.writeInt(portNumber);
        dout.writeInt(assignedID);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

}
