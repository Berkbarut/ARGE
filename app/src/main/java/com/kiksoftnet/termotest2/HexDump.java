package com.kiksoftnet.termotest2;



import java.security.InvalidParameterException;

/**
 * Clone of Android's HexDump class, for use in debugging. Cosmetic changes
 * only.
 */
public class HexDump {
    private final static char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }
    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();
        byte[] line = new byte[8];
        int lineIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == line.length) {
                for (int j = 0; j < line.length; j++) {
                    if (line[j] > ' ' && line[j] < '~') {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(".");
                    }
                }
                result.append("\n");
                lineIndex = 0;
            }
            byte b = array[i];
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);
            result.append(" ");
            line[lineIndex++] = b;
        }
        for (int i = 0; i < (line.length - lineIndex); i++) {
            result.append("   ");
        }
        for (int i = 0; i < lineIndex; i++) {
            if (line[i] > ' ' && line[i] < '~') {
                result.append(new String(line, i, 1));
            } else {
                result.append(".");
            }
        }
        return result.toString();
    }
}