package cs455.overlay.wireformats;

import java.io.*;

public class NodeReportsOverlaySetupStatus implements Event {

    private int messageType;
    private int successStatus;
    private String infoString;

    public NodeReportsOverlaySetupStatus() {
        this.messageType = getType();
    }

    public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.messageType = din.readInt();
        this.successStatus = din.readInt();
        int length = din.readInt();
        byte [] infoBytes = new byte[length];
        din.readFully(infoBytes, 0, length);
        this.infoString = new String(infoBytes);
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt(messageType);
        dout.writeInt(successStatus);
        byte [] infoStringBytes = infoString.getBytes();
        int infoStringLength = infoStringBytes.length;
        dout.writeInt(infoStringLength);
        dout.write(infoStringBytes, 0, infoStringLength);
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
