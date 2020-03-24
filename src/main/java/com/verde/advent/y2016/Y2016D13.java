package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * --- Day 13: A Maze of Twisty Little Cubicles ---
 * You arrive at the first floor of this new building to discover a much less welcoming environment than the shiny atrium of the last one. 
 * Instead, you are in a maze of twisty little cubicles, all alike.
 * 
 * Every location in this area is addressed by a pair of non-negative integers (x,y). Each such coordinate is either a wall or an open space. 
 * You can't move diagonally. The cube maze starts at 0,0 and seems to extend infinitely toward positive x and y; negative values are invalid, 
 * as they represent a location outside the building. You are in a small waiting area at 1,1.
 * 
 * While it seems chaotic, a nearby morale-boosting poster explains, the layout is actually quite logical. 
 * You can determine whether a given x,y coordinate will be a wall or an open space using a simple system:
 * 
 * - Find x*x + 3*x + 2*x*y + y + y*y.
 * - Add the office designer's favorite number (your puzzle input).
 * - Find the binary representation of that sum; count the number of bits that are 1.
 *   - If the number of bits that are 1 is even, it's an open space.
 *   - If the number of bits that are 1 is odd, it's a wall.
 * 
 * For example, if the office designer's favorite number were 10, drawing walls as # and open spaces as ., the corner of the building containing 0,0 would look like this:
 * 
 *   0123456789
 * 0 .#.####.##
 * 1 ..#..#...#
 * 2 #....##...
 * 3 ###.#.###.
 * 4 .##..#..#.
 * 5 ..##....#.
 * 6 #...##.###
 * 
 * Now, suppose you wanted to reach 7,4. The shortest route you could take is marked as O:
 * 
 *   0123456789
 * 0 .#.####.##
 * 1 .O#..#...#
 * 2 #OOO.##...
 * 3 ###O#.###.
 * 4 .##OO#OO#.
 * 5 ..##OOO.#.
 * 6 #...##.###
 * 
 * Thus, reaching 7,4 would take a minimum of 11 steps (starting from your current location, 1,1).
 * 
 * What is the fewest number of steps required for you to reach 31,39?
 * 
 * Your puzzle answer was 82.
 * 
 * --- Part Two ---
 * How many locations (distinct x,y coordinates, including your starting location) can you reach in at most 50 steps?
 * 
 * Your puzzle answer was 138.
 * 
 * @author bumpperde
 */
public class Y2016D13 {
    public static class Room {
        int x, y, level;

        public Room(int x, int y) {
            this (x, y, 0);
        }

        public Room(int x, int y, int level) {
            this.x = x;
            this.y = y;
            this.level = level;
        }
        
        public Room goLeft() {
            return (x > 0) ? new Room(x-1, y, level + 1) : null;
        }
        public Room goRight() {
            return new Room(x+1, y, level + 1);
        }
        public Room goUp() {
            return new Room(x, y+1, level + 1);
        }
        public Room goDown() {
            return (y > 0) ? new Room(x, y-1, level + 1) : null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof Room)) {
                return false;
            }
            Room rhs = (Room) obj;
            return (x == rhs.x)&& (y == rhs.y); 
        }
        
        @Override
        public String toString() {
            return String.format("(%d, %d, %d)", x, y, level);
        }
    }
    
    public static class Map {
        int designerNumber;
        
        public Map(int designerNumber) {
            this.designerNumber = designerNumber;
        }
        
        public boolean isOpen(Room r) {
            int value = (r.x*r.x) + (3 * r.x) + (2 * r.x * r.y) + r.y + (r.y*r.y) + designerNumber;
            
            int numBitsOn = getNumBitsOn(value);
            boolean isEven = (numBitsOn % 2) == 0;
            return isEven;
        }
        
        public int getShortestDistance(Room from, Room to) {
            Queue<Room> queue = new LinkedList<Room>();
            Set<Room> visited = new HashSet<Room>();
            
            Consumer<Room> pusher = (room) -> {
                if ((room != null) && isOpen(room) && !visited.contains(room)) {
                    queue.add(room);
                    visited.add(room);
                }
            };
            
            // Add the root
            pusher.accept(from);
            
            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Room next = queue.remove();
                System.out.printf("Processing %s\n", next);
                
                // Visit the node to see if we're there yet
                if (next.equals(to)) {
                    return next.level;
                }
                
                pusher.accept(next.goLeft());
                pusher.accept(next.goRight());
                pusher.accept(next.goUp());
                pusher.accept(next.goDown());
            }
            
            // Couldn't get from here to there
            return -1;
        }
    }
    
    public static int getNumBitsOn(int value) {
        int numBitsOn = 0;
        for (int bit=0; bit<32; ++bit) {
            if ((value & (1 << bit)) != 0) {
                ++numBitsOn; 
            }
        }

        return numBitsOn;
    }
    
    @Test
    public void testGetShortestDistance() {
        Map map = new Map(10);
        assertEquals("getShortestDistance() incorrect", 11, map.getShortestDistance(new Room(1,1), new Room(7,4)));
    }

    @Test
    public void testStarOne() {
        Map map = new Map(1362);
        assertEquals("testStarOne() incorrect", 82, map.getShortestDistance(new Room(1,1), new Room(31,39)));
    }

    // testStarTwo() I hand-counted up to 50, and the answer is 138
    
    @Test
    public void testGetNumBitsOn() {
        int[] inputs = {
                1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048,
                1-1, 2-1, 4-1, 8-1, 16-1, 32-1, 64-1, 128-1, 256-1, 512-1, 1024-1, 2048-1,
                3, 5, 65535
        };
        int[] expected = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                2, 2, 16
        };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("getNumBitsOn(%d) is inocrrect", inputs[i]), expected[i], getNumBitsOn(inputs[i]));
        }
    }
}


