package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D24 {
    public static class Room {
        int x, y, level;
        String path;
        String numbersSeen;// comma separated

        public Room(int x, int y) {
            this (x, y, 0);
        }

        public Room(int x, int y, int level) {
            this(x, y, level, "",  "");
        }
        
        public Room(int x, int y, int level, String path, String numbersSeen) {
            this.x = x;
            this.y = y;
            this.level = level;
            this.path = path;
            this.numbersSeen = numbersSeen;
        }
        
        public Room goLeft() {
            return new Room(x-1, y, level + 1, path + "L", numbersSeen);
        }
        public Room goRight() {
            return new Room(x+1, y, level + 1, path + "R", numbersSeen);
        }
        public Room goUp() {
            return new Room(x, y-1, level + 1, path + "U", numbersSeen);    // Y axis is inverted, so up is minus
        }
        public Room goDown() {
            return new Room(x, y+1, level + 1, path + "D", numbersSeen);    // Y axis is inverted, so down is plus
        }

        public String getUncycledPath() {
            String uncycled = path;
            
            int currLen;
            do {
                currLen = uncycled.length();
                uncycled = StringUtils.replace(uncycled, "RL", "");
                uncycled = StringUtils.replace(uncycled, "LR", "");
                uncycled = StringUtils.replace(uncycled, "UD", "");
                uncycled = StringUtils.replace(uncycled, "DU", "");
                uncycled = StringUtils.replace(uncycled, "RDLU", "");
                uncycled = StringUtils.replace(uncycled, "RULD", "");
                uncycled = StringUtils.replace(uncycled, "LURD", "");
                uncycled = StringUtils.replace(uncycled, "LDRU", "");
                uncycled = StringUtils.replace(uncycled, "DRUL", "");
                uncycled = StringUtils.replace(uncycled, "DLUR", "");
                uncycled = StringUtils.replace(uncycled, "URDL", "");
                uncycled = StringUtils.replace(uncycled, "ULDR", "");
            } while (currLen != uncycled.length());
            
            return uncycled;
        }
        
        public boolean sameCoords(Room rhs) {
            return (rhs != null) && (x == rhs.x) && (y == rhs.y);
        }
        
        public int manhattanDistance(Room r) {
            return (r == null) ? 0 : Math.abs(r.x - x) + Math.abs(r.y - y);
        }
        
        public boolean addNumber(char number) {
            String s = AdventUtils.toString(number);
            
            if (! numbersSeen.contains(s)) {
                if (numbersSeen.isEmpty()) {
                    numbersSeen = s;
                } else {
                    numbersSeen = String.format("%s,%s", numbersSeen, s);
                }
                
                return true; // new number
            }
            
            return false;    // not a new number
        }
        
        public int getCountOfNumbersSeen() {
            return numbersSeen.isEmpty() ? 0 : StringUtils.countMatches(numbersSeen, ',') + 1;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
//            result = prime * result + path.hashCode();
//            for (String n : numbersSeen) {
//                result = prime * result + n.hashCode();
//            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof Room)) {
                return false;
            }
            Room rhs = (Room) obj;
            return 
                    (x == rhs.x) 
                    && (y == rhs.y); 
//                    && path.equals(rhs.path);    // Q: should we include path in this? i think so...
//                    && ( (numbersSeen.size() == rhs.numbersSeen.size()) && (SetUtils.union(numbersSeen,  rhs.numbersSeen).size() == numbersSeen.size()));
        }
        
        @Override
        public String toString() {
            return String.format("(%d, %d, %d, %s, [%s])", x, y, level, path, numbersSeen);
        }
        
//        public static String toDB(Room r) {
//            // Don't serialize as empty strings, since split() will compress those to nothing
//            return String.format("%d;%d;%d;%s;%s",  r.x, r.y, r.level, r.path.isEmpty() ? " " : r.path, r.numbersSeen.isEmpty() ? " " : r.numbersSeen);
//        }
//        
//        public static Room fromDB(String s) {
//            // Be sure to trim(), since we converted empty strings to a space in toDB()
//            String[] parts = StringUtils.split(s, ";");
//            return new Room(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), parts[3].trim(), parts[4].trim()); 
//        }
    }

