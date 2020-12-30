Assignment requirements/description can be found at https://drive.google.com/file/d/1IbJhLwu2wAAZkJ73Dq5geA9eXBrrz_Q-/view?usp=sharing


Routing Packets Within a Structured Peer-to-Peer (P2P) Network Overlay

Steps to follow:

1. Start Registry at a port.
       java cs455.overlay.node.Registry <portnum>

2. In the 4th line of the start-nodes.sh change the <host-name> and <portnum> fields,
    to the host name of the machine and port which the Registry was started

3. Run start-nodes.sh

4. Immediately, messaging nodes will connect with the registry and register themselves.

5. Once all messaging nodes have registered themselves, at Registry, type command 'list-messaging-nodes'
    to list the registered messaging nodes. A sample output is provided below.

    --------------------------------------------------
    |Hostname             |Port-number    |Node ID   |
    --------------------------------------------------
    |        129.82.44.144|          49152|        90|
    --------------------------------------------------
    |        129.82.44.138|          49152|        43|
    --------------------------------------------------
    |        129.82.44.137|          49152|        25|
    --------------------------------------------------
    |        129.82.44.147|          49152|        42|
    --------------------------------------------------
    |        129.82.44.153|          49152|        32|
    --------------------------------------------------
    |        129.82.44.143|          49152|        71|
    --------------------------------------------------
    |        129.82.44.136|          49152|        84|
    --------------------------------------------------
    |        129.82.44.156|          49152|        33|
    --------------------------------------------------
    |        129.82.44.162|          49152|        86|
    --------------------------------------------------
    |        129.82.44.152|          49152|        78|
    --------------------------------------------------

6. Once above command list downs all messaging nodes, type 'setup-overlay' with the
    appropriate number of routing table size.

7. In a while, if required, you can view the routing tables setup at each messaging node with the command (in Registry),
    'list-routing-tables'. A sample output is provided below.

    Routing table of messaging node 33

    -------------------------------------------------------------
    |Distance  |Hostname             |Port-number    |Node ID   |
    -------------------------------------------------------------
    |         1|        129.82.44.147|          49152|        42|
    -------------------------------------------------------------
    |         2|        129.82.44.138|          49152|        43|
    -------------------------------------------------------------
    |         4|        129.82.44.152|          49152|        78|
    -------------------------------------------------------------
    |         8|        129.82.44.137|          49152|        25|
    -------------------------------------------------------------




    Routing table of messaging node 42

    -------------------------------------------------------------
    |Distance  |Hostname             |Port-number    |Node ID   |
    -------------------------------------------------------------
    |         1|        129.82.44.138|          49152|        43|
    -------------------------------------------------------------
    |         2|        129.82.44.143|          49152|        71|
    -------------------------------------------------------------
    |         4|        129.82.44.136|          49152|        84|
    -------------------------------------------------------------
    |         8|        129.82.44.153|          49152|        32|
    -------------------------------------------------------------

8. Once all messaging nodes successfully setup the overlay, Registry will print out the following banner.
            ####################################################################
            #                                                                  #
            #               REGISTRY NOW READY TO INITIATE TASKS               #
            #                                                                  #
            ####################################################################

9. Now you can specify 'start' along with the number of messages (in Registry), to initiate the tasks at each messaging node.

10. Once messaging nodes finishes sending/receiving/relaying messages, they will notify the registry.

11. While messages are being sent around, 'print-counters-and-diagnostics' command could be used to see statistics
    at each messaging node. A sample output is provided below.


    Number of messages sent : 25,000
    Number of messages received : 25,222
    Number of messages relayed : 22,251
    Summation of messages sent : -151,647,355,962
    Summation of messages received : -248,831,146,215

12. Next, Registry will automatically print out the traffic summaries in the console.
    A sample output is provided below.

------------------------------------------------------------------------------------------------------------
|          |Packets Send|Packets Received |Packets Relayed |Sum Values Sent      |Sum Values Received      |
------------------------------------------------------------------------------------------------------------
|   Node 32|      25,000|           25,173|          13,700|      198,392,555,482|            8,186,711,717|
------------------------------------------------------------------------------------------------------------
|   Node 84|      25,000|           24,903|          14,068|       87,300,106,981|          501,798,426,160|
------------------------------------------------------------------------------------------------------------
|   Node 33|      25,000|           24,597|          16,442|       17,038,463,206|           48,616,515,471|
------------------------------------------------------------------------------------------------------------
|   Node 71|      25,000|           24,906|          16,839|     -105,984,981,090|          -65,090,165,660|
------------------------------------------------------------------------------------------------------------
|   Node 25|      25,000|           25,222|          22,251|     -151,647,355,962|         -248,831,146,215|
------------------------------------------------------------------------------------------------------------
|   Node 42|      25,000|           24,857|          16,688|     -248,810,537,784|         -408,041,170,639|
------------------------------------------------------------------------------------------------------------
|   Node 86|      25,000|           25,212|          11,221|      109,000,408,966|         -133,411,721,648|
------------------------------------------------------------------------------------------------------------
|   Node 43|      25,000|           24,919|          19,442|       78,337,151,587|           98,001,436,121|
------------------------------------------------------------------------------------------------------------
|   Node 90|      25,000|           25,231|           5,680|     -137,590,207,565|         -202,381,131,725|
------------------------------------------------------------------------------------------------------------
|   Node 78|      25,000|           24,980|          33,381|      -12,611,397,421|          234,576,452,818|
------------------------------------------------------------------------------------------------------------
|       Sum|     250,000|          250,000|         169,712|     -166,575,793,600|         -166,575,793,600|
------------------------------------------------------------------------------------------------------------

13. Once again, you can specify 'start' along with the number of messages (in Registry), to initiate the tasks at each messaging node.
    This can be performed any number of times.

14. Deregister messaging nodes from the overlay using 'exit-overlay' command.



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
