package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

/*
    This is used to parse the commands entered into the Registry and MessagingNode
 */
public class InteractiveCommandParser implements Runnable{

    private static final String CMD_LIST_MESSAGING_NODES = "list-messaging-nodes";
    private static final String CMD_SETUP_OVERLAY = "setup-overlay";
    private static final String CMD_START = "start";
    private static final String CMD_PRINT_COUNTERS_AND_DIAGNOSTICS = "print-counters-and-diagnostics";
    private static final String CMD_EXIT_OVERLAY = "exit-overlay";
    private static final String CMD_LIST_ROUTING_TABLES = "list-routing-tables";
    private Node node;
    private static Logger LOGGER = Logger.getLogger(InteractiveCommandParser.class.getName());

    public InteractiveCommandParser(Node node) {
        this.node = node;
    }

    @Override
    public void run() {

        readAndProcess();
    }

    private void readAndProcess() {

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
                    Registry registry = (Registry) node;
                    registry.listMessagingNode();
                } else if (inArr[0].equals(CMD_SETUP_OVERLAY) && node instanceof Registry) {

                    //sending every messaging node the REGISTRY_SENDS_NODE_MANIFEST message
                    if( inArr.length == 1 )
                    {
                        System.out.println("[InteractiveCommandParser_readAndProcess] Invalid command, another argument required");
                        continue;
                    }
                    int noOfRoutingEntries = Integer.parseInt(inArr[1]);
                    Registry registry = (Registry)node;
                    registry.setupOverlay(noOfRoutingEntries);

                } else if ( inArr[0].equals(CMD_LIST_ROUTING_TABLES) && node instanceof Registry) {

                    //information about the computed routing tables for each node in the overlay.
                    Registry registry = (Registry) node;
                    registry.listRoutingTables();

                } else if ( inArr[0].equals(CMD_START) && node instanceof Registry) {

                    //results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all nodes within the overlay
                    if( inArr.length == 1 )
                    {
                        System.out.println("[InteractiveCommandParser_readAndProcess] Invalid command, another argument required");
                        continue;
                    }
                    int noOfMessages = Integer.parseInt(inArr[1]);
                    Registry registry = (Registry)node;
                    registry.initiateTasks(noOfMessages);
                }
                // Messaging Node commands
                else if ( inArr[0].equals(CMD_PRINT_COUNTERS_AND_DIAGNOSTICS) && node instanceof MessagingNode) {

                    MessagingNode messagingNode = (MessagingNode) node;
                    messagingNode.printCountersAndDiagnostics();
                } else if ( inArr[0].equals(CMD_EXIT_OVERLAY) && node instanceof MessagingNode) {

                    //allows a messaging node to exit the overlay
                    MessagingNode messagingNode = (MessagingNode)node;
                    messagingNode.sendDeregisterEvent();
                }
                else
                {
                    LOGGER.info("[InteractiveCommandParser_readAndProcess] Invalid command");
                }
            } catch (SocketException se)
            {
                LOGGER.info("[InteractiveCommandParser_readAndProcess] " + se.getStackTrace());
                se.printStackTrace();
                break;
            } catch (IOException e) {
                LOGGER.info("[InteractiveCommandParser_readAndProcess] " + e.getStackTrace());
                e.printStackTrace();
                break;
            }
        }
    }

}
