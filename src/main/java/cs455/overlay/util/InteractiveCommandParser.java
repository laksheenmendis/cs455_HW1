package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    This is used to parse the commands entered into the Registry and MessagingNode
 */
public class InteractiveCommandParser implements Runnable{

    public static final String CMD_LIST_MESSAGING_NODES = "list-messaging-nodes";
    public static final String CMD_SETUP_OVERLAY = "setup-overlay";
    public static final String CMD_START = "start";
    public static final String CMD_PRINT_COUNTERS_AND_DIAGNOSTICS = "print-counters-and-diagnostics";
    public static final String CMD_EXIT_OVERLAY = "exit-overlay";
    public static final String CMD_LIST_ROUTING_TABLES = "list-routing-tables";
    private Node node;
    static Logger LOGGER = Logger.getLogger(InteractiveCommandParser.class.getName());

    public InteractiveCommandParser(Node node) {
        this.node = node;
    }

    @Override
    public void run() {

        readAndProcess();
    }

    public void readAndProcess() {

        InputStreamReader inputStreamReader;
        BufferedReader br;

        while (true) {
            inputStreamReader = new InputStreamReader(System.in);
            br = new BufferedReader(inputStreamReader);

            try {
                String command = br.readLine();
                String[] inArr = command.split(" ");

                // Registry commands
                if ( inArr[0].equals(CMD_LIST_MESSAGING_NODES) && node instanceof Registry) {
                    //information about the messaging nodes (hostname, port-number, and node ID) being listed.
                    // Information for each messaging node should be listed on a separate line
                } else if (inArr[0].equals(CMD_SETUP_OVERLAY) && node instanceof Registry) {       //registry setting up the overlay
                    //sending every messaging node the REGISTRY_SENDS_NODE_MANIFEST message

                    int routingTableEntries = Integer.parseInt(inArr[1]);


                } else if ( inArr[0].equals(CMD_LIST_ROUTING_TABLES) && node instanceof Registry) {
                    //information about the computed routing tables for each node in the overlay. Each messaging node’s
                    // information should be well separated (i.e., have 3-4 blank lines between node listings) and should
                    // include the node’s IP address, portnum, and logical-ID
                } else if ( inArr[0].equals(CMD_START) && node instanceof Registry) {
                    //results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all
                    //nodes within the overlay
                    int noOfMessages = Integer.parseInt(inArr[1]);
                }
                // Messaging Node commands
                else if ( inArr[0].equals(CMD_PRINT_COUNTERS_AND_DIAGNOSTICS) && node instanceof MessagingNode) {
                    //information (to the console using System.out) about the number of messages that have been sent,
                    // received, and relayed along with the sums for the messages that have been sent from and received at the node
                } else if ( inArr[0].equals(CMD_EXIT_OVERLAY) && node instanceof MessagingNode) {
                    //allows a messaging node to exit the overlay. The messaging node should first send a deregistration message
                    //to the registry and await a response before exiting and terminating the process
                    MessagingNode messagingNode = (MessagingNode)node;
                    messagingNode.sendDeregisterEvent();
                }
                else
                {
                    LOGGER.info("[InteractiveCommandParser_readAndProcess] Invalid command");
                }


            } catch (IOException e) {
                System.out.print(e.getStackTrace());
            }
        }
    }

}
