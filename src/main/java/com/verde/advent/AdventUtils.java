package com.verde.advent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
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

    /**
     * Convert a Collection of Strings to a 2D array of chars.
     *
     * @param lines the lines to convert
     * @return the 2D array of chars for the lines
     */
    public static char[][] toChars(Collection<String> lines) {
        String[] linesArray = lines.toArray(new String[lines.size()]);
        char[][] grid = new char[lines.size()][];
        for (int i = 0; i < linesArray.length; i++) {
            grid[i] = linesArray[i].toCharArray();
        }

        return grid;
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

    public static IntStream toIntStream(Collection<String> strings) {
        return strings.stream().mapToInt(s -> toInt(s));
    }

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

    /**
     * For the Advent 2025 (and maybe other years), we need to split string on spaces in a
     * way that retains the spaces after the separator. String.split() won't do this,
     * nor will StringUtils methods.
     *
     * @param line the input
     * @return the split parts, preserving extra spaces after the separator and surrounding the input
     */
    public static String[] splitForTranspose(String line) {
        String newForm = replaceFirstOccurences(line, ' ', '|', true);
        String[] parts = StringUtils.split(newForm, "|");

        return parts;
    }

    /**
     * Transpose the rows and columns of some strings.
     * This assumes that the number of columns is consistent across the rows.
     *
     * @param inputs the input strings of strings seperated by spaces
     * @return the transposed output
     */
    public static Collection<String> transpose(Collection<String> inputs) {
        StringBuilder[] builders = null;
        for (String line : inputs) {
            String[] parts = splitForTranspose(line);

            // Create the builders array if necessary
            if (builders == null) {
                builders = new StringBuilder[parts.length];
            }

            for (int i = 0; i < parts.length; i++) {
                if (builders[i] == null) {
                    builders[i] = new StringBuilder();
                }
                builders[i].append(parts[i]).append(' ');
            }
        }

        Collection<String> transposed = new ArrayList<>(builders.length);
        for (StringBuilder builder : builders) {
            transposed.add(builder.toString());
        }

        return transposed;
    }

    public static String prefixOfRepeating(String s, char c) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == c) {
            i++;
        }
        return s.substring(0, i);
    }

    public static String suffixOfRepeating(String s, char c) {
        int i = s.length() - 1;
        while (i >= 0 && s.charAt(i) == c) {
            i--;
        }
        return s.substring(i + 1);
    }

    /**
     * Replace the first occurence of a character with another, but do replace subsequent
     * consecutive occurrences. For instance calling this on "43 24  36" and replacing ' '
     * with '|', you'll get "43|24| 36"
     *
     * @param s the input
     * @return the resulting String
     */
    public static String replaceFirstOccurences(String s, char from, char to, boolean retainSurrounding) {
        StringBuilder sb = new StringBuilder();
        char lastChar = from;

        String prefix = retainSurrounding ? prefixOfRepeating(s, from) : "";
        String suffix = retainSurrounding ? suffixOfRepeating(s, from) : "";

        // If retaining the surrounding from chars, then get rid of them now because we'll add them back in
        if (retainSurrounding) {
            s = StringUtils.stripStart(s, "" + from);
            s = StringUtils.stripEnd(s, "" + from);
        }

        if (retainSurrounding && !prefix.isEmpty()) {
            sb.append(prefix);
        }

        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);
            if ((c != from) || (i > 0) && (lastChar == from)) {
                sb.append(c);
            } else {
                sb.append(to);
            }
            lastChar = c;
        }

        if (retainSurrounding && !suffix.isEmpty()) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    public static int nextIndexOf(String s, int startIndex, Predicate<Character> predicate) {
        for (int i=startIndex; i<s.length(); ++i) {
            Character c = s.charAt(i);
            if (predicate.test(c)) {
                return i;
            }
        }

        return -1;
    }

    public static Collection<String> transposeCephalapod(char[][] grid) {
        // Sanity check the lengths
        for (int row=0; row < grid.length-1; ++row) {
            System.out.printf("Line %d: %d\n", row, grid[row].length);
        }

            Collection<String> result = new LinkedList<>();
        int lineLength = grid[0].length;
        String operatorsLine = new String(grid[grid.length-1]);
        boolean done = false;
        int startIndex=0, stopIndex=-1;  // for the next column of data

        do {
            // Find the end of the current column, which will be two back from the next operator (skip the separator).
            // If there are no more operators, scan to the end of the operators line and we're done after this last piece.
            stopIndex = nextIndexOf(operatorsLine, startIndex+1, c -> (c == '*') || (c == '+')) - 2;
            if (stopIndex < 0) {
                stopIndex = lineLength - 1;  // was operatorsLine.length()-1, but that line is shorter than the others
                done = true;
            }

            // Now build the cephalapod values going top-to-bottom and left-to-right
            StringBuilder groupBuilder =  new StringBuilder();
            for (int col=stopIndex; col >= startIndex; --col) {
                StringBuilder colBuilder =  new StringBuilder();

                for (int row=0; row < grid.length-1; ++row) {
                    char c = grid[row][col];
                    if (c != ' ') {
                        colBuilder.append(c);
                    }
                }

                // Add the column output to the overall group
                groupBuilder.append(colBuilder.toString()).append(" ");
            }

            // Add on the operator and pop that onto the result
            groupBuilder.append(operatorsLine.charAt(startIndex));
            String group = groupBuilder.toString();
            result.add(group);
            System.out.printf("Group (%,d - %,d): %s\n", startIndex, stopIndex,  group);

            // Move on to the next group
            startIndex = stopIndex + 2;  // 2 because we have to skip the separator
        } while (!done);

        return result;
    }

        @Test
    public void testTranspose() {
        Collection<String> inputs =
                List.of("123 328  51 64 ",
                        " 45 64  387 23 ",
                        "  6 98  215 314",
                        "*   +   *   + ");
        Collection<String> expected =
                List.of("123 45 6 *",
                        "328 64 98 +",
                        "51 387 215 *",
                        "64 23 314 +");
        Collection<String> actual = transpose(inputs);
        assertEquals("transpose of strings failed", expected, actual);
    }

    @Test
    public void testReplaceFirstOccurence() {
        String[] inputs = {
                "123 328  51 64 "
                //        " 45 64  387 23 ",
                //      "  6 98  215 314"
        };
        String[] expected = {
                "123|328| 51|64 "
        };

        for (int i = 0; i < inputs.length; ++i) {
            String actual = replaceFirstOccurences(inputs[i], ' ', '|', true);
            assertEquals(String.format("replaceFirstOccurences() failed: '%s' vs '%s'", expected[i], actual), expected[i], actual);
        }
    }

    @Test
    public void testSplitRetainConsecutiveSep() {
        String[] inputs = {
                "123 328  51 64 "
        //        " 45 64  387 23 ",
          //      "  6 98  215 314"
        };
        String[][] expected = {
                {"123", "328", " 51", "64 "}
        };

        for (int i = 0; i < inputs.length; ++i) {
            String[] actual = StringUtils.split(inputs[i], " ");
            //String[] actual = StringUtils.splitByWholeSeparator(inputs[i], " ");
            //String[] actual = inputs[i].split(" (?=\\S)");
            assertEquals(String.format("transpose of strings failed: '%s' vs '%s'", expected[i], actual), expected[i], actual);
        }
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
