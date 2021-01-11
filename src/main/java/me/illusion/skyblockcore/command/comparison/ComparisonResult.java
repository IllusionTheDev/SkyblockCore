package me.illusion.skyblockcore.command.comparison;

import lombok.Getter;
import me.illusion.utilities.storage.StringUtil;

@Getter
public class ComparisonResult {

    private boolean matches;
    private int[] wildcardPositions = null;

    public ComparisonResult(String current, String test, String[] aliases) {
        test(current, test);

        if(!matches)
            for(String str : aliases)
            {
                test(current, str);

                if(matches)
                    return;
            }
    }

    private void test(String identifier, String test) {
        String[] currentSplit = StringUtil.split(identifier, '.');
        String[] testSplit = StringUtil.split(test, '.');

        int length = currentSplit.length;

        if(length != testSplit.length)
        {
            matches = false;
            return;
        }

        wildcardPositions = new int[length];
        int wildcard = 0;

        for(int i = 0; i < length; i++) {
            String c = currentSplit[i];
            String t = testSplit[i];

            if(c.equals("*")) {
                wildcardPositions[wildcard++] = i;
                continue;
            }

            if(c.equalsIgnoreCase(t))
                continue;

            matches = false;
            return;
        }
        matches = true;
        System.arraycopy(wildcardPositions, 0, wildcardPositions, 0, wildcard);
    }
}
