package cs455.overlay.wireformats;

/*
    This class is build to avoid hard coding different message
    types, in different parts of the code
 */
public final class Protocol {

    private Protocol() {
        //restrict instantiation
    }

    public static final int OVERLAY_NODE_SENDS_REGISTRATION = 1;
    public static final int REGISTRY_REPORTS_REGISTRATION_STATUS = 2;
    public static final int OVERLAY_NODE_SENDS_DEREGISTRATION = 3;
    public static final int REGISTRY_REPORTS_DEREGISTRATION_STATUS = 4;
    public static final int REGISTRY_SENDS_NODE_MANIFEST = 5;
    public static final int NODE_REPORTS_OVERLAY_SETUP_STATUS = 6;
    public static final int REGISTRY_REQUESTS_TASK_INITIATE = 7;
    public static final int OVERLAY_NODE_REPORTS_TASK_FINISHED = 8;
    public static final int REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 9;
    public static final int OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 10;

}
