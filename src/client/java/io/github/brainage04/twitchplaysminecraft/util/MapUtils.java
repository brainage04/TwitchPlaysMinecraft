package io.github.brainage04.twitchplaysminecraft.util;

import java.util.Map;

public class MapUtils {
    public static String getMostCommonString(Map<String, Integer> map) {
        String mostFrequentString = "";
        int maxCount = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentString = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostFrequentString;
    }
}
