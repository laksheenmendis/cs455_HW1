package cs455.overlay.wireformats;

public class OverlayNodeReportsTaskFinished implements Event {

    int messageType;

    public OverlayNodeReportsTaskFinished(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
