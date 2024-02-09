package com.verde.advent.y2021;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import com.verde.advent.AdventUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 */
public class Y2021D01 {

    public static class DeeperFilter implements IntPredicate {
        private boolean seenReading = false;
        private int lastReading = -1;

        @Override
        public boolean test(int reading) {
            boolean deeper = seenReading && (reading > lastReading);

            seenReading = true;
            lastReading = reading;
            return deeper;
        }
    }

    public int[] toWindowedSums(int[] readings, int width) {
        ArrayList<Integer> sums = new ArrayList<Integer>();

        for (int i=0; i <= (readings.length - width); ++i) {
            int sum = 0;
            for (int j=0; j<width; ++j) {
                sum += readings[i+j];
            }
            sums.add(sum);
        }

        return sums.stream().mapToInt(Integer::intValue).toArray();
    }

    public static long getNumIncreased(int[] readings) {
        return getNumIncreased(Arrays.stream(readings));
    }

    public static long getNumIncreased(IntStream readings) {
        long numDeeper = readings.filter(new DeeperFilter()).count();

        return numDeeper;
    }

    @Test
    public void testNumIncreased() {
        int[] readings = { 199, 200, 208, 210, 200, 207, 240, 269, 260, 263 };
        assertEquals("getNumIncreased() incorrect", 7, getNumIncreased(readings));
    }

    @Test
    public void testToWindowedSums() {
        int[] readings = { 199, 200, 208, 210, 200, 207, 240, 269, 260, 263 };
        int[] expected = { 607, 618, 618, 617, 647, 716, 769, 792 };
        Assert.assertArrayEquals("toWindowedSums() incorrect", expected, toWindowedSums(readings, 3));
    }

    @Test
    public void testStar1() {
        IntStream readings = AdventUtils.toIntStream(STAR_INPUTS);
        assertEquals("STAR1 incorrect", 1791, getNumIncreased(readings));
    }

    @Test
    public void testStar2() {
        IntStream readings = AdventUtils.toIntStream(STAR_INPUTS);
        assertEquals("STAR2 incorrect", 1822, getNumIncreased(toWindowedSums(readings.toArray(), 3)));
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2021/day1-inputs.txt");
}
