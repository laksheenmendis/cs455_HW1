package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTaskInitiate implements Event {

    int messageType;

    public RegistryRequestsTaskInitiate(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    }

    @Override
    public byte[] getBytes()throws IOException {
        return new byte[0];
    }
}
