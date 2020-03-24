package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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
