package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        //TODO remove
//        OverlayNodeSendsData o1 = new OverlayNodeSendsData();
//        o1.setDestinationID(14);
//        o1.setSourceID(20);
//        o1.setPayload(435);
//        int [] arr = new int[3];
//        arr[0] = 1;
//        arr[1] = 4;
//        arr[2] = 5;
//        o1.setDisseminationTrace(arr);
//
//        try {
//            byte [] marshalled = o1.getBytes();
//
//            OverlayNodeSendsData o2 = new OverlayNodeSendsData(marshalled);
//
//            System.out.println("Destination :" + o2.getDestinationID());
//            System.out.println("Source :" + o2.getSourceID());
//            System.out.println("Payload :" + o2.getPayload());
//
//            for(int i = 0; i < o2.getDisseminationTrace().length; ++i)
//            {
//                System.out.println("Dissemination " + i + " is " + o2.getDisseminationTrace()[i]);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            RegistrySendsNodeManifest r1 = new RegistrySendsNodeManifest();

            RegistrySendsNodeManifest.NodeInfo [] arr = new RegistrySendsNodeManifest.NodeInfo[1];

            RegistrySendsNodeManifest.NodeInfo n1 = new RegistrySendsNodeManifest.NodeInfo();
            n1.setNodeID(146);
            n1.setIpAddress("123.56.33.12".getBytes());
            n1.setPortNumber(34324);
            arr[0] = n1;

            r1.setRoutingTableSize(1);
            r1.setNodeInfoList(arr);
            r1.setNoOfNodeIDs(5);
            r1.setNodeIDs(new int[]{4,6,7,8,9});

            byte [] marshalled = r1.getBytes();

            ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalled);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            //TODO remove below line, this is for testing
            char messageType = din.readChar();

            RegistrySendsNodeManifest r2 = new RegistrySendsNodeManifest(marshalled);
            System.out.println("Routing Table Size : " + r2.getRoutingTableSize());

            for(int i =0 ; i < r2.getRoutingTableSize(); ++i)
            {
                RegistrySendsNodeManifest.NodeInfo info = r2.getNodeInfoList()[i];
                System.out.println("NodeInfo node ID : " + info.getNodeID());
                System.out.println("NodeInfo IP Address : " + new String(info.getIpAddress()));
                System.out.println("NodeInfo port : " + info.getPortNumber());
            }

            System.out.println("Number of Node IDs : " + r2.getNoOfNodeIDs());

            for(int j = 0; j< r2.getNoOfNodeIDs(); ++j)
            {
                System.out.println("Node ID : " + r2.getNodeIDs()[j]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
