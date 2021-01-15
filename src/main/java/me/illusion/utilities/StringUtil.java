package me.illusion.utilities;

import java.util.Arrays;

public final class StringUtil {

    private StringUtil() {
        // Private Empty constructor for utility reasons
    }

    /**
     * More efficient implementation of String#split, as it's a heavy operation
     *
     * @param str       The string to split
     * @param delimiter The delimiter
     * @return The string split by the delimiter
     */
    public static String[] split(String str, char delimiter) {
        String[] array = new String[str.length()]; // Initializes a new array with the string size as a default

        int index = 0; // This will be the array index counter
        int start = -1; // This will be the section start (end of the last string position)

        char[] chars = str.toCharArray(); // String characters
        int size = chars.length; // Array length, used to iterate

        for (int i = 0; i < size; i++) { // Loop through all characters
            char c = chars[i]; // Obtain the character

            if (c == delimiter) { // Compare character to delimiter
                array[index++] = str.substring(start + 1, i); // Adds the substring between <START> + 1 (<START> is the previous delimiter) and the current position
                start = i; // Resets the <START> position to the current delimiter
            }
        }

        array[index] = str.substring(start + 1, size); // Adds the end

        return Arrays.copyOf(array, index + 1); // Returns a copy with all the NULL values removed
    }

    public static String replaceFirst(String str, char key, String replacement) {
        char[] chars = str.toCharArray();
        int size = chars.length;

        for (int i = 0; i < size; i++) {
            char c = chars[i];

            if (c == key)
                return str.substring(0, i - 1) + replacement + str.substring(i);
        }

        return str;
    }

}
