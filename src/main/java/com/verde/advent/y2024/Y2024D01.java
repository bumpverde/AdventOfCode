package com.verde.advent.y2024;

import com.verde.advent.AdventUtils;
import com.verde.advent.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.sql.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Y2024D01 {

    public long computeDistance(Collection<String> inputs, int numCols) {
        // Allocate all the collections for each column
        Collection<Integer>[] intColls = new Collection[numCols];
        for (int i=0; i<numCols; ++i) {
            intColls[i] = new ArrayList<>(intColls.length);
        }

        // Populate all the collections
        for (String input : inputs) {
            String[] parts = StringUtils.split(input," ");
            for (int i=0; i< parts.length; ++i) {
                int val = Integer.parseInt(parts[i]);
                intColls[i].add(val);
            }
        }

        // Sort them all
        for (int i=0; i<numCols; ++i) {
            intColls[i] = intColls[i].stream().sorted().collect(Collectors.toList());
        }

        // Distance function passed to the zip util
        BiFunction<Integer, Integer, Integer> distanceCalc = (x, y) -> Math.abs(x - y);

        long totalDistance = 0L;
        for (int i=0; i<intColls[0].size(); ++i) {
            int a = ((ArrayList<Integer>) intColls[0]).get(i);
            int b = ((ArrayList<Integer>) intColls[1]).get(i);
            int distance = Math.abs(a-b);

            totalDistance += distance;
        }

        //        IntStream distanceStr = StreamUtils.zip(intColls[0].stream().mapToInt(x -> x), intColls[1].stream().mapToInt(x -> x), distanceCalc);

        return totalDistance;
    }

    @Test
    public void testBasicInputsPart1() {
        String[] inputs = {
                "3   4",
                "4   3",
                "2   5",
                "1   3",
                "3   9",
                "3   3"
        };

        long expected = 11;
        long actual = computeDistance(Arrays.asList(inputs), 2);
        assertEquals("Basic inputs Part1 incorrect", expected, actual);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2024/day1-p1.txt");

        long expected = 1660292;
        long actual = computeDistance(inputs, 2);
        assertEquals("Part1 incorrect", expected, actual);
    }

    @Test
    public void testBasicInputsPart2() {
//        assertEquals("Part2 trebuchet calculation incorrect", expected, actual);
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day1-p1.txt");
        //assertEquals("Part2 trebuchet calculation incorrect", expected, actual);
    }
}
