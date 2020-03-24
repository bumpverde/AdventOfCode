package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * --- Day 17: Two Steps Forward ---
 * You're trying to access a secure vault protected by a 4x4 grid of small rooms connected by doors. 
 * You start in the top-left room (marked S), and you can access the vault (marked V) once you reach the bottom-right room:
 * 
 * #########
 * #S| | | #
 * #-#-#-#-#
 * # | | | #
 * #-#-#-#-#
 * # | | | #
 * #-#-#-#-#
 * # | | |  
 * ####### V
 * 
 * Fixed walls are marked with #, and doors are marked with - or |.
 * 
 * The doors in your current room are either open or closed (and locked) based on the hexadecimal MD5 hash of a passcode 
 * (your puzzle input) followed by a sequence of uppercase characters representing the path you have taken so far (U for up, D for down, L for left, and R for right).
 * 
 * Only the first four characters of the hash are used; they represent, respectively, the doors up, down, left, and right from your current position.
 * Any b, c, d, e, or f means that the corresponding door is open; any other character (any number or a) means that the corresponding door is closed and locked.
 * 
 * To access the vault, all you need to do is reach the bottom-right room; reaching this room opens the vault and all doors in the maze.
 * 
 * For example, suppose the passcode is hijkl. Initially, you have taken no steps, and so your path is empty: 
 * you simply find the MD5 hash of hijkl alone. The first four characters of this hash are ced9, which indicate 
 * that up is open (c), down is open (e), left is open (d), and right is closed and locked (9). Because you start 
 * in the top-left corner, there are no "up" or "left" doors to be open, so your only choice is down.
 * 
 * Next, having gone only one step (down, or D), you find the hash of hijklD. This produces f2bc, which indicates 
 * that you can go back up, left (but that's a wall), or right. Going right means hashing hijklDR to get 5745 - all doors closed and locked. 
 * However, going up instead is worthwhile: even though it returns you to the room you started in, your path would then be DU, opening a different set of doors.
 * 
 * After going DU (and then hashing hijklDU to get 528e), only the right door is open; after going DUR, all doors lock. (Fortunately, your actual passcode is not hijkl).
 * 
 * Passcodes actually used by Easter Bunny Vault Security do allow access to the vault if you know the right path. For example:
 * - If your passcode were ihgpwlah, the shortest path would be DDRRRD.
 * - With kglvqrro, the shortest path would be DDUDRLRRUDRD.
 * - With ulqzkmiv, the shortest would be DRURDRUDDLLDLUURRDULRLDUUDDDRR.
 * 
 * Given your vault's passcode, what is the shortest path (the actual path, not just the length) to reach the vault?
 * 
 * Your puzzle answer was DDURRLRRDD.
 * 
 * --- Part Two ---
 * You're curious how robust this security solution really is, and so you decide to find longer and longer paths which still provide access to the vault. 
 * You remember that paths always end the first time they reach the bottom-right room (that is, they can never pass through it, only end in it).
 * 
 * For example:
 * - If your passcode were ihgpwlah, the longest path would take 370 steps.
 * - With kglvqrro, the longest path would be 492 steps long.
 * - With ulqzkmiv, the longest path would be 830 steps long.
 * 
 * What is the length of the longest path that reaches the vault?
 * 
 * Your puzzle answer was 436.
 * 
 * @author bumpverde
 */
public class Y2016D17 {
    public static class Room {
        int x, y, level;
        String path;

        public Room(int x, int y) {
            this (x, y, 0);
        }

        public Room(int x, int y, int level) {
            this(x, y, level, "");
        }
        
        public Room(int x, int y, int level, String path) {
            this.x = x;
            this.y = y;
            this.level = level;
            this.path = path;
        }
        
        public Room goLeft() {
            return (x > 0) ? new Room(x-1, y, level + 1, path + "L") : null;
        }
        public Room goRight() {
            return (x < 3) ? new Room(x+1, y, level + 1, path + "R") : null;
        }
        public Room goUp() {
            return (y > 0) ? new Room(x, y-1, level + 1, path + "U") : null;    // Y axis is inverted, so up is minus
        }
        public Room goDown() {
            return (y < 3) ? new Room(x, y+1, level + 1, path + "D") : null;    // Y axis is inverted, so down is plus
        }

        public boolean sameCoords(Room rhs) {
            return (rhs != null) && (x == rhs.x) && (y == rhs.y);
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + path.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof Room)) {
                return false;
            }
            Room rhs = (Room) obj;
            return (x == rhs.x) && (y == rhs.y) && path.equals(rhs.path);    // Q: should we include path in this? i think so... 
        }
        
        @Override
        public String toString() {
            return String.format("(%d, %d, %d, %s)", x, y, level, path);
        }
    }
    
    public static class Map {
        String passcode;
        
        public Map(String passcode) {
            this.passcode = passcode;
        }
        
        public boolean isOpen(char c) {
            return (c == 'b') || (c == 'c') || (c == 'd') || (c == 'e') || (c == 'f');
        }
        
        
        public String getShortestPath(Room from, Room to) {
            return getAllPaths(from, to, true).stream().findFirst().orElse(null); 
        }
            
        public String getLongestPath(Room from, Room to) {
            return getAllPaths(from, to, false).stream().sorted((s1, s2) -> Integer.compare(s2.length(), s1.length())).findFirst().orElse(null); 
        }
            
        public Set<String> getAllPaths(Room from, Room to, boolean stopOnShortest) {
            Queue<Room> queue = new LinkedList<Room>();
            Set<Room> visited = new HashSet<Room>();
            Set<String> finalPaths = new HashSet<String>();
            
            BiConsumer<Room,Character> pusher = (room, c) -> {
//                System.out.printf("\tEvaluating %s with lock char '%c'...", room, c);
                if ((room != null) && isOpen(c) && !visited.contains(room)) {
                    // If we're recording all the paths, don't actually visit the vault, 
                    // but do record a final path if we see it.
                    if (!stopOnShortest && room.sameCoords(to)) {
                        finalPaths.add(room.path);
//                        System.out.printf(" Recording long path %s\n", room);
                    } else {
                        queue.add(room);
                        visited.add(room);
//                        System.out.printf(" Queueing\n", room);
                    }
                } else {
//                    System.out.printf(" Discarding\n", room);
                }
            };
            
            // Add the root (assume it is open, so pass 'b')
            pusher.accept(from, 'b');
            
            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Room next = queue.remove();
//                System.out.printf("Processing %s\n", next);
                
                // Visit the node to see if we're there yet.
                if (next.sameCoords(to)) {
                    finalPaths.add(next.path);
                    
                    if (stopOnShortest) {
                        return finalPaths;
                    }
                }
                
                // Compute the hash of the passcode and the node's path
                String hash = DigestUtils.md5Hex(passcode + next.path).toLowerCase();
                
                pusher.accept(next.goUp(), hash.charAt(0));
                pusher.accept(next.goDown(), hash.charAt(1));
                pusher.accept(next.goLeft(), hash.charAt(2));
                pusher.accept(next.goRight(), hash.charAt(3));
            }
            
            // Return what was discovered
            return finalPaths;
        }
    }
    
    @Test
    public void testRoomSets() {
        Set<Room> rooms = new HashSet<Room>();
        Room origin1 = new Room(0,0);
        Room origin2 = new Room(0,0);
        Room originWithLevel = new Room(0,0,10);
        rooms.add(origin1);
        assertTrue("incorrect set eval origin1", rooms.contains(origin1));
        assertTrue("incorrect set eval origin2", rooms.contains(origin2));
        assertTrue("incorrect set eval originWithLevel", rooms.contains(originWithLevel));
        
        Room origin1WithPath = new Room(10,20,0,"UD");
        Room origin2WithPath = new Room(10,20,0,"UD");
        Room originWithLevelPath = new Room(10,20,10, "UD");
        rooms.add(origin1WithPath);
        assertTrue("incorrect set eval origin1Path", rooms.contains(origin1WithPath));
        assertTrue("incorrect set eval origin2Path", rooms.contains(origin2WithPath));
        assertTrue("incorrect set eval originWithLevelPath", rooms.contains(originWithLevelPath));
    }
    
    @Test
    public void testGetShortestPath() {
        String[] inputs = { "hijkl", "ihgpwlah", "kglvqrro",  "ulqzkmiv", };
        String[] expected = { null, "DDRRRD", "DDUDRLRRUDRD", "DRURDRUDDLLDLUURRDULRLDUUDDDRR", };
        for (int i=0; i<inputs.length; ++i) {
            Map map = new Map(inputs[i]);
            assertEquals("getShortestPath() incorrect", expected[i], map.getShortestPath(new Room(0,0), new Room(3,3)));
        }
    }

    @Test
    public void testGetLongestPathLength() {
        String[] inputs = { "hijkl", "ihgpwlah", "kglvqrro",  "ulqzkmiv", };
        int[] expected = { -1, 370, 492, 830 };
        for (int i=0; i<inputs.length; ++i) {
            Map map = new Map(inputs[i]);
            String actual = map.getLongestPath(new Room(0,0), new Room(3,3));
            assertEquals("getLongestPathLength() incorrect", expected[i], (actual == null) ? -1 : actual.length());
        }
    }

    @Test
    public void testStarOne() {
        Map map = new Map("bwnlcvfs");
        assertEquals("testStarOne() incorrect", "DDURRLRRDD", map.getShortestPath(new Room(0,0), new Room(3,3)));
    }
    
    @Test
    public void testStarTwo() {
        Map map = new Map("bwnlcvfs");
        assertEquals("testStarOne() incorrect", 436, map.getLongestPath(new Room(0,0), new Room(3,3)).length());
    }
}


