package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.verde.advent.Point2D;

/**
 * --- Day 1: No Time for a Taxicab ---
 * Santa's sleigh uses a very high-precision clock to guide its movements, and the clock's oscillator is regulated by stars. 
 * Unfortunately, the stars have been stolen... by the Easter Bunny. To save Christmas, Santa needs you to retrieve all fifty 
 * stars by December 25th.
 * 
 * Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar; the second puzzle 
 * is unlocked when you complete the first. Each puzzle grants one star. Good luck!
 * 
 * You're airdropped near Easter Bunny Headquarters in a city somewhere. "Near", unfortunately, is as close as you can get - 
 * the instructions on the Easter Bunny Recruiting Document the Elves intercepted start here, and nobody had time to work them out further.
 * 
 * The Document indicates that you should start at the given coordinates (where you just landed) and face North. 
 * Then, follow the provided sequence: either turn left (L) or right (R) 90 degrees, then walk forward the given number of blocks, 
 * ending at a new intersection.
 * 
 * There's no time to follow such ridiculous instructions on foot, though, so you take a moment and work out the destination. 
 * Given that you can only walk on the street grid of the city, how far is the shortest path to the destination?
 * 
 * For example:
 * 
 * Following R2, L3 leaves you 2 blocks East and 3 blocks North, or 5 blocks away.
 * R2, R2, R2 leaves you 2 blocks due South of your starting position, which is 2 blocks away.
 * R5, L5, R5, R3 leaves you 12 blocks away.
 * How many blocks away is Easter Bunny HQ?
 * 
 * Your puzzle answer was 246.
 * 
 * --- Part Two ---
 * Then, you notice the instructions continue on the back of the Recruiting Document. Easter Bunny HQ is actually at the first location you visit twice.
 * 
 * For example, if your instructions are R8, R4, R4, R8, the first location you visit twice is 4 blocks away, due East.
 * 
 * How many blocks away is the first location you visit twice?
 * 
 * Your puzzle answer was 124.
 *  
 * @author bumpverde
 */
public class Y2016D01 {
    private static final String NORTH="north", SOUTH="south", EAST="east", WEST="west";
    private static final String RIGHT="R", LEFT="L";

    private static final HashMap<String, String> dirChangeMap = new HashMap<String,String>();
    static {
        dirChangeMap.put(NORTH + RIGHT, EAST);
        dirChangeMap.put(NORTH + LEFT, WEST);
        dirChangeMap.put(SOUTH + RIGHT, WEST);
        dirChangeMap.put(SOUTH + LEFT, EAST);
        dirChangeMap.put(EAST + RIGHT, SOUTH);
        dirChangeMap.put(EAST + LEFT, NORTH);
        dirChangeMap.put(WEST + RIGHT, NORTH);
        dirChangeMap.put(WEST + LEFT, SOUTH);
    };
    
    private static final Point2D NORTH_DIR = new Point2D(0, 1);
    private static final Point2D SOUTH_DIR = new Point2D(0, -1);
    private static final Point2D EAST_DIR = new Point2D(1, 0);
    private static final Point2D WEST_DIR = new Point2D(-1, 0);
    private static final HashMap<String, Point2D> vectorsByDir = new HashMap<String,Point2D>();
    static {
        vectorsByDir.put(NORTH, NORTH_DIR);
        vectorsByDir.put(SOUTH, SOUTH_DIR);
        vectorsByDir.put(EAST, EAST_DIR);
        vectorsByDir.put(WEST, WEST_DIR);
    };
    
    public static int getManhattanDistance(String path, boolean stopAtFirstDoubleVisit) {
        String[] steps = path.split(",");
        String currDir = NORTH;
        Point2D currPos = new Point2D(0, 0);
        Set<Point2D> visited = new HashSet<Point2D>();
        
        boolean stopped = false;
        for (String step : steps) {
            if (stopped) {
                break;
            }
            
            step = step.trim();
            String dir = step.substring(0, 1);
            int distance = Integer.parseInt(step.substring(1));
            
            // Determine new direction
            currDir = dirChangeMap.get(currDir + dir);
            
            // Determine the delta to apply based on new direction
            Point2D delta = vectorsByDir.get(currDir);
            
            // Update pos based on scaled delta
            for (int i=0; i<distance; ++i) {
                currPos = currPos.add(delta);
            
                // Should we stop?
                if (stopAtFirstDoubleVisit && visited.contains(currPos)) {
                    stopped = true;
                    break;
                }

                // Remember what we visit
                visited.add(currPos);
            }
        }
        
        return currPos.getManhattanDistanceToOrigin();
    }
    
    @Test
    public void testDistance() {
        String[] inputs = { 
                "R2, L3",
                "R2, R2, R2",
                "R5, L5, R5, R3",
                INPUTS,
                "R8, R4, R4, R8",
                INPUTS,
        };
        boolean [] stopEarly = { false, false, false, false, true, true };
        int[] expected = { 5, 2, 12, 246, 4, 124 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getManhattanDistance() incorrect", expected[i], getManhattanDistance(inputs[i], stopEarly[i]));
        }
    }

    public static String INPUTS = "R2, L3, R2, R4, L2, L1, R2, R4, R1, L4, L5, R5, R5, R2, R2, R1, L2, L3, L2, L1, R3, L5, R187, R1, R4, L1, R5, L3, L4, R50, L4, R2, R70, L3, L2, R4, R3, R194, L3, L4, L4, L3, L4, R4, R5, L1, L5, L4, R1, L2, R4, L5, L3, R4, L5, L5, R5, R3, R5, L2, L4, R4, L1, R3, R1, L1, L2, R2, R2, L3, R3, R2, R5, R2, R5, L3, R2, L5, R1, R2, R2, L4, L5, L1, L4, R4, R3, R1, R2, L1, L2, R4, R5, L2, R3, L4, L5, L5, L4, R4, L2, R1, R1, L2, L3, L2, R2, L4, R3, R2, L1, L3, L2, L4, L4, R2, L3, L3, R2, L4, L3, R4, R3, L2, L1, L4, R4, R2, L4, L4, L5, L1, R2, L5, L2, L3, R2, L2";
}
