package com.verde.advent.y2019.d3;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class TestY2019D03 {
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2019/day/3/inputs.txt");

    @Test
    public void testMinManhattanDistance() {
        String[][] inputs = {
                { 
                    "R75,D30,R83,U83,L12,D49,R71,U7,L72",
                    "U62,R66,U55,R34,D71,R55,D58,R83"
                },
                {
                    "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
                    "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
                }
        };
        int[] expected = { 159, 135 };

        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("getMinManhattanDistance() incorrect (index=%d)", i), expected[i], Y2019D03.getMinManhattanDistance(inputs[i][0], inputs[i][1]));
        }
    }

    @Test
    public void testMinBestSteps() {
        String[][] inputs = {
                { 
                    "R75,D30,R83,U83,L12,D49,R71,U7,L72",
                    "U62,R66,U55,R34,D71,R55,D58,R83"
                },
                {
                    "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
                    "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
                }
        };
        int[] expected = { 610, 410 };

        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("getMinBestSteps() incorrect (index=%d)", i), expected[i], Y2019D03.getMinBestSteps(inputs[i][0], inputs[i][1]));
        }
    }

    @Test
    public void testStarInputOne() {
        int result = Y2019D03.getMinManhattanDistance(STAR_INPUTS.get(0), STAR_INPUTS.get(1)); 
        
        System.out.printf("getMinManhattanDistance() result: %d\n", result);
        assertEquals("star input one is incorrect", 2180, result);
    }
    
    @Test
    public void testStarInputTwo() {
        int result = Y2019D03.getMinBestSteps(STAR_INPUTS.get(0), STAR_INPUTS.get(1)); 
        
        System.out.printf("getMinBestSteps() result: %d\n", result);
        assertEquals("star input two is incorrect", 112316, result);
    }

}
