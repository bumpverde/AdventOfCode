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


