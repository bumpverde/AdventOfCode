package com.verde.advent.y2019.d1;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class TestY2019D01 {
    public static final Collection<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2019/day/1/inputs.txt");
    
    @Test
    public void testComputeRequiredFuel() {
        int[] inputs = { 12, 14, 1969, 100756 };
        int[] expected = { 2, 2, 654, 33583 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("computeRequiredFuel() incorrect", expected[i], Y2019D01.computeRequiredFuel(inputs[i]));
        }
    }
    
    @Test
    public void testStarInputOne() {
        int sum = 
                STAR_INPUTS
                .stream()
                .mapToInt(Integer::parseInt)
                .map(Y2019D01::computeRequiredFuel)
                .sum();
        
        System.out.printf("Fuel required for %d components is %d\n", STAR_INPUTS.size(), sum);
        assertEquals("star input one is incorrect", sum, 3161483);
    }

    @Test
    public void testComputeAllRequiredFuel() {
        int[] inputs = { 14, 1969, 100756 };
        int[] expected = { 2, 966, 50346 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("computeAllRequiredFuel() incorrect", expected[i], Y2019D01.computeAllRequiredFuel(inputs[i]));
        }
    }

    @Test
    public void testStarInputTwo() {
        int sum = 
                STAR_INPUTS
                .stream()
                .mapToInt(Integer::parseInt)
                .map(Y2019D01::computeAllRequiredFuel)
                .sum();
        
        System.out.printf("ALL Fuel required for %d components is %d\n", STAR_INPUTS.size(), sum);
        assertEquals("star input two is incorrect", sum, 4739374);
    }

}
