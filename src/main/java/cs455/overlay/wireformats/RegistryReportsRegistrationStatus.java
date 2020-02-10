package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsRegistrationStatus implements Event {

    private int messageType;
    private int successStatus; //assigned ID if successful, otherwise -1
    private String infoString;

    public RegistryReportsRegistrationStatus() {
        this.messageType = getType();
    }

    public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();
        this.successStatus = din.readInt();
        int lengthOfInfo = din.readInt();
        byte [] infoStringBytes = new byte[lengthOfInfo];
        din.readFully(infoStringBytes, 0, lengthOfInfo);
        this.infoString = infoStringBytes.toString();
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt(messageType);
        dout.writeInt(successStatus);
        byte[] infoStringBytes = infoString.getBytes();
        int length = infoStringBytes.length;
        dout.writeInt(length);
        dout.write(infoStringBytes,0,length);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }


    public int getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(int successStatus) {
        this.successStatus = successStatus;
    }

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }
}
