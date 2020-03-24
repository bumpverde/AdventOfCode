package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.verde.advent.Point2D;

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
