package com.verde.advent.day3;

/**
 * --- Day 3: Spiral Memory ---
 * You come across an experimental new kind of memory stored on an infinite two-dimensional grid.
 * <p>
 * Each square on the grid is allocated in a spiral pattern starting at a location marked 1 and then 
 * counting up while spiraling outward. For example, the first few squares are allocated like this:
 * <p>
 * 17  16  15  14  13
 * 18   5   4   3  12
 * 19   6   1   2  11
 * 20   7   8   9  10
 * 21  22  23---> ...
 * <p>
 * While this is very space-efficient (no squares are skipped), requested data must be carried back to 
 * square 1 (the location of the only access port for this memory system) by programs that can only move 
 * up, down, left, or right. They always take the shortest path: the Manhattan Distance between the 
 * location of the data and square 1.
 * <p>
 * For example:
 * <p>
 * Data from square 1 is carried 0 steps, since it's at the access port.
 * Data from square 12 is carried 3 steps, such as: down, left, left.
 * Data from square 23 is carried only 2 steps: up twice.
 * Data from square 1024 must be carried 31 steps.
 * How many steps are required to carry the data from the square identified in your puzzle input all the way to the access port?
 * <p>
 * Your puzzle input is 325489.
 * 
 * NOTES:
 * - see https://en.wikipedia.org/wiki/Taxicab_geometry
 */
public class Day3 {
    public static class Point {
        public int x, y;
        Point() {
            x = y = 0;
        }
    }
    
    public static class Shell {
        public int min, max, count, level; 
        public Point[] points;
        
        public Shell(int location) {
            // Compute min, count, and level for the location
            min = 1;
            level = 0;
            count = 1; 
            while ((location -= count) > 0) {
                ++level;
                min += count;
                count = (level == 1) ? 8 : count + 8; 
            }
            
            // Set the max based on min and count
            max = min + count - 1;

            // Populate the points
            points = new Point[count];
        }
    }
    
}
