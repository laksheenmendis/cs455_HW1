package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTaskInitiate implements Event {

    private char messageType;
    private int noOfMessages;

    public RegistryRequestsTaskInitiate() {
        this.messageType = getType();
    }

    public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readChar();
        noOfMessages = din.readInt();
        baInputStream.close();
        din.close();
    }

    @Override
    public char getType() {
        return Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    }

    @Override
    public byte[] getBytes()throws IOException {
        byte [] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.writeChar(messageType);
        dout.writeInt(noOfMessages);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getNoOfMessages() {
        return noOfMessages;
    }

    public void setNoOfMessages(int noOfMessages) {
        this.noOfMessages = noOfMessages;
    }
}
