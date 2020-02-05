package cs455.overlay;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class log4jExample{

    /* Get actual class name to be printed on */
    static Logger log = Logger.getLogger(log4jExample.class.getName());
    public static final char OVERLAY_NODE_SENDS_REGISTRATION = 'a';

    public static void main(String[] args)throws IOException,SQLException{
        log.debug("Hello this is a debug message");
        log.info("Hello this is an info message");
//        System.out.println("This char value is " + (byte)OVERLAY_NODE_SENDS_REGISTRATION);

    }
}
