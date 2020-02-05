package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

    private static EventFactory instance;

    private static EventFactory EventFactory() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public static EventFactory getInstance() {
        return EventFactory();
    }

    public Event createEvent(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        char messageType = din.readChar();
        Event event = null;

        switch (messageType) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                event = new OverlayNodeSendsRegistration(marshalledBytes);
                break;
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                event = new RegistryReportsRegistrationStatus(marshalledBytes);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                event = new OverlayNodeSendsDeregistration(marshalledBytes);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                event = new RegistryReportsDeregistrationStatus(marshalledBytes);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                event = new RegistrySendsNodeManifest(marshalledBytes);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                event = new NodeReportsOverlaySetupStatus(marshalledBytes);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                event = new RegistryRequestsTaskInitiate();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                event = new OverlayNodeReportsTaskFinished(marshalledBytes);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                event = new RegistryRequestsTrafficSummary();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                event = new OverlayNodeReportsTrafficSummary(marshalledBytes);
                break;
            default:
                return null;
        }

        return event;
    }

    /**
     * This method creates new events based on the message type
     *
     * @param messageType
     * @return
     */
    public Event createEventByType(char messageType) {
        Event event = null;

        switch (messageType) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                event = new OverlayNodeSendsRegistration();
                break;
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                event = new RegistryReportsRegistrationStatus();
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                event = new OverlayNodeSendsDeregistration();
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                event = new RegistryReportsDeregistrationStatus();
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                event = new RegistrySendsNodeManifest();
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                event = new NodeReportsOverlaySetupStatus();
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                event = new RegistryRequestsTaskInitiate();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                event = new OverlayNodeReportsTaskFinished();
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                event = new RegistryRequestsTrafficSummary();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                event = new OverlayNodeReportsTrafficSummary();
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                event = new OverlayNodeSendsData();
                break;
            default:
                event = null;
        }
        return event;
    }


}
