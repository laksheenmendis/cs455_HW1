package cs455.overlay.wireformats;

import java.io.IOException;

public interface Event {

    char getType();

    byte[] getBytes()  throws IOException;

}
