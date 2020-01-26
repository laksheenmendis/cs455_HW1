package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration implements Event {

    private byte messageType;
    private byte lengthOfIP;
    private byte[] ipAddress;
    private int portNumber;

    public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        lengthOfIP = din.readByte();
        ipAddress = new byte[lengthOfIP];
        din.readFully(ipAddress);
        portNumber = din.readInt();
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
