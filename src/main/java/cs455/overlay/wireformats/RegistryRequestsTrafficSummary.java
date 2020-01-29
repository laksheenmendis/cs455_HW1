package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTrafficSummary implements Event{

    int messageType;

    public RegistryRequestsTrafficSummary(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }
}