//    public static class RoomConverter implements Converter<Room> {
//        @Override
//        public Room from(byte[] source) throws IOException {
//            return Room.fromDB(new String(source));
//        }
//
//        @Override
//        public void toStream(Room room, OutputStream sink) throws IOException {
//            sink.write(Room.toDB(room).getBytes());
//        }
//    }
    
    public static class Map {
        String[] contents;
        int xMax, yMax; // exclusive max's
        Set<String> numbers = new HashSet<String>();
        Room start = null;
        
        public Map(String[] inputs) {
            contents = inputs;
            xMax= inputs[0].length();
            yMax= inputs.length;
            
            // Find all the numbers and remember where the "0" is
            for (int x=0; x<xMax; ++x) {
                for (int y=0; y<yMax; ++y) {
                    Room r = new Room(x, y);
                    
                    if (containsNumber(r)) {
                        String num = AdventUtils.toString(roomContents(r)); 
                        
                        numbers.add(num);
                        if ("0".equals(num)) {
                            start = r;
                        }
                    }
                }
            }
            
            System.out.printf("Initted map - xMax=%d, yMax=%d, start=%s, numbers=[%s]\n", xMax, yMax, start, StringUtils.join(numbers, ","));
        }
        
        public boolean inRange(Room r) {
            return (r.x >= 0) && (r.x < xMax) && (r.y >= 0) && (r.y < yMax);  
        }
        
        public char roomContents(Room r) {
            return contents[r.y].charAt(r.x);
        }

        public boolean isOpen(Room r) {
            return roomContents(r) != '#'; 
        }

        public boolean containsNumber(Room r) {
            return Character.isDigit(roomContents(r)); 
        }

//        public void process(Room room, ObjectQueue<Room> queue) throws Exception {
//            if (inRange(room) && isOpen(room)) {
//                queue.add(room);
////                System.out.printf("  Queuing %s\n", room);
//            } else {
////                System.out.printf("  Discarding %s\n", room);
//            }
//        }
        
        public void process(Room room, Queue<Room> queue) throws Exception {
            if (inRange(room) && isOpen(room)) {
                queue.add(room);
//                System.out.printf("  Queuing %s\n", room);
            } else {
//                System.out.printf("  Discarding %s\n", room);
            }
        }
        
        public int getShortestDistance() throws Exception {
            Queue<Room> queue = new LinkedList<Room>();
//            QueueFile file = new QueueFile.Builder(new File("C:/mapdb/tape")).build();
//            final ObjectQueue<Room> queue = ObjectQueue.create(file, new RoomConverter());
            
            System.out.printf("Starting with %s\n", start);
            
            // Add the root
            process(start, queue);
            
            int maxLevel = -1;
            int numProcessed = 0;
            
            // Do breadth-first traversal until we reach the destination
            Room next = null;
            while ((next = queue.peek()) != null) {
                queue.remove();
                ++numProcessed;
//                System.out.printf("Processing %s\n", next);
            
                // Update some progress indicator
                if (next.level > maxLevel) {
                    System.out.printf("\nLevel %d %s (%d processed)\n", next.level, next, numProcessed);
                    maxLevel = next.level;
                }
                if ((numProcessed % 1000) == 0) {
                    System.out.printf(".");
                }
                if ((numProcessed % 100000) == 0) {
                    System.out.printf(" queued=%d processed=%d visited=0\n", queue.size(), numProcessed);
                }
                
                // Visit the node to see if we're on a number
                if (containsNumber(next)) {
                    char num = roomContents(next);
                    
                    // Add it to the numbers seen for the room's path
                    next.addNumber(num);
                    
                    // If the room has seen all the numbers, it is the winner
                    if (next.getCountOfNumbersSeen() == numbers.size()) {
                        System.out.printf("Winner %s\n", next);
                        return next.level;
                    }
                }
                
                process(next.goLeft(), queue);
                process(next.goRight(), queue);
                process(next.goUp(), queue);
                process(next.goDown(), queue);
            }
            
            // Couldn't get from here to there
            return -1;
        }

        public interface Visitor<T> {
            /**
             * Visit a node along a given path, returning true to continue visitations
             * to children, or false to prune the visitation.
             * 
             * @param t the node
             * @return true to continue visiting along this path, false to stop
             */
            public boolean visit(T t);
        }
        
        public void visitDfs(Room r, Visitor<Room> visitor) {
            Set<String> visited = new HashSet<String>();
            
            // Kick off the visitation 
//            visitDfsWorker(r, visitor, visited);
            visitDfsIterative(r, visitor, visited);
        }

        private void visitDfsWorker(Room room, Visitor<Room> visitor, Set<String> visited) {
            // We want to prevent visitation back thru a room in the same direction
            String key = String.format("%d,%d,%s", room.x, room.y, room.getUncycledPath()); //room.path.isEmpty() ? "" : room.path.substring(room.path.length()-1));
            
            if ((room == null) || !inRange(room) || !isOpen(room) || visited.contains(key)) {
                return;
            }

//            System.out.printf("Processing %s, key=%s \n", room, key);
            visited.add(key);
            
            // Visit this node
            if (! visitor.visit(room)) {
                return;
            }
            
            // Recurse on the children, maintaining the path of parent nodes as we go
            visitDfsWorker(room.goLeft(), visitor, visited);
            visitDfsWorker(room.goRight(), visitor, visited);
            visitDfsWorker(room.goUp(), visitor, visited);
            visitDfsWorker(room.goDown(), visitor, visited);
        }

        private void visitDfsIterative(Room room, Visitor<Room> visitor, Set<String> visited) {
            Deque<Room> queue = new LinkedList<Room>();
            queue.addFirst(room);
            
            while ((room = queue.peekFirst()) != null) {
                queue.removeFirst();
                
                // We want to prevent visitation back thru a room in the same direction
                String key = String.format("%d,%d,%s", room.x, room.y, room.getUncycledPath()); //room.path.isEmpty() ? "" : room.path.substring(room.path.length()-1));

                if ((room == null) || !inRange(room) || !isOpen(room) || visited.contains(key)) {
                    continue;
                }

                System.out.printf("Processing %s, key=%s \n", room, key);
                visited.add(key);

                // Visit this node
                if (! visitor.visit(room)) {
                    continue;
                }

                // Push the children
                queue.addFirst(room.goDown());
                queue.addFirst(room.goUp());
                queue.addFirst(room.goRight());
                queue.addFirst(room.goLeft());
            }
        }

        public Collection<Room> getAllPathsDfs() {
            Collection<Room> paths = new LinkedList<Room>();
            AtomicInteger numProcessed = new AtomicInteger(0);

            System.out.printf("Starting with %s\n", start);

            // Visit all the nodes, checking each to see if we're on a number
            visitDfs(start, (room) -> {
                // Update progress indicator
                numProcessed.incrementAndGet();
//                System.out.printf("Processing %s\n", room);
                if ((numProcessed.get() % 1000) == 0) {
                    System.out.printf(".");
                }
                if ((numProcessed.get() % 100000) == 0) {
                    System.out.printf(" processed=%d\n", numProcessed.get());
                }

                if (containsNumber(room)) {
                    char num = roomContents(room);
                    
                    // Add it to the numbers seen for the room's path
                    if (room.addNumber(num)) {
                        System.out.printf("Visting number %c (%d seen so far, including this one): %s\n", num, room.getCountOfNumbersSeen(), room);
                    }
                    
                    // If the room has seen all the numbers, it is the winner, so stop further visits along this path
                    if (room.getCountOfNumbersSeen() == numbers.size()) {
                        System.out.printf("Winner %s\n", room);
                        paths.add(room);
                        return false;
                    }
                }
                
                return true;    // keep going
            });
            
            return paths;
        }

        public String getShortestPath() {
            return getAllPathsDfs().stream().findFirst().map(r -> r.path).orElse(null); 
        }
            
        public String getLongestPath() {
            return getAllPathsDfs().stream().sorted((r1, r2) -> Integer.compare(r2.level, r1.level)).findFirst().map(r -> r.path).orElse(null); 
        }
    }
    
    
    @Test
    public void testGetShortestDistance() throws Exception {
        String[] inputs = {
                "###########",
                "#0.1.....2#",
                "#.#######.#",
                "#4.......3#",
                "###########",
        };
                
        Map map = new Map(inputs);
//        assertEquals("getShortestDistance() incorrect", 14, map.getShortestDistance());
        assertEquals("getShortestPath() incorrect", 14, map.getShortestPath().length());
    }

