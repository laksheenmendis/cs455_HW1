package cs455.overlay.util;

import java.io.*;

public class Converter {

    public static byte[] integerArrayToByteArray(int[] values) throws IOException
    {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        for(int i=0; i < values.length; ++i)
        {
            dout.writeInt(values[i]);
        }
        dout.close();
        baOutputStream.close();
        return baOutputStream.toByteArray();
    }

    public static int[] byteArrayToIntegerArray(int length, byte[] values) throws IOException
    {
        int[] arr = new int[length];
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(values);
        DataInputStream din = new DataInputStream(baInputStream);
        for(int i=0; i< length; ++i)
        {
            arr[i] = din.readInt();
        }
        din.close();
        baInputStream.close();
        return arr;
    }
}
