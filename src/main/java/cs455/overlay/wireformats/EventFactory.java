package cs455.overlay.wireformats;

/*
    A singleton class which generates Events
 */
public class EventFactory {

    private EventFactory instance;

    private EventFactory EventFactory() {
        if(instance == null)
        {
            instance = new EventFactory();
        }
        return instance;
    }

    public EventFactory getInstance()
    {
        return EventFactory();
    }
}
