package matwably.util;

import java.nio.ByteBuffer;

public class ByteConversion {
    public byte[] byteArray;

    private static final char[] kDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f' };

    public ByteConversion(double d)
    {
        byteArray = new byte[8];
        long lng = Double.doubleToLongBits(d);
        for(int i = 0; i < 8; i++) byteArray[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
    }
    public ByteConversion(float d)
    {
        byteArray = new byte[4];
        long lng = Float.floatToRawIntBits(d);
        for(int i = 0; i < 4; i++) byteArray[i] = (byte)((lng >> ((3 - i) * 8)) & 0xff);
    }
    public ByteConversion(int d)
    {
        byteArray = ByteBuffer.allocate(4).putInt(d).array();
    }
    public ByteConversion(byte d)
    {
        byteArray = new byte[1];
        byteArray[0] = (byte)(d & 0xff);
    }
    public ByteConversion(short d)
    {
        byteArray = new byte[2];
        byteArray[0] = (byte)(d >> 8 & 0xff);
        byteArray[1] = (byte)(d & 0xff);
    }
    public ByteConversion(char d)
    {
        byteArray = new byte[2];
        byteArray[0] = (byte)(d >> 8 & 0xff);
        byteArray[1] = (byte)(d & 0xff);
    }
    public String toHexString(){
        char []arr = bytesToHex(byteArray);
        StringBuilder acc = new StringBuilder();
        for (int i = arr.length-1; i >=1 ; i-=2) {
            acc.append("\\"+arr[i-1]+""+arr[i]);
        }
        return acc.toString();
    }
    public static char[] bytesToHex(byte[] raw) {
        int length = raw.length;
        char[] hex = new char[length * 2];
        for (int i = 0; i < length; i++) {
            int value = (raw[i] + 256) % 256;
            int highIndex = value >> 4;
            int lowIndex = value & 0x0f;
            hex[i * 2 + 0] = kDigits[highIndex];
            hex[i * 2 + 1] = kDigits[lowIndex];
        }
        return hex;
    }

    public String toString() {
       return this.toHexString();
    }

}
