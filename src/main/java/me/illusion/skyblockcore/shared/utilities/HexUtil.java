package me.illusion.skyblockcore.shared.utilities;

public final class HexUtil {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private HexUtil() {

    }

    public static String bytesToHex(byte... bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int index = 0; index < bytes.length; index++) {
            int v = bytes[index] & 0xFF;
            hexChars[index * 2] = HEX_ARRAY[v >>> 4];
            hexChars[index * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
