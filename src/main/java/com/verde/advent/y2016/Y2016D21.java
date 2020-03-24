package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 21: Scrambled Letters and Hash ---
 * The computer system you're breaking into uses a weird scrambling function to store its passwords. 
 * It shouldn't be much trouble to create your own scrambled password so you can add it to the system; you just have to implement the scrambler.
 * 
 * The scrambling function is a series of operations (the exact list is provided in your puzzle input). 
 * Starting with the password to be scrambled, apply each operation in succession to the string. The individual operations behave as follows:
 * - swap position X with position Y means that the letters at indexes X and Y (counting from 0) should be swapped.
 * - swap letter X with letter Y means that the letters X and Y should be swapped (regardless of where they appear in the string).
 * - rotate left/right X steps means that the whole string should be rotated; for example, one right rotation would turn abcd into dabc.
 * - rotate based on position of letter X means that the whole string should be rotated to the right based on the index of letter X (counting from 0) 
 *   as determined before this instruction does any rotations. Once the index is determined, rotate the string to the right one time, plus a number of 
 *   times equal to that index, plus one additional time if the index was at least 4.
 * - reverse positions X through Y means that the span of letters at indexes X through Y (including the letters at X and Y) should be reversed in order.
 * - move position X to position Y means that the letter which is at index X should be removed from the string, then inserted such that it ends up at index Y.
 * 
 * For example, suppose you start with abcde and perform the following operations:
 * - swap position 4 with position 0 swaps the first and last letters, producing the input for the next step, ebcda.
 * - swap letter d with letter b swaps the positions of d and b: edcba.
 * - reverse positions 0 through 4 causes the entire string to be reversed, producing abcde.
 * - rotate left 1 step shifts all letters left one position, causing the first letter to wrap to the end of the string: bcdea.
 * - move position 1 to position 4 removes the letter at position 1 (c), then inserts it at position 4 (the end of the string): bdeac.
 * - move position 3 to position 0 removes the letter at position 3 (a), then inserts it at position 0 (the front of the string): abdec.
 * - rotate based on position of letter b finds the index of letter b (1), then rotates the string right once plus a number of times equal to that index (2): ecabd.
 * - rotate based on position of letter d finds the index of letter d (4), then rotates the string right once, plus a number of times equal to that index, 
 *   plus an additional time because the index was at least 4, for a total of 6 right rotations: decab.
 *   
 * After these steps, the resulting scrambled password is decab.
 * 
 * Now, you just need to generate a new scrambled password and you can access the system. Given the list of scrambling operations 
 * in your puzzle input, what is the result of scrambling abcdefgh?
 * 
 * Your puzzle answer was agcebfdh.
 * 
 * The first half of this puzzle is complete! It provides one gold star: *
 * 
 * --- Part Two ---
 * You scrambled the password correctly, but you discover that you can't actually modify the password file on the system. 
 * You'll need to un-scramble one of the existing passwords by reversing the scrambling process.
 * 
 * What is the un-scrambled version of the scrambled password fbgdceah?
 * 
 * @author bumpverde
 */
public class Y2016D21 {
    public static class Scrambler {
        String regex;
        Pattern pattern;
        BiFunction<String,Matcher,String> scrambler;
        BiFunction<String,Matcher,String> unscrambler;
        
        public Scrambler(String regex, BiFunction<String,Matcher,String> scrambler) {
            this(regex, scrambler, scrambler);
        }

        public Scrambler(String regex, BiFunction<String,Matcher,String> scrambler, BiFunction<String,Matcher,String> unscrambler) {
            this.regex = regex;
            this.pattern = Pattern.compile(regex);
            this.scrambler = scrambler;
            this.unscrambler = unscrambler;
        }

        public String scramble(String s, String instruction) {
            Matcher m = pattern.matcher(instruction);
            return m.matches() ? scrambler.apply(s,  m) : null;
        }

        public String unscramble(String s, String instruction) {
            Matcher m = pattern.matcher(instruction);
            return m.matches() ? unscrambler.apply(s,  m) : null;
        }
    }
    
    public static int indexOf(String s, String substr) {
        return s.indexOf(substr);
    }

    public static char[] swap(char[] chars, int p1, int p2) {
        char tmp = chars[p1];
        chars[p1] = chars[p2];
        chars[p2] = tmp;
        return chars;
    }

    public static char[] reverse(char[] chars, int p1, int p2) {
        ArrayUtils.reverse(chars, p1, p2+1);
        return chars;
    }
    
