package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsDeregistrationStatus implements Event {

    private char messageType;
    private int successStatus; //assigned ID if successful, otherwise -1
    private String infoString;

    public RegistryReportsDeregistrationStatus() {
        this.messageType = getType();
    }

    public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readChar();
        this.successStatus = din.readInt();
        int lengthOfInfo = din.readInt();
        byte [] infoStringBytes = new byte[lengthOfInfo];
        din.readFully(infoStringBytes);
        this.infoString = new String(infoStringBytes);
        baInputStream.close();
        din.close();
    }

    @Override
    public char getType() {
        return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeChar( messageType );
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
