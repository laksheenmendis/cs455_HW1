package cs455.overlay.wireformats;

public class NodeReportsOverlaySetupStatus implements Event {

    int messageType;

    public NodeReportsOverlaySetupStatus(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public int getType() {
        return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
