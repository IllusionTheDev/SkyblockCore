package me.illusion.skyblockcore.shared.utilities;

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
        if (str == null)
            return new String[0];

        String[] array = new String[str.length()]; // Initializes a new array with the string size as a default

        int index = 0; // This will be the array index counter
        int start = -1; // This will be the section start (end of the last string position)

        char[] chars = str.toCharArray(); // String characters
        int size = chars.length; // Array length, used to iterate

        for (int position = 0; position < size; position++) { // Loop through all characters
            char letter = chars[position]; // Obtain the character

            if (letter == delimiter) { // Compare character to delimiter
                array[index++] = str.substring(start + 1, position); // Adds the substring between <START> + 1 (<START> is the previous delimiter) and the current position
                start = position; // Resets the <START> position to the current delimiter
            }
        }

        array[index] = str.substring(start + 1, size); // Adds the end

        return Arrays.copyOf(array, index + 1); // Returns a copy with all the NULL values removed
    }

    public static String replaceFirst(String str, char key, String replacement) {
        char[] chars = str.toCharArray();
        int size = chars.length;

        for (int index = 0; index < size; index++) {
            char letter = chars[index];

            if (letter == key)
                return str.substring(0, index) + replacement + str.substring(index + 1);
        }

        return str;
    }

}
