package io.github.brainage04.twitchplaysminecraft.util;

import java.util.TreeMap;

// https://stackoverflow.com/a/19759564
public class RomanNumber {
    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static String toRoman(Number number) {
        int l =  map.floorKey(number.intValue());
        if (number.intValue() == l) {
            return map.get(number.intValue());
        }
        return map.get(l) + toRoman(number.intValue() - l);
    }
}