package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.junit.Test;

import java.io.DataInput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Y2023D01 {
    public static final Map<String,String> DIGIT_WORDS = new HashMap<>();
    static {
        DIGIT_WORDS.put("one", "1");
        DIGIT_WORDS.put("two", "2");
        DIGIT_WORDS.put("three", "3");
        DIGIT_WORDS.put("four", "4");
        DIGIT_WORDS.put("five", "5");
        DIGIT_WORDS.put("six", "6");
        DIGIT_WORDS.put("seven", "7");
        DIGIT_WORDS.put("eight", "8");
        DIGIT_WORDS.put("nine", "9");
    };

    public static String findDigit(String s, int start, int delta) {
        for (int i=start; (i >= 0) && i <s.length(); i += delta) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return String.valueOf(c);
            }

            // Check for the words at this current place moving forward
            String subStr = s.substring(i);
            for (String word : DIGIT_WORDS.keySet()) {
                if (subStr.startsWith(word)) {
                    return DIGIT_WORDS.get(word);
                }
            }
        }

        return null;
    }

    public static String findFirstDigit(String s) {
        return findDigit(s, 0, 1);
    }

    public static String findLastDigit(String s) {
        return findDigit(s, s.length()-1, -1);
    }

    public static String getCalibrationCode(String s) {
        return String.format("%s%s", findFirstDigit(s), findLastDigit(s));
    }

    public static int getCalibrationCodeAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    @Test
    public void testBasicInputsPart1() {
        String[] inputs = {
                "1abc2",
                "pqr3stu8vwx",
                "a1b2c3d4e5f",
                "treb7uchet"
        };

        int expected = 142;
        int actual =
                Stream.of(inputs)
                        .map(Y2023D01::getCalibrationCode)
                        .mapToInt(Y2023D01::getCalibrationCodeAsInt)
                        .sum();

        assertEquals("trebuchet calculation incorrect", expected, actual);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day1-p1.txt");


        int expected = 55834;
        int actual =
                inputs.stream()
                        .map(Y2023D01::getCalibrationCode)
                        .mapToInt(Y2023D01::getCalibrationCodeAsInt)
                        .sum();
        System.out.printf("Day1 Part1: %d\n", actual);
        assertEquals("Part1 trebuchet calculation incorrect", expected, actual);
    }

    @Test
    public void testBasicInputsPart2() {
        String[] inputs = {
                "two1nine",
                "eightwothree",
                "abcone2threexyz",
                "xtwone3four",
                "4nineeightseven2",
                "zoneight234",
                "7pqrstsixteen",
        };

        int expected = 281;
        int actual =
                Stream.of(inputs)
                        .map(Y2023D01::getCalibrationCode)
                        .mapToInt(Y2023D01::getCalibrationCodeAsInt)
                        .sum();

        assertEquals("Part2 trebuchet calculation incorrect", expected, actual);
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day1-p1.txt");


        int expected = 53221;
        int actual =
                inputs.stream()
                        .map(Y2023D01::getCalibrationCode)
                        .mapToInt(Y2023D01::getCalibrationCodeAsInt)
                        .sum();
        System.out.printf("Day1 Part2: %d\n", actual);
        assertEquals("Part2 trebuchet calculation incorrect", expected, actual);
    }

}
