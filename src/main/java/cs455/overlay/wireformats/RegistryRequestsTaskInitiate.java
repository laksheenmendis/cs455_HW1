package cs455.overlay.wireformats;

public class RegistryRequestsTaskInitiate implements Event {
    @Override
    public int getType() {
        return Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
