package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * --- Day 18: Like a Rogue ---
 * As you enter this room, you hear a loud click! Some of the tiles in the floor here seem to be pressure plates for traps, 
 * and the trap you just triggered has run out of... whatever it tried to do to you. You doubt you'll be so lucky next time.
 * 
 * Upon closer examination, the traps and safe tiles in this room seem to follow a pattern. The tiles are arranged into rows 
 * that are all the same width; you take note of the safe tiles (.) and traps (^) in the first row (your puzzle input).
 * 
 * The type of tile (trapped or safe) in each row is based on the types of the tiles in the same position, and to either side of that position, 
 * in the previous row. (If either side is off either end of the row, it counts as "safe" because there isn't a trap embedded in the wall.)
 * 
 * For example, suppose you know the first row (with tiles marked by letters) and want to determine the next row (with tiles marked by numbers):
 * ABCDE
 * 12345
 * 
 * The type of tile 2 is based on the types of tiles A, B, and C; the type of tile 5 is based on tiles D, E, and an imaginary "safe" tile. 
 * Let's call these three tiles from the previous row the left, center, and right tiles, respectively. Then, a new tile is a trap only in 
 * one of the following situations:
 * - Its left and center tiles are traps, but its right tile is not.
 * - Its center and right tiles are traps, but its left tile is not.
 * - Only its left tile is a trap.
 * - Only its right tile is a trap.
 * 
 * In any other situation, the new tile is safe.
 * 
 * Then, starting with the row ..^^., you can determine the next row by applying those rules to each new tile:
 * - The leftmost character on the next row considers the left (nonexistent, so we assume "safe"), center (the first ., which means "safe"), and right (the second ., also "safe") tiles on the previous row. Because all of the trap rules require a trap in at least one of the previous three tiles, the first tile on this new row is also safe, ..
 * - The second character on the next row considers its left (.), center (.), and right (^) tiles from the previous row. This matches the fourth rule: only the right tile is a trap. Therefore, the next tile in this new row is a trap, ^.
 * - The third character considers .^^, which matches the second trap rule: its center and right tiles are traps, but its left tile is not. Therefore, this tile is also a trap, ^.
 * - The last two characters in this new row match the first and third rules, respectively, and so they are both also traps, ^.
 * 
 * After these steps, we now know the next row of tiles in the room: .^^^^. Then, we continue on to the next row, using the same rules, 
 * and get ^^..^. After determining two new rows, our map looks like this:
 * ..^^.
 * .^^^^
 * ^^..^
 * 
 * Here's a larger example with ten tiles per row and ten rows:
 * .^^.^.^^^^
 * ^^^...^..^
 * ^.^^.^.^^.
 * ..^^...^^^
 * .^^^^.^^.^
 * ^^..^.^^..
 * ^^^^..^^^.
 * ^..^^^^.^^
 * .^^^..^.^^
 * ^^.^^^..^^
 * 
 * In ten rows, this larger example has 38 safe tiles.
 * 
 * Starting with the map in your puzzle input, in a total of 40 rows (including the starting row), how many safe tiles are there?
 * 
 * Your puzzle answer was 2013.
 * 
 * --- Part Two ---
 * How many safe tiles are there in a total of 400000 rows?
 * 
 * Your puzzle answer was 20006289.
 * 
 * @author bumpverde
 */
public class Y2016D18 {
    public static final char SAFE = '.';
    public static final char TRAP = '^';
    
    /**
     * The type of tile 2 is based on the types of tiles A, B, and C; the type of tile 5 
     * is based on tiles D, E, and an imaginary "safe" tile. Let's call these three tiles 
     * from the previous row the left, center, and right tiles, respectively. Then, a new 
     * tile is a trap only in one of the following situations:
     * 
     * Its left and center tiles are traps, but its right tile is not.
     * Its center and right tiles are traps, but its left tile is not.
     * Only its left tile is a trap.
     * Only its right tile is a trap.
     * 
     * In any other situation, the new tile is safe.
     */
    public static boolean willBeSafe(String s, int i) {
        char left = (i == 0) ? SAFE : s.charAt(i - 1);
        char center = s.charAt(i);
        char right = (i == (s.length() - 1)) ? SAFE : s.charAt(i + 1);
        
        boolean isTrap = 
                ((left == TRAP) && (center == TRAP) && (right == SAFE))
                || ((center == TRAP) && (right == TRAP) && (left == SAFE))
                || ((left == TRAP) && (center == SAFE) && (right == SAFE))
                || ((left == SAFE) && (center == SAFE) && (right == TRAP));

        return !isTrap;
    }
    
    public static String genRow(String priorRow) {
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<priorRow.length(); ++i) {
            sb.append(willBeSafe(priorRow, i) ? SAFE : TRAP);
        }
        return sb.toString();
    }
    
    public static Collection<String> fillBoard(String firstRow, int numRows) {
        Collection<String> rows = new LinkedList<String>();
        rows.add(firstRow);
        
        // Fill out the board, row by row
        for (String priorRow=firstRow; rows.size() < numRows; ) {
            rows.add(priorRow = genRow(priorRow));
        }
        
        return rows;
    }
    
    public static int count(Collection<String> rows, char c) {
        return rows.stream().mapToInt(s -> StringUtils.countMatches(s, c)).sum();
    }
    
    @Test
    public void testwillBeSafe() {
        String[] inputs = {
                "..^^.",
                ".^^^^",
//                "^^..^",
        };
        boolean[][] expected = {
                { true, false, false, false, false },
                { false, false, true, true, false },
        };
        
        for (int i=0; i<inputs.length; ++i) {
            for (int c=0; c<inputs[i].length(); ++c) {
                assertEquals(String.format("willBeSafe(%s, %d) incorrect", inputs[i], c), expected[i][c], willBeSafe(inputs[i], c));
            }
        }
    }
    
    @Test
    public void testStarOne() {
        String input = STAR_INPUT;
        int expected = 2013;
        assertEquals("star one incorrect", expected, count(fillBoard(input, 40), SAFE));
    }
    
    @Test
    public void testStarTwo() {
        String input = STAR_INPUT;
        int expected = 20006289;
        assertEquals("star two rrect", expected, count(fillBoard(input, 400000), SAFE));
    }
    
    public static String STAR_INPUT = ".^^^.^.^^^.^.......^^.^^^^.^^^^..^^^^^.^.^^^..^^.^.^^..^.^..^^...^.^^.^^^...^^.^.^^^..^^^^.....^....";
}
