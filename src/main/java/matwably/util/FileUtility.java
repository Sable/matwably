package matwably.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtility {
    /**
     * Static function to read a stream in a hex string, used to inline wasm module into a hex string
     * @param inputStream InputStream to be converted
     * @return A UTF-8 string representation of the buffer
     * @throws IOException If it fails to read the input stream file, it throws an IOException
     */
    public static String readStreamIntoString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString("UTF-8");
    }
    /**
     * Static function to convert a byte array  in a hex string, used to inline wasm module into a hex string
     * @param bytes Byte array to be converted into hex string
     * @return A hex string representation of the byte array
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Static function to read a stream in a hex string, used to inline wasm module into a hex string
     * @param inputStream InputStream to be converted
     * @return A hex string representation of the buffer
     * @throws IOException If it fails to read the input stream file, it throws an IOException
     */
    public static String readStreamIntoHexString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }

        return bytesToHex(buf.toByteArray());
    }
}
