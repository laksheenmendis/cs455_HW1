package cs455.overlay.wireformats;

/*
    This class is build to avoid hard coding different message
    types, in different parts of the code
 */
public final class Protocol {

    private Protocol() {
        //restrict instantiation
    }

    public static final char OVERLAY_NODE_SENDS_REGISTRATION = 'a';
    public static final char REGISTRY_REPORTS_REGISTRATION_STATUS = 'b';
    public static final char OVERLAY_NODE_SENDS_DEREGISTRATION = 'c';
    public static final char REGISTRY_REPORTS_DEREGISTRATION_STATUS = 'd';
    public static final char REGISTRY_SENDS_NODE_MANIFEST = 'e';
    public static final char NODE_REPORTS_OVERLAY_SETUP_STATUS = 'f';
    public static final char REGISTRY_REQUESTS_TASK_INITIATE = 'g';
    public static final char OVERLAY_NODE_REPORTS_TASK_FINISHED = 'h';
    public static final char REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 'i';
    public static final char OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 'j';
    public static final char OVERLAY_NODE_SENDS_DATA = 'k';

    public static final String REGISTRATION_SUCCESSFULL = "Registration Successfull";
    public static final String REGISTRATION_FAILED = "Registration Failed";

}
