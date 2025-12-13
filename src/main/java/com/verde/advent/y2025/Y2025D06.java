package com.verde.advent.y2025;

import com.verde.advent.AdventUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Y2025D06 {
    public static final Collection<String> SAMPLE_INPUTS =
            List.of("123 328  51 64 ",
                    " 45 64  387 23 ",
                    "  6 98  215 314",
                    "*   +   *   +  ");
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2025/day6-p1.txt");

    public BigInteger solve(String input) {
        input = input.trim();
        BigInteger result = null;
        String[] parts = StringUtils.split(input, " ");
        boolean multiply = input.endsWith("*");

        for (int i = 0; i < parts.length-1; i++) {
            BigInteger value =  new BigInteger(parts[i]);

            if (result == null) {
                result = value;
            } else {
                if (multiply) {
                    result = result.multiply(value);
                } else {
                    result = result.add(value);
                }
            }
        }

        return result;
    }

    public BigInteger solve(Collection<String> inputs) {
        return inputs.stream().map(this::solve).reduce(BigInteger.ZERO, BigInteger::add);

        /**
        BigInteger result = BigInteger.ZERO;
        for (String input : inputs) {
            result = result.add(solve(input));
        }

        return result;
         */
    }

    @Test
    public void testSampleStar1() {
        Collection<String> transposed = AdventUtils.transpose(SAMPLE_INPUTS);
        BigInteger actual = solve(transposed);
        BigInteger expected = BigInteger.valueOf(4277556L);
        Assert.assertEquals("sample star1 cephalapod math is incorrect", expected, actual);
    }

    @Test
    public void testStar1() {
        Collection<String> transposed = AdventUtils.transpose(STAR_INPUTS);
        BigInteger actual = solve(transposed);
        BigInteger expected = BigInteger.valueOf(5322004718681L);
        Assert.assertEquals("star1 cephalapod math is incorrect", expected, actual);
    }

    @Test
    public void testSampleStar2() {
        char[][] grid = AdventUtils.toChars(SAMPLE_INPUTS);
        Collection<String> inputs = AdventUtils.transposeCephalapod(grid);
        BigInteger actual = solve(inputs);
        BigInteger expected = BigInteger.valueOf(3263827L);
        Assert.assertEquals("sample star2 cephalapod math is incorrect", expected, actual);
    }

    @Test
    public void testStar2() {
        char[][] grid = AdventUtils.toChars(STAR_INPUTS);
        Collection<String> inputs = AdventUtils.transposeCephalapod(grid);
        BigInteger actual = solve(inputs);
        BigInteger expected = BigInteger.valueOf(9876636978528L);
        Assert.assertEquals("star2 cephalapod math is incorrect", expected, actual);
    }
}
