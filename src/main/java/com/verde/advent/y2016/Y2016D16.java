package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class Y2016D16 {
    /**
     * Call the data you have at this point "a".
     * Make a copy of "a"; call this copy "b".
     * Reverse the order of the characters in "b".
     * In "b", replace all instances of 0 with 1 and all 1s with 0.
     * The resulting data is "a", then a single 0, then "b".
     */
    public static String dragonCurve(String a) {
        StringBuilder sb = new StringBuilder();
        String b = StringUtils.reverse(a);
        for (int i=0; i<b.length(); ++i) {
            sb.append(b.charAt(i) == '0' ? '1' : '0');
        }
        b = sb.toString();
        
        String dragon = a + "0" + b;
        return dragon;
    }

    /**
     * The checksum for some given data is created by considering each non-overlapping pair of 
     * characters in the input data. If the two characters match (00 or 11), the next checksum 
     * character is a 1. If the characters do not match (01 or 10), the next checksum character 
     * is a 0. This should produce a new string which is exactly half as long as the original. 
     * If the length of the checksum is even, repeat the process until you end up with a 
     * checksum with an odd length.
     */
    public static String computeChecksum(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<input.length()-1; i += 2) {
            char c1 = input.charAt(i);
            char c2 = input.charAt(i+1);
            
            sb.append(c1 == c2 ? '1' : '0');
        }
        
        String checksum = sb.toString();
        return ((checksum.length() % 2) == 0) ? computeChecksum(checksum) : checksum;
    }
    
    public static String getChecksum(String input, int length) {
        String data = input;
        
        // Generate at least as much as you need
        while (data.length() < length) {
            data = dragonCurve(data);
        }
        
        // But no more than you need
        if (data.length() > length) {
            data = data.substring(0, length);
        }
        
        String checksum = computeChecksum(data);
        return checksum;
    }

    @Test
    public void testDragonCurve() {
        String[] inputs =   { "1", "0", "11111", "111100001010", "10000", "10000011110", };
        String[] expected = { "100", "001", "11111000000", "1111000010100101011110000", "10000011110", "10000011110010000111110", };

        for (int i=0; i<inputs.length; ++i) {
            assertEquals("dragonCurve() incorrect", expected[i], dragonCurve(inputs[i]));
        }
    }
    @Test
    public void testComputeChecksum() {
        String[] inputs =   { "110010110100", "10000011110010000111", };
        String[] expected = { "100", "01100", };

        for (int i=0; i<inputs.length; ++i) {
            assertEquals("computeChecksum() incorrect", expected[i], computeChecksum(inputs[i]));
        }
    }

    @Test
    public void testStarOne() {
        String input = "11101000110010100";
        String expected = "10100101010101101";
        assertEquals("getChecksum() incorrect", expected, getChecksum(input, 272));
    }

    @Test
    public void testStarTwo() {
        String input = "11101000110010100";
        String expected = "01100001101101001";
        assertEquals("getChecksum() incorrect", expected, getChecksum(input, 35651584));
    }
}
