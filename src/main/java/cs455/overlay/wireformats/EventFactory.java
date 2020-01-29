package cs455.overlay.wireformats;

public class EventFactory {

    private static EventFactory instance;

    private static EventFactory EventFactory() {
        if(instance == null)
        {
            instance = new EventFactory();
        }
        return instance;
    }

    public static EventFactory getInstance()
    {
        return EventFactory();
    }

    /**
     * This method creates new events based on the message type
     * @param messageType
     * @return
     */
    public Event createEvent(int messageType)
    {
        switch (messageType){
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                return new OverlayNodeSendsRegistration( messageType );
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                return new RegistryReportsRegistrationStatus( messageType );
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                return new OverlayNodeSendsDeregistration( messageType );
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                return new RegistryReportsDeregistrationStatus( messageType );
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                return new RegistrySendsNodeManifest( messageType );
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                return new NodeReportsOverlaySetupStatus( messageType );
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                return new RegistryRequestsTaskInitiate( messageType );
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                return new OverlayNodeReportsTaskFinished( messageType );
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                return new RegistryRequestsTrafficSummary( messageType );
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                return new OverlayNodeReportsTrafficSummary( messageType );
            default:
                return null;
        }
    }


}
