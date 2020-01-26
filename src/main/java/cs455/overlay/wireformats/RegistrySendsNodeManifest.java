package cs455.overlay.wireformats;

public class RegistrySendsNodeManifest implements Event {



    @Override
    public int getType() {
        return Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
