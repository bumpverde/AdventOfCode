package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D03 {
    public static boolean isValid(int s1, int s2, int s3) {
        return (s1 + s2) > s3;
    }
    
    public static int getNumValid(String[] specs) {
        int numValid = 0;
        for (String spec : specs) {
            String[] sides = StringUtils.split(spec, " ");
            
            for (int i=0; i<sides.length; i+=3) {
                int s1 = Integer.parseInt(sides[i].trim());
                int s2 = Integer.parseInt(sides[i+1].trim());
                int s3 = Integer.parseInt(sides[i+2].trim());
            
                if (isValid(s1, s2, s3) && isValid(s2, s3, s1) && isValid(s1, s3, s2)) {
                    ++numValid;
                }
            }
        }
        
        return numValid;
    }
    
    public static int getNumVerticalValid(String[] specs) {
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        // Flip the orientation of the specs
        for (String spec : specs) {
            String[] sides = StringUtils.split(spec, " ");
            
            for (int i=0; i<sides.length; i+=3) {
                sb0.append(" " + sides[i]);
                sb1.append(" " + sides[i+1]);
                sb2.append(" " + sides[i+2]);
            }
        }
        
        // Fall back to non-vertical evaluator
        return getNumValid(new String[] { sb0.toString(), sb1.toString(), sb2.toString() });
    }
    
    @Test
    public void testGetNumValid() {
        String[][] inputs = {
                { 
                    "5 10 25" 
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
                {
                    "101 102 103",
                    "201 202 203",
                    "301 302 303",
                    "401 402 403",
                    "501 502 503",
                    "601 602 603"
                }
        };
        
        int[] expected = { 0, 993, 6};
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getNumValid() incorrect", expected[i], getNumValid(inputs[i]));
        }
    }

    @Test
    public void testGetNumVerticalValid() {
        String[][] inputs = {
                {
                    "101 301 501", 
                    "102 302 502",
                    "103 303 503",
                    "201 401 601",
                    "202 402 602",
                    "203 403 603",
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
        };
        
        int[] expected = { 6, 1849 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getNumVerticalValid() incorrect", expected[i], getNumVerticalValid(inputs[i]));
        }
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day3-inputs.txt");
}
