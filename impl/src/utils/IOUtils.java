package utils;

import java.io.*;

public class IOUtils {

    public static String readString(InputStream is) {
        DataInputStream ds = new DataInputStream(is);
        String s = "";
        try {
            s = ds.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void writeString(OutputStream os, String s) {
        DataOutputStream dStream = new DataOutputStream(os);
        try {
            dStream.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
