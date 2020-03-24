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
//            STAR_INPUTS.toArray(new String[STAR_INPUTS.size()])
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
