package cs455.overlay.wireformats;

import cs455.overlay.util.Converter;
import java.io.*;

public class OverlayNodeSendsData implements Event {

    private int messageType;
    private int destinationID;
    private int sourceID;
    private int payload;
    private int[] disseminationTrace; // comprising nodeIDs that the packet traversed through

    public OverlayNodeSendsData() {
        this.messageType = getType();
    }

    public OverlayNodeSendsData(byte [] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.messageType = din.readInt();
        this.destinationID = din.readInt();
        this.sourceID = din.readInt();
        this.payload = din.readInt();
        int dissArrLength = din.readInt();
        int byteArrLength = din.readInt();
        byte [] byteArr = new byte[byteArrLength];
        din.readFully(byteArr, 0, byteArrLength);
        this.disseminationTrace = Converter.byteArrayToIntegerArray(dissArrLength, byteArr);
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DATA;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte [] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeInt(messageType);
        dout.writeInt(destinationID);
        dout.writeInt(sourceID);
        dout.writeInt(payload);
        byte [] array = Converter.integerArrayToByteArray(disseminationTrace);
        int byteArrLength = array.length;
        dout.writeInt(disseminationTrace.length);
        dout.writeInt(byteArrLength);
        dout.write(array, 0, byteArrLength);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(int destinationID) {
        this.destinationID = destinationID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public int[] getDisseminationTrace() {
        return disseminationTrace;
    }

    public void setDisseminationTrace(int[] disseminationTrace) {
        this.disseminationTrace = disseminationTrace;
    }
}
