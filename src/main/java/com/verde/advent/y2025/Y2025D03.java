package com.verde.advent.y2025;


import com.verde.advent.AdventUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;

public class Y2025D03 {
    public static final Collection<String> SAMPLE_INPUTS =
            List.of("987654321111111",
                    "811111111111119",
                    "234234234234278",
                    "818181911112111");
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2025/day3-p1.txt");

    /**
     * ChatGPT helped on this one.
     *
     * @param bank
     * @param voltageLength
     * @return
     */
    public static BigInteger getJoltage(String bank, int voltageLength) {
        int n = bank.length();
        if (voltageLength > n) {
            throw new IllegalArgumentException("voltageLength cannot be greater than length");
        }

        int toRemove = n - voltageLength;
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : bank.toCharArray()) {
            // Pop smaller previous digits if we can still remove
            while (!stack.isEmpty() && toRemove > 0 && stack.peekLast() < c) {
                stack.pollLast();
                toRemove--;
            }
            stack.addLast(c);
        }

        // If we still have digits to remove, remove from the end
        while (toRemove > 0) {
            stack.pollLast();
            toRemove--;
        }

        // Build result of length voltageLength from the stack
        StringBuilder sb = new StringBuilder(voltageLength);
        int count = 0;
        for (char c : stack) {
            if (count == voltageLength) break;
            sb.append(c);
            count++;
        }

        return new BigInteger(sb.toString());
    }

    public BigInteger getTotalJoltage(Collection<String> banks, int joltLength) {
        //return banks.stream().mapToInt(b -> getJoltage(b)).sum();
        //BigInteger sum = banks.stream().map(b -> getJoltageStar2(b, joltLength)).reduce(BigInteger.ZERO, BigInteger::add);
        BigInteger sum = banks.stream().map(b -> getJoltage(b, joltLength)).reduce(BigInteger.ZERO, BigInteger::add);
        return sum;
    }

    @Test
    public void testSampleStar1() {
        BigInteger actual = getTotalJoltage(SAMPLE_INPUTS, 2);
        BigInteger expected = BigInteger.valueOf(357L);
        Assert.assertEquals("sample star1 joltage is incorrect", expected, actual);
    }

    @Test
    public void testStar1() {
        BigInteger actual = getTotalJoltage(STAR_INPUTS, 2);
        BigInteger expected = BigInteger.valueOf(17452L);
        Assert.assertEquals("star1 joltage is incorrect", expected, actual);
    }

    @Test
    public void testSampleStar2() {
        BigInteger actual = getTotalJoltage(SAMPLE_INPUTS, 12);
        BigInteger expected = BigInteger.valueOf(3121910778619L);
        Assert.assertEquals("sample star2 joltage is incorrect", expected, actual);
    }

    @Test
    public void testStar2() {
        BigInteger actual = getTotalJoltage(STAR_INPUTS, 12);
        BigInteger expected = BigInteger.valueOf(173300819005913L);
        Assert.assertEquals("star2 joltage is incorrect", expected, actual);
    }
}
