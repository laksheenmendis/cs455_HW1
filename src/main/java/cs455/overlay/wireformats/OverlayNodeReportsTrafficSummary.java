package cs455.overlay.wireformats;

public class OverlayNodeReportsTrafficSummary implements Event {

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
