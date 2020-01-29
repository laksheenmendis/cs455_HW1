package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsDeregistrationStatus implements Event {

    int messageType;
    int successStatus; //assigned ID if successful, otherwise -1
    String infoString;

    public RegistryReportsDeregistrationStatus(int messageType) {
        this.messageType = messageType;
    }

    public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readInt();
        successStatus = din.readInt();
        int lengthOfInfo = din.readInt();
        byte [] infoStringBytes = new byte[lengthOfInfo];
        din.readFully(infoStringBytes);
        infoString = new String(infoStringBytes);
        din.close();
        baInputStream.close();
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt( messageType );
        dout.writeInt( successStatus );
        byte[] infoStringBytes = infoString.getBytes();
        int infoLength = infoStringBytes.length;
        dout.writeInt(infoLength);
        dout.write(infoStringBytes);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