    public static char[] move(char[] chars, int p1, int p2) {
        if (p1 == p2) {
            return chars;
        }
        
        return  (p1 < p2)  
                ? AdventUtils.rotateLeftByOne(chars, p1, p2) 
                : AdventUtils.rotateRightByOne(chars, p2, p1);
    }

    public static char[] rotate(char[] chars, int index) {
        int count = 1 + index;
        if (index >= 4) {
            ++count;
        }
        
        return AdventUtils.rotateRight(chars, count);
    }
    
    public static Collection<Scrambler> getScramblers() {
        Collection<Scrambler> scramblers = new LinkedList<Scrambler>();
        
        // Match specs like "swap position 4 with position 0".
        // Unscramble by re-applying the scrambler.
        scramblers.add(new Scrambler("swap position (\\d*) with position (\\d*)", (s, m) -> {
            return new String(swap(s.toCharArray(), AdventUtils.toInt(m.group(1)), AdventUtils.toInt(m.group(2))));
        }));
        
        // Match specs like "swap letter d with letter b"
        // Unscramble by re-applying the scrambler.
        scramblers.add(new Scrambler("swap letter (.) with letter (.)", (s, m) -> {
            return new String(swap(s.toCharArray(), indexOf(s, m.group(1)), indexOf(s, m.group(2))));
        }));
        
        // reverse positions 0 through 4
        // Unscramble by re-applying the scrambler.
        scramblers.add(new Scrambler("reverse positions (\\d*) through (\\d*)", (s, m) -> {
            return new String(reverse(s.toCharArray(), AdventUtils.toInt(m.group(1)), AdventUtils.toInt(m.group(2))));
        }));
        
        // rotate left 1 step
        // rotate left 4 steps
        scramblers.add(new Scrambler("rotate left (\\d*) .*", (s, m) -> {
            return new String(AdventUtils.rotateLeft(s.toCharArray(), AdventUtils.toInt(m.group(1))));
        }, (s, m) -> {
            return new String(AdventUtils.rotateRight(s.toCharArray(), AdventUtils.toInt(m.group(1))));
        }));
        
        // rotate right 1 step
        // rotate right 4 steps
        scramblers.add(new Scrambler("rotate right (\\d*) .*", (s, m) -> {
            return new String(AdventUtils.rotateRight(s.toCharArray(), AdventUtils.toInt(m.group(1))));
        }, (s, m) -> {
            return new String(AdventUtils.rotateLeft(s.toCharArray(), AdventUtils.toInt(m.group(1))));
        }));
        
        // move position 1 to position 4
        // move position 3 to position 0
        // Unscramble by re-applying the scrambler.
        scramblers.add(new Scrambler("move position (\\d*) to position (\\d*)", (s, m) -> {
            return new String(move(s.toCharArray(), AdventUtils.toInt(m.group(1)), AdventUtils.toInt(m.group(2))));
        }));
        
        // rotate based on position of letter b
        scramblers.add(new Scrambler("rotate based on position of letter (.)", (s, m) -> {
            return new String(rotate(s.toCharArray(), indexOf(s, m.group(1))));
        }));
        
        return scramblers;
    }
    
    public static String getScrambled(String s, String[] instructions) {
        Collection<Scrambler> scramblers = getScramblers();
        
        // For each instruction, apply all the scramblers, returning the first non-null result
        for (String instruction : instructions) {
            String scrambled = null;
            
            // Get the first scrambled string from the scramblers, and use that.
            for (Scrambler scrambler  : scramblers) {
                scrambled = scrambler.scramble(s, instruction);

                if (scrambled != null) {
                    s = scrambled;
                    break;
                }
            }
            
            // If there are no matches, fire an alert
            if (scrambled == null) {
                System.out.printf("ERROR: no scramblers matched to work on instruction '%s' for input '%s'\n", instruction, s);
            }
        }
        
        // If we get here, there were no matches
        return s;
    }
    
    @Test
    public void testGetScrambled() {
        String[][] instructions = {
                {
                    "swap position 4 with position 0",
                    "swap letter d with letter b",
                    "reverse positions 0 through 4",
                    "rotate left 1 step",
                    "move position 1 to position 4",
                    "move position 3 to position 0",
                    "rotate based on position of letter b",
                    "rotate based on position of letter d"
                },
        };
        String[] inputs = { "abcde" };
        String[] expected = { "decab" };
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getScrambled() incorrect", expected[i], getScrambled(inputs[i], instructions[i]));
        }
    }
    
    @Test
    public void testScrambledStarOne() {
        String[] instructions = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        String input = "abcdefgh";
        String expected = "agcebfdh";
    
        assertEquals("getScrambledStarOne() incorrect", expected, getScrambled(input, instructions));
    }


    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day21-inputs.txt");
}
