package cs455.overlay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    This is used to parse the commands entered into the Registry and MessagingNode
 */
public class InteractiveCommandParser {

    public static void readAndProcess() {

        InputStreamReader inputStreamReader;
        BufferedReader br;

        while (true) {
            inputStreamReader = new InputStreamReader(System.in);
            br = new BufferedReader(inputStreamReader);

            try {
                String command = br.readLine();

                // Registry commands
                if (command.equals("list-messaging-nodes")) {
                    //information about the messaging nodes (hostname, port-number, and node ID) being listed.
                    // Information for each messaging node should be listed on a separate line
                } else if (command.startsWith("setup-overlay")) {       //registry setting up the overlay
                    //TODO split and get the number of entries in the routing table
                    //sending every messaging node the REGISTRY_SENDS_NODE_MANIFEST message
                } else if (command.equals("list-routing-tables")) {
                    //information about the computed routing tables for each node in the overlay. Each messaging node’s
                    // information should be well separated (i.e., have 3-4 blank lines between node listings) and should
                    // include the node’s IP address, portnum, and logical-ID
                } else if (command.startsWith("start")) {
                    //TODO split and get the number of messages to send out
                    //results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all
                    //nodes within the overlay
                }
                // Messaging Node commands
                else if (command.equals("print-counters-and-diagnostics")) {
                    //information (to the console using System.out) about the number of messages that have been sent,
                    // received, and relayed along with the sums for the messages that have been sent from and received at the node
                } else if (command.equals("exit-overlay")) {
                    //allows a messaging node to exit the overlay. The messaging node should first send a deregistration message
                    //to the registry and await a response before exiting and terminating the process
                }


            } catch (IOException e) {
                System.out.print(e.getStackTrace());
            }
        }
    }

}
