package me.illusion.skyblockcore.spigot.command.comparison;

import lombok.Getter;
import me.illusion.skyblockcore.shared.utilities.StringUtil;

@Getter
public class ComparisonResult {

    private boolean partiallyMatches;
    private boolean fullyMatches;
    private int[] wildcardPositions = null;

    public ComparisonResult(String identifier, String test, String[] aliases) {
        test(identifier, test);

        if (!fullyMatches)
            for (String str : aliases) {
                test(str, test);

                if (fullyMatches)
                    break;
            }

        partiallyMatches = fullyMatches || partiallyMatches;
    }

    private void test(String identifier, String test) {
        String[] identifierSplit = StringUtil.split(identifier, '.');
        String[] testSplit = StringUtil.split(test, '.');

        int length = identifierSplit.length;

        wildcardPositions = new int[length];
        int wildcard = 0;

        for (int i = 0; i < length; i++) {
            String word = identifierSplit[i];
            String testWord = testSplit[i];

            if ("*".equals(word)) { // If word is wildcard
                wildcardPositions[wildcard++] = i;
                continue;
            }

            if (!word.equalsIgnoreCase(testWord)) { // Check for match
                if (testWord.startsWith(word))
                    partiallyMatches = true;
                fullyMatches = false;
                return;
            }
        }
        fullyMatches = true;
        System.arraycopy(wildcardPositions, 0, wildcardPositions, 0, wildcard);
    }
}
