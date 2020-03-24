package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.verde.advent.AdventUtils;
import com.verde.advent.Point2D;

/**
 * --- Day 2: Bathroom Security ---
 * You arrive at Easter Bunny Headquarters under cover of darkness. However, you left in such a rush that you forgot to use the bathroom! 
 * Fancy office buildings like this one usually have keypad locks on their bathrooms, so you search the front desk for the code.
 * 
 * "In order to improve security," the document you find says, "bathroom codes will no longer be written down. 
 * Instead, please memorize and follow the procedure below to access the bathrooms."
 * 
 * The document goes on to explain that each button to be pressed can be found by starting on the previous button and 
 * moving to adjacent buttons on the keypad: U moves up, D moves down, L moves left, and R moves right. Each line of 
 * instructions corresponds to one button, starting at the previous button (or, for the first line, the "5" button); 
 * press whatever button you're on at the end of each line. If a move doesn't lead to a button, ignore it.
 * 
 * You can't hold it much longer, so you decide to figure out the code as you walk to the bathroom. You picture a keypad like this:
 * 
 * 1 2 3
 * 4 5 6
 * 7 8 9
 * 
 * Suppose your instructions are:
 * 
 * ULL
 * RRDDD
 * LURDL
 * UUUUD
 * 
 * - You start at "5" and move up (to "2"), left (to "1"), and left (you can't, and stay on "1"), so the first button is 1.
 * - Starting from the previous button ("1"), you move right twice (to "3") and then down three times (stopping at "9" after two moves and ignoring the third), ending up with 9.
 * - Continuing from "9", you move left, up, right, down, and left, ending with 8.
 * - Finally, you move up four times (stopping at "2"), then down once, ending with 5.
 * 
 * So, in this example, the bathroom code is 1985.
 * 
 * Your puzzle input is the instructions from the document you found at the front desk. What is the bathroom code?
 * 
 * Your puzzle answer was 65556.
 * 
 * --- Part Two ---
 * You finally arrive at the bathroom (it's a several minute walk from the lobby so visitors can behold the many fancy conference 
 * rooms and water coolers on this floor) and go to punch in the code. Much to your bladder's dismay, the keypad is not at all like 
 * you imagined it. Instead, you are confronted with the result of hundreds of man-hours of bathroom-keypad-design meetings:
 * 
 *     1
 *   2 3 4
 * 5 6 7 8 9
 *   A B C
 *     D
 * You still start at "5" and stop when you're at an edge, but given the same instructions as above, the outcome is very different:
 * 
 * - You start at "5" and don't move at all (up and left are both edges), ending at 5.
 * - Continuing from "5", you move right twice and down three times (through "6", "7", "B", "D", "D"), ending at D.
 * - Then, from "D", you move five more times (through "D", "B", "C", "C", "B"), ending at B.
 * - Finally, after five more moves, you end at 3.
 * 
 * So, given the actual keypad layout, the code would be 5DB3.
 * 
 * Using the same instructions in your puzzle input, what is the correct bathroom code?
 * 
 * Your puzzle answer was CB779.
 *  
 * @author bumpverde
 */
public class Y2016D02 {
    private static final String NORTH="north", SOUTH="south", EAST="east", WEST="west";
    private static final String RIGHT="R", LEFT="L", UP="U", DOWN="D";

    private static final Point2D UP_DIR = new Point2D(0, 1);
    private static final Point2D DOWN_DIR = new Point2D(0, -1);
    private static final Point2D RIGHT_DIR = new Point2D(1, 0);
    private static final Point2D LEFT_DIR = new Point2D(-1, 0);
    private static final HashMap<String, Point2D> vectorsByDir = new HashMap<String,Point2D>();
    static {
        vectorsByDir.put(UP, UP_DIR);
        vectorsByDir.put(DOWN, DOWN_DIR);
        vectorsByDir.put(RIGHT, RIGHT_DIR);
        vectorsByDir.put(LEFT, LEFT_DIR);
    };
    
    private static final HashMap<String, Point2D> STD_KEYS = new HashMap<String,Point2D>();
    static {
        STD_KEYS.put("1", new Point2D(0, 2));
        STD_KEYS.put("2", new Point2D(1, 2));
        STD_KEYS.put("3", new Point2D(2, 2));
        STD_KEYS.put("4", new Point2D(0, 1));
        STD_KEYS.put("5", new Point2D(1, 1));
        STD_KEYS.put("6", new Point2D(2, 1));
        STD_KEYS.put("7", new Point2D(0, 0));
        STD_KEYS.put("8", new Point2D(1, 0));
        STD_KEYS.put("9", new Point2D(2, 0));
    };
    
    private static final HashMap<String, Point2D> CRAZY_KEYS = new HashMap<String,Point2D>();
    static {
        CRAZY_KEYS.put("1", new Point2D(2, 4));
        CRAZY_KEYS.put("2", new Point2D(1, 3));
        CRAZY_KEYS.put("3", new Point2D(2, 3));
        CRAZY_KEYS.put("4", new Point2D(3, 3));
        CRAZY_KEYS.put("5", new Point2D(0, 2));
        CRAZY_KEYS.put("6", new Point2D(1, 2));
        CRAZY_KEYS.put("7", new Point2D(2, 2));
        CRAZY_KEYS.put("8", new Point2D(3, 2));
        CRAZY_KEYS.put("9", new Point2D(4, 2));
        CRAZY_KEYS.put("A", new Point2D(1, 1));
        CRAZY_KEYS.put("B", new Point2D(2, 1));
        CRAZY_KEYS.put("C", new Point2D(3, 1));
        CRAZY_KEYS.put("D", new Point2D(2, 0));
    };
    
    public static boolean inRange(int min, int max, int val) {
        return (val >= min) && (val <= max);
    }
    
    public static String getCode(String[] inputs, String startingKey, Map<String, Point2D> ptsByDigit) {
        // Build a reverse map
        Map<Point2D, String> digitsByPt = ptsByDigit.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey(), (k1, k2) -> k1));
        
        Point2D currPos = ptsByDigit.get(startingKey);
        String code = "";
        
        for (String path: inputs) {
            for (int i=0; i<path.length(); ++i) {
                String dir = path.substring(i, i+1);
            
                // Determine the delta to apply based on direction
                Point2D delta = vectorsByDir.get(dir);

                // Compute new pos based on delta
                Point2D pos = currPos.add(delta);

                // Update if in range, meaning, if there is a key for the pos
                if (digitsByPt.get(pos) != null) {
                    currPos = pos.copy();
                }
            }
            
            // Update code to be the last digit
            code += digitsByPt.get(currPos);
        }
        
        return code;
    }
    
    @Test
    public void testGetCode() {
        String[][] inputs = {
                {
                    "ULL",
                    "RRDDD",
                    "LURDL",
                    "UUUUD",
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
                {
                    "ULL",
                    "RRDDD",
                    "LURDL",
                    "UUUUD",
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
        };
        
        boolean[] stdKeys = { true, true, false, false };
        String[] expected = { "1985", "65556", "5DB3", "CB779" };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getCode() incorrect", expected[i], getCode(inputs[i], "5", stdKeys[i] ? STD_KEYS : CRAZY_KEYS));
        }
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day2-inputs.txt");
}
