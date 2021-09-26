package me.illusion.skyblockcore.spigot.command.comparison;

import lombok.Getter;
import me.illusion.skyblockcore.shared.utilities.StringUtil;

@Getter
public class ComparisonResult {

    private boolean matches;
    private int[] wildcardPositions = null;

    private final String[] currentSplit;

    public ComparisonResult(String current, String test, String[] aliases) {
        currentSplit = StringUtil.split(current, '.');
        test(test);

        if (!matches)
            for (String str : aliases) {
                test(str);

                if (matches)
                    return;
            }

    }

    private void test(String test) {
        String[] testSplit = StringUtil.split(test, '.');

        int length = currentSplit.length;

        if (length != testSplit.length) {
            matches = false;
            return;
        }

        wildcardPositions = new int[length];
        int wildcard = 0;

        for(int i = 0; i < length; i++) {
            String word = currentSplit[i];
            String testWord = testSplit[i];

            if ("*".equals(word)) { // If word is wildcard
                wildcardPositions[wildcard++] = i;
                continue;
            }

            if (!word.equalsIgnoreCase(testWord)) { // Check for match
                matches = false;
                return;
            }
        }
        matches = true;
        System.arraycopy(wildcardPositions, 0, wildcardPositions, 0, wildcard);
    }
}
