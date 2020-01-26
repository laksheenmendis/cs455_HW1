package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class OverlayNodeSendsDeregistration implements Event {

    private byte messageType;
    private byte lengthOfIP;
    private byte[] ipAddress;
    private int portNumber;
    private int assignedID;

    public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        lengthOfIP = din.readByte();
        ipAddress = new byte[lengthOfIP];
        din.readFully(ipAddress);
        portNumber = din.readInt();
        assignedID = din.readInt();

    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

}
