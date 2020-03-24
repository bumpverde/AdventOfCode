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
