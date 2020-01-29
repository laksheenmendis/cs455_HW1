package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistryReportsRegistrationStatus implements Event {

    int messageType;
    int successStatus; //assigned ID if successful, otherwise -1
    byte lengthOfInfo;
    byte[] infoString;

    public RegistryReportsRegistrationStatus(int messageType) {
        this.messageType = messageType;
    }

    public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
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
        return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }
}
