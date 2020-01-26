package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistryReportsDeregistrationStatus implements Event {

    byte messageType;
    int successStatus; //assigned ID if successful, otherwise -1
    byte lengthOfInfo;
    byte[] infoString;

    public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        successStatus = din.readInt();
        lengthOfInfo = din.readByte();
        infoString = new byte[lengthOfInfo];
        din.readFully(infoString);
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
