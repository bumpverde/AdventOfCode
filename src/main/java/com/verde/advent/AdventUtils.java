package com.verde.advent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class AdventUtils {
    public static Collection<String> genSubStrings(String s, int subLen) {
        Collection<String> subs = new LinkedList<String>();
        for (int i=0; i<=s.length()-subLen; ++i) {
            subs.add(s.substring(i, i+subLen));
        }
        
        return subs;
    }
    
    public static String[] loadResourceStringsAsArray(String resourceName) {
        Collection<String> lines = loadResourceStrings(resourceName);
        return lines.toArray(new String[lines.size()]);
    }
    
    public static List<String> loadResourceStrings(String resourceName) {
        try (InputStream is = AdventUtils.class.getResourceAsStream(resourceName)) {
            return IOUtils.readLines(is, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<String>();
        }
    }
    
    public static <T> int indexOf(Collection<T> coll, T element) {
        int index = 0;
        for (T e : coll) {
            if (element.equals(e)) {
                return index;
            } 
            
            ++index;
        }
        
        return -1;
    };

    public static String toString(IntStream chars) {
        StringBuilder sb = new StringBuilder();

        chars.forEach(c -> sb.append((char) c));
        return sb.toString();
    }

    public static String toString(char c) {
        return new Character(c).toString();
    }
    
    public static char[] rotateLeft(char arr[], int count) {
        for (int i=0; i<count; i++) {
            rotateLeftByOne(arr); 
        }
        
        return arr;
    } 

    public static char[] rotateLeftByOne(char[] arr) {
        return rotateLeftByOne(arr, 0, arr.length-1);
    }
        
    public static char[] rotateLeftByOne(char[] arr, int startInclusive, int endInclusive) { 
        char temp = arr[startInclusive];
        
        for (int i=startInclusive; i<=endInclusive-1; ++i) { 
            arr[i] = arr[i + 1]; 
        }
        
        arr[endInclusive] = temp; 

        return arr;
    } 

    public static char[] rotateRight(char arr[], int count) {
        for (int i=0; i<count; i++) {
            rotateRightByOne(arr, 0, arr.length-1); 
        }
        
        return arr;
    } 
    
    public static char[] rotateRightByOne(char[] arr, int startInclusive, int endInclusive) { 
        char temp = arr[endInclusive];
        
        for (int i=endInclusive; i>=startInclusive+1; --i) { 
            arr[i] = arr[i - 1]; 
        }
        
        arr[startInclusive] = temp; 

        return arr;
    } 
    
    @Test
    public void testRotateLeftByOne() {
        char[] input = "abcdef".toCharArray();
        assertEquals("rotateLeftByOne(abcdef) incorrect", "bcdefa", new String(rotateLeftByOne(input)));

        input = "abcdef".toCharArray();
        assertEquals("rotateLeftByOne(abcdef, 1, 5) incorrect", "acdefb", new String(rotateLeftByOne(input, 1, 5)));

        input = "abcdef".toCharArray();
        assertEquals("rotateLeftByOne(abcdef, 2, 5) incorrect", "abdefc", new String(rotateLeftByOne(input, 2, 5)));

        input = "abcdef".toCharArray();
        assertEquals("rotateLeftByOne(abcdef, 3, 4) incorrect", "abcedf", new String(rotateLeftByOne(input, 3, 4)));
    }
    
    @Test
    public void testRotateRightByOne() {
        char[] input = "abcdef".toCharArray();
        assertEquals("rotateRightByOne(abcdef) incorrect", "fabcde", new String(rotateRightByOne(input, 0, 5)));

        input = "abcdef".toCharArray();
        assertEquals("rotateRightByOne(abcdef, 1, 5) incorrect", "afbcde", new String(rotateRightByOne(input, 1, 5)));

        input = "abcdef".toCharArray();
        assertEquals("rotateRightByOne(abcdef, 2, 5) incorrect", "abfcde", new String(rotateRightByOne(input, 2, 5)));

        input = "abcdef".toCharArray();
        assertEquals("rotateRightByOne(abcdef, 3, 4) incorrect", "abcedf", new String(rotateRightByOne(input, 3, 4)));
    }
    
    @Test
    public void testGenSubstrings() {
        String[] inputs = {
                "1234567890",
                "1234567890",
                "dnwtsgywerfamfv",
                "abcdefg",
        };
        int[] lengths = { 3, 8, 4, 1 };
        String[][] expected = {
                {
                    "123", "234", "345", "456", "567", "678", "789", "890"
                },
                {
                    "12345678", 
                },
                {
                    "dnwt", "nwts", "wtsg", "tsgy", "sgyw", "gywe", "ywer", "werf",
                },
                {
                    "a", "b", "c", "d", "e", "f", "g",
                }
        };
        
        for (int i=0; i<inputs.length; ++i) {
            Collection<String> subs = genSubStrings(inputs[i], lengths[i]);
            
            for (int j=0; j<expected[i].length; ++j) {
                String actual = expected[i][j];
                assertTrue("genSubStrings() is missing " + actual, subs.contains(actual));
            }
        }

    }

    public static int toInt(String s) {
        return Integer.parseInt(s.trim()); 
    }

    public static long toLong(String s) {
        return Long.parseLong(s.trim()); 
    }
}
