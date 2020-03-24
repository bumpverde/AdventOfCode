package com.verde.advent.y2019.d2;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.verde.advent.AdventUtils;
import com.verde.advent.y2018.d3.Y2018D03;

public class TestY2019D02 {
    public static final int[] STAR_INPUTS = {
            1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,10,1,19,1,19,9,23,1,23,6,27,2,27,13,31,1,10,31,35,1,10,35,39,2,39,6,43,1,43,5,47,2,10,47,51,1,5,51,55,1,55,13,59,1,59,9,63,2,9,63,67,1,6,67,71,1,71,13,75,1,75,10,79,1,5,79,83,1,10,83,87,1,5,87,91,1,91,9,95,2,13,95,99,1,5,99,103,2,103,9,107,1,5,107,111,2,111,9,115,1,115,6,119,2,13,119,123,1,123,5,127,1,127,9,131,1,131,10,135,1,13,135,139,2,9,139,143,1,5,143,147,1,13,147,151,1,151,2,155,1,10,155,0,99,2,14,0,0
    };

    @Test
    public void testRunProgram() {
        int[][] inputs = {
                { 1,9,10,3,2,3,11,0,99,30,40,50 },
                { 1,0,0,0,99 }, 
                { 2,3,0,3,99 }, 
                { 2,4,4,5,99,0 },
                { 1,1,1,4,99,5,6,0,99 },
        };
        int[] expected = { 3500, 2, 2, 2, 30, };

        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("runProgram() incorrect (index=%d)", i), expected[i], Y2019D02.runProgram(inputs[i]));
        }
    }

    @Test
    public void testStarInputOne() {
        int result = Y2019D02.runProgram(STAR_INPUTS, 12, 2);
        
        System.out.printf("Program result with %d components: %d\n", STAR_INPUTS.length, result);
        assertEquals("star input one is incorrect", 4462686, result);
    }
    
    @Test
    public void testStarInputTwo() {
        boolean done = false;
        for (int noun=0; !done && noun<=99; ++noun) {
            for (int verb=0; !done && verb<=99; ++verb) {
                int result = Y2019D02.runProgram(STAR_INPUTS, noun, verb);
                
                if (done = result == 19690720) {
                    System.out.printf("To get %d, noun=%d and verb=%d and 100*noun + verb=%d\n", result, noun, verb, (100*noun) +  verb);
                }
            }
        }
    }
    
}
