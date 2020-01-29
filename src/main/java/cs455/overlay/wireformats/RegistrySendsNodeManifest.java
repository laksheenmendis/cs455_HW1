package cs455.overlay.wireformats;

public class RegistrySendsNodeManifest implements Event {

    int messageType;

    public RegistrySendsNodeManifest(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
