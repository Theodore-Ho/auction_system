package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    /**
     * prepare for analysis InputStream
     * @param is InputStream
     * @return combine the top four digits to an integer
     */
    public static int readInt(InputStream is) {
        int[] values = new int[4]; // why 4? - because IO.read() returns 8 bits. The "int" has 32 bits.
        try {
            for (int i = 0; i < 4; i++) {
                values[i] = is.read(); // read the top four digits (in int format)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values[0] << 24 | values[1] << 16 | values[2] << 8 | values[3]; // bitwise operation combine the top four digits to int
    }

    /**
     * Send message
     * @param os OutputStream
     * @param s the message to be sent
     */
    public static void writeInt(OutputStream os, int s) {
        int[] values = new int[4];
        values[0] = (s >> 24) & 0xFF;
        values[1] = (s >> 16) & 0xFF;
        values[2] = (s >> 8) & 0xFF;
        values[3] = (s) & 0xFF;

        try{
            for (int i = 0; i < 4; i++) { // Split the 32 bits int data into 4 parts and send in 4 times
                os.write(values[i]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Handle the InputStream
     * @param is InputStream
     * @return Return the InputStream in String
     */
    public static String readString(InputStream is) {
        int len = readInt(is);
        byte[] sByte = new byte[len];
        try {
            is.read(sByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(sByte);
    }

    /**
     * Handle the OutputStream
     * @param os OutputStream
     * @param s message to be sent
     */
    public static void writeString(OutputStream os, String s) {
        byte[] bytes = s.getBytes();
        int len = bytes.length;
        writeInt(os, len);
        try {
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
