package com.verde.advent.y2025;

import com.verde.advent.AdventUtils;
import javaslang.Tuple2;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class Y2025D04 {
    public static final String[] SAMPLE_INPUTS =
            List.of("..@@.@@@@.",
                    "@@@.@.@.@@",
                    "@@@@@.@.@@",
                    "@.@@@@..@.",
                    "@@.@@@@.@@",
                    ".@@@@@@@.@",
                    ".@.@.@.@@@",
                    "@.@@@.@@@@",
                    ".@@@@@@@@.",
                    "@.@.@@@.@.").toArray(String[]::new);
    public static final String[] STAR_INPUTS =
            AdventUtils.loadResourceStrings("/2025/day4-p1.txt").toArray(String[]::new);

    public Character getStatus(String[] grid, int row, int col) {
        if ((row < 0) || (row >= grid.length)) {
            return null;
        }

        String gridLine = grid[row];
        if ((col < 0) || (col >= gridLine.length())) {
            return null;
        }

        return gridLine.charAt(col);
    }

    public void setStatus(String[] grid, int row, int col, char status) {
        if ((row >= 0) && (row < grid.length)) {
            String gridLine = grid[row];
            if ((col >= 0) && (col < gridLine.length())) {
                char[] chars = gridLine.toCharArray();
                chars[col] = status;
                grid[row] = new String(chars);
            }
        }
    }

    public boolean isStatus(String[] grid, int row, int col, char status) {
        Character gridStatus = getStatus(grid, row, col);

        return (gridStatus != null) && (gridStatus.charValue() == status);
    }

    public int countAdjacent(String[] grid, int row, int col, char status) {
        // Make sure the starting point has the status
        if (!isStatus(grid, row, col, status)) {
            return 0;
        }

        int count = 0;


        // Check the row above
        if (isStatus(grid, row - 1, col - 1, status)) ++count;
        if (isStatus(grid, row - 1, col, status)) ++count;
        if (isStatus(grid, row - 1, col + 1, status)) ++count;

        // Check the row
        if (isStatus(grid, row, col - 1, status)) ++count;
        if (isStatus(grid, row, col + 1, status)) ++count;

        // Check the row  below
        if (isStatus(grid, row + 1, col - 1, status)) ++count;
        if (isStatus(grid, row + 1, col, status)) ++count;
        if (isStatus(grid, row + 1, col + 1, status)) ++count;

        return count;
    }

    public boolean isRollAccessible(String[] grid, int row, int col, BiConsumer<Integer,Integer> callback) {
        // Make sure the starting point has the status
        if (!isStatus(grid, row, col, '@')) {
            return false;
        }

        int numAdjacent = countAdjacent(grid, row, col, '@');
        boolean isAccessible = numAdjacent < 4;
        if (isAccessible) {
            //setStatus(grid, row, col, 'x');
            callback.accept(row, col);
        }

        return isAccessible;
    }

    public int countAccessibleRolls(String[] grid, BiConsumer<Integer,Integer> callback) {
        int count = 0;

        for (int row = 0; row < grid.length; ++row) {
            String gridLine = grid[row];
            for (int col = 0; col < gridLine.length(); ++col) {
                if (isRollAccessible(grid, row, col, callback)) {
                    ++count;
                }
            }
        }

        return count;
    }

    public int countAccessibleRollsStar2(String[] grid) {
        int lastCount = 0, overallCount = 0;

        do {
            // Keep track of all the positions that are accessible
            final Collection<Tuple2<Integer,Integer>> accessiblePositions = new LinkedList<>();

            BiConsumer<Integer, Integer> callback = (r, c) -> accessiblePositions.add(new Tuple2<>(r, c));

            // Iterate on the new grid
            lastCount = countAccessibleRolls(grid, callback);
            overallCount += lastCount;

            // Set all the accessible nodes from '@' to 'x', so they won't be considered in the next round
            accessiblePositions.forEach(tuple -> setStatus(grid, tuple._1(), tuple._2(), 'x'));
        } while (lastCount > 0);

        return overallCount;
    }

    @Test
    public void testSampleStar1() {
        int actual = countAccessibleRolls(SAMPLE_INPUTS, (r, c) -> {});
        int expected = 13;
        Assert.assertEquals("sample star1 accessible roll count is incorrect", expected, actual);
    }

    @Test
    public void testStar1() {
        int actual = countAccessibleRolls(STAR_INPUTS, (r, c) -> {});
        int expected = 1569;
        Assert.assertEquals("star1 accessible roll count is incorrect", expected, actual);
    }

    @Test
    public void testSampleStar2() {
        int actual = countAccessibleRollsStar2(SAMPLE_INPUTS);
        int expected = 43;
        Assert.assertEquals("sample star2 accessible roll count is incorrect", expected, actual);
    }

    @Test
    public void testStar2() {
        int actual = countAccessibleRollsStar2(STAR_INPUTS);
        int expected = 9280;
        Assert.assertEquals("star2 accessible roll count is incorrect", expected, actual);
    }
}