//    @Test
//    public void testToFromDB() {
//        Consumer<Room> checkEquals = (r1) -> {
//            Room r2 = Room.fromDB(Room.toDB(r1));
//            assertEquals("x is incorrect", r1.x, r2.x);
//            assertEquals("y is incorrect", r1.y, r2.y);
//            assertEquals("level is incorrect", r1.level, r2.level);
//            assertEquals("path is incorrect", r1.path, r2.path);
//            assertEquals("numbersSeen is incorrect", r1.numbersSeen, r2.numbersSeen);
//        };
//
//        checkEquals.accept(new Room(1, 2, 3, "", "")); 
//        checkEquals.accept(new Room(1, 2, 3, "", "0,2,7")); 
//        checkEquals.accept(new Room(1, 2, 3, "UD", "")); 
//        checkEquals.accept(new Room(1, 2, 3, "UD", "0,2,7")); 
//    }
    
    @Test
    public void testStarOne() throws Exception {
        Map map = new Map(STAR_INPUTS);
        assertEquals("testStarOne() incorrect", 10, map.getShortestPath().length());
    }

    // testStarTwo() I hand-counted up to 50, and the answer is 138
    
//    public static void main(String[] args) throws Exception {
////        Thread.sleep(15000);
//        Map map = new Map(STAR_INPUTS);
//        System.out.printf("Shortest Distance: %d\n", map.getShortestDistance());
//    }
    
    public static final String[] STAR_INPUTS = AdventUtils.loadResourceStringsAsArray("/2016/day24-inputs.txt");
}


