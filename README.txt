Description of files included (src/main/java);

cs455.overlay.node.Node:
An interface which includes the method onEvent. Two primary classes in this project (MessagingNode and Registry)
are implemented using this interface.

cs455.overlay.node.MessagingNode:
Includes all the functionality of a Node in the system/overlay. Sending registration/de-registration requests,
create connections to other messaging nodes, sending out messages, reporting traffic summaries
to Registry and much more.

cs455.overlay.node.Registry:
Includes to the functionality which allows messaging nodes to register/deregister themselves,
assign random identifiers (between 0-127) to messaging nodes, enables the construction of the
overlay by populating the routing table at each messaging nodes and much more.

cs455.overlay.routing.RoutingTable:
HashMap which dictates the connections that a messaging node initiates with other messaging nodes in the system.
And it contains of Routing Entries. When the destination is given, it searches the map and finds the most suitable
node to forward a message.

cs455.overlay.routing.RoutingEntry:
Stores the distance, ip address, port number and assigned ID of a particular messaging node.

cs455.overlay.transport.TCPConnection:
Includes two inner classes, TCPSender and TCPReceiverThread. Sending and receiving of messages between
messaging nodes and registry is handled by these classes.

cs455.overlay.transport.TCPConnectionsCache:
Maintains a static map which includes key-value pairs of ip address#port as the key and socket as the value.

cs455.overlay.transport.TCPServerThread:
Handles creation of the server socket and includes the functionality to find an available port
in a particular range.

cs455.overlay.util.Constants:
Declaration of frequently used constants.

cs455.overlay.util.Converter:
Handles conversions from integer array to byte array and vice versa. Further it includes a Function to
convert Node information into a Routing Entry.

cs455.overlay.util.InteractiveCommandParser:
Used to parse the commands entered into the Registry and MessagingNode.

cs455.overlay.util.StatisticsCollectorAndDisplay:
Collects various statistics and display in console.

cs455.overlay.util.TrafficSummaryTracker:
Keeps track of the number of traffic summaries received from messaging nodes and display the
overall summary, when all traffic summaries have arrived.

cs455.overlay.util.OverlaySetupSummaryTracker:
Keeps track of the number of NODE_REPORTS_OVERLAY_SETUP_STATUS messages sent by messaging nodes
and then print out that the registry is ready to initiate tasks

cs455.overlay.wireformats.Protocol:
Defines message types which are available in the network.

cs455.overlay.wireformats.Event:
Interface with 2 methods, getType and getBytes. This is implemented by all the Message type classes.

cs455.overlay.wireformats.EventFactory:
Handles creation of different events/messages.

cs455.overlay.wireformats.OverlayNodeSendsRegistration:
Message type which is being sent by a messaging node to register itself with the registry.

cs455.overlay.wireformats.RegistryReportsRegistrationStatus:
Message type which is being sent by the registry to notify the status of registration.

cs455.overlay.wireformats.OverlayNodeSendsDeregistration:
Message type which is being sent by a messaging node to de-register itself with the registry.

cs455.overlay.wireformats.RegistryReportsDeregistrationStatus:
Message type which is being sent by the registry to notify the status of de-registration.

cs455.overlay.wireformats.RegistrySendsNodeManifest:
Message type which is being sent by the registry to notify each messaging node about their
routing table.

cs455.overlay.wireformats.NodeReportsOverlaySetupStatus:
Message type which is being sent by the messaging node to notify registry, regarding overlay setup
status.

cs455.overlay.wireformats.RegistryRequestsTaskInitiate:
Message type which is being sent by registry to notify messaging nodes to start sending messages.

cs455.overlay.wireformats.OverlayNodeReportsTaskFinished:
Message type which is being sent by the messaging node to report the task status to the registry.

cs455.overlay.wireformats.RegistryRequestsTrafficSummary:
Message type which is being sent by the registry requesting the traffic summary at each messaging node.

cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary:
Message type which is being sent by the messaging node to report traffic summary.

cs455.overlay.wireformats.OverlayNodeSendsData
Message type which is being sent between messaging nodes.

Description of files included (src/main/resources);

log4j.properties :
Includes configuration for log4j in key-value pairs.
Please mark the parent folder (src/main/resources) as a source folder.


Steps to follow:

1. Start Registry at a port

2. Start Messaging Nodes, providing hostname and port of the Registry.

3. At Registry, type command 'list-messaging-nodes' to list the registered messaging nodes
    A sample output is provided below.

4. Once above command list downs all messaging nodes, type 'setup-overlay' with the
    appropriate number of routing table size.

5. In a while, if required, you can view the routing tables at each messaging node with the command,
    'list-routing-tables'. A sample output is provided below.

5. Once all messaging nodes successfully setup the overlay, Registry will print out the following banner.
            ####################################################################
            #                                                                  #
            #               REGISTRY NOW READY TO INITIATE TASKS               #
            #                                                                  #
            ####################################################################

6. Now you can specify 'start' along with the number of messages, to initiate the tasks at each messaging node.

7. Once messaging nodes finishes sending/receiving/relaying messages, they will notify the registry.

8. Next, Registry will automatically print out the traffic summaries in the console.
    A sample output is provided below.

