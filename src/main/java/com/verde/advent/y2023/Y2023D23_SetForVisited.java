package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;

/**
 * --- Day 13, 2016: A Maze of Twisty Little Cubicles ---
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
public class Y2023D23_SetForVisited {
    // NOTE: Y2016D17.java has a longest path solution

    public static final char PATH = '.';
    public static final char FOREST = '#';
    public static final char SLOPE_UP = '^';
    public static final char SLOPE_DOWN = 'v';
    public static final char SLOPE_LEFT = '<';
    public static final char SLOPE_RIGHT = '>';
    public static final char UP = 'u';
    public static final char DOWN = 'd';
    public static final char LEFT = 'l';
    public static final char RIGHT = 'r';

    public static final String[] SAMPLE_INPUT_ARRAY = {
            "#.#####################",
            "#.......#########...###",
            "#######.#########.#.###",
            "###.....#.>.>.###.#.###",
            "###v#####.#v#.###.#.###",
            "###.>...#.#.#.....#...#",
            "###v###.#.#.#########.#",
            "###...#.#.#.......#...#",
            "#####.#.#.#######.#.###",
            "#.....#.#.#.......#...#",
            "#.#####.#.#.#########v#",
            "#.#...#...#...###...>.#",
            "#.#.#v#######v###.###v#",
            "#...#.>.#...>.>.#.###.#",
            "#####v#.#.###v#.#.###.#",
            "#.....#...#...#.#.#...#",
            "#.#########.###.#.#.###",
            "#...###...#...#...#.###",
            "###.###.#.###v#####v###",
            "#...#...#.#.>.>.#.>.###",
            "#.###.###.#.###.#.#v###",
            "#.....###...###...#...#",
            "#####################.#"
    };

    public static final List<String> SAMPLE_INPUT = new ArrayList<>(Arrays.asList(SAMPLE_INPUT_ARRAY));

    public static class Tile {
        int x, y, level;
        String coordsAsString;
        String path;
        Set<String> visited;

        public Tile(int x, int y) {
            this (x, y, 0);
        }

        public Tile(int x, int y, int level) {
            this(x, y, level, "", null);
        }

        public Tile(int x, int y, int level, String path, Set<String> visited) {
            this.x = x;
            this.y = y;
            this.coordsAsString =  String.format("(%d,%d)", x, y);
            this.level = level;
            this.path = path;
            this.visited = new HashSet<>();

            if (visited != null) {
                this.visited.addAll(visited);
            }
        }

        public boolean isVisited(Tile t) {
            return visited.contains(t.coordsAsString);
        }

        public void markVisited(Tile t) {
            visited.add(t.coordsAsString);
        }

        public void inheritVisited(Tile t) {
            visited = new HashSet<>(t.visited);
        }

        public Tile goLeft() { return new Tile(x-1, y, level + 1, path + LEFT, visited); }
        public Tile goRight() { return new Tile(x+1, y, level + 1, path + RIGHT, visited); }
        public Tile goUp() { return new Tile(x, y-1, level + 1, path + UP, visited); }
        public Tile goDown() { return new Tile(x, y+1, level + 1, path + DOWN, visited); }

        public Tile go(char dir) {
            if (dir == UP) {
                return goUp();
            } else if (dir == DOWN) {
                return goDown();
            } else if (dir == LEFT) {
                return goLeft();
            } else if (dir == RIGHT) {
                return goRight();
            }

            return null;
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
            if (! (obj instanceof Tile)) {
                return false;
            }
            Tile rhs = (Tile) obj;
            return (x == rhs.x)&& (y == rhs.y); 
        }
        
        @Override
        public String toString() {
            return String.format("(%d, %d, %d, %d visited, %s)", x, y, level, visited.size(), path);
        }
    }
    
    public static class Map {
        public List<String> tiles;
        int xMax, yMax;

        public Map(List<String> input) {
            tiles = input;
            xMax = tiles.get(0).length() - 1;
            yMax = tiles.size() - 1;
        }

        public char getTileContents(Tile t) {
            String row = tiles.get(t.y);
            return row.charAt(t.x);
        }

        public boolean inBounds(Tile t) {
            return (t.x >= 0) && (t.x <= xMax) && (t.y >= 0) && (t.y <= yMax);
        }

        public boolean isOpen(Tile t) {
            return inBounds(t) && (getTileContents(t) == PATH);
        }

        public Tile slide(Tile t, char dir) {
            // Move in the requested direction
            t = t.go(dir);

            if (!inBounds(t)) {
                return null;
            }

            // Look at where we are
            char content = getTileContents(t);

            // PATH is good
            if (content == PATH) {
                return t;
            }

            // FOREST is not good
            if (content == FOREST) {
                return null;
            }

            // SLOPE_UP, so slide UP if we were headed that way
            if (content == SLOPE_UP) {
                return (dir == UP) ? slide(t, UP) : null;
            }

            // SLOPE_DOWN, so slide DOWN if we were headed that way
            if (content == SLOPE_DOWN) {
                return (dir == DOWN) ? slide(t, DOWN) : null;
            }

            // SLOPE_LEFT, so slide LEFT if we were headed that way
            if (content == SLOPE_LEFT) {
                return (dir == LEFT) ? slide(t, LEFT) : null;
            }

            // SLOPE_RIGHT, so slide RIGHT if we were headed that way
            if (content == SLOPE_RIGHT) {
                return (dir == RIGHT) ? slide(t, RIGHT) : null;
            }

            return null;
        }

        public Tile getFirstPathTile(int y) {
            String row = tiles.get(y);
            int x = row.indexOf(PATH);

            return (x < 0) ? null : new Tile(x, y);
        }

        public Tile getStartingTile() {
            return getFirstPathTile(0);
        }

        public Tile getEndingTile() {
            return getFirstPathTile(yMax);
        }

        public String getShortestPath(Tile from, Tile to) {
            return getAllPaths(from, to, true).stream().findFirst().orElse(null);
        }

        public String getLongestPath(Tile from, Tile to) {
            Set<String> allPaths = getAllPaths(from, to, false);
            return allPaths.stream().sorted((s1, s2) -> Integer.compare(s2.length(), s1.length())).findFirst().orElse(null);
        }

        public Set<String> getAllPaths(Tile from, Tile to, boolean stopOnShortest) {
            Queue<Tile> queue = new LinkedList<>();
            // Set<Tile> visited = new HashSet<>();
            Set<String> finalPaths = new LinkedHashSet<>();

            BiConsumer<Tile, Character> pusher = (parent, dir)-> {
                Tile child = (dir == null) ? parent : slide(parent, dir);
                if ((parent == null) || (child == null)) {
                    System.out.printf("  Discarding out of bounds child while moving %c from %s\n", dir, parent);
                    return;
                }

                boolean open = isOpen(child);
                boolean alreadyVisited = parent.isVisited(child);
                if (open && !alreadyVisited) {
                    System.out.printf("  Queuing and marking visited %s\n", child);
                    parent.markVisited(child);
                    child.inheritVisited(parent);
                    queue.add(child);
                } else {
                    System.out.printf("  Discarding open=%b, visited=%b %s\n", open, alreadyVisited, child);
                }
            };

            // Add the root
             pusher.accept(from, null);
            //queue.add(from);

            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Tile next = queue.remove();
                System.out.printf("Processing %s\n", next);

                // Are we at the end?
                if (next.equals(to)) {
                    finalPaths.add(next.path);
                    System.out.printf("  Recording long path of length %d\n", next.path.length());

                    if (stopOnShortest) {
                        return finalPaths;
                    }
                }

                pusher.accept(next, UP);
                pusher.accept(next, DOWN);
                pusher.accept(next, LEFT);
                pusher.accept(next, RIGHT);
            }

            // Return what was discovered
            return finalPaths;
        }
    }

    @Test
    public void testGetStartingTile() {
        Map map = new Map(SAMPLE_INPUT);
        Tile expected = new Tile(1, 0);
        Tile actual = map.getStartingTile();

        assertEquals("getStartingTile() incorrect", expected, actual);
    }

    @Test
    public void testGetEndingTile() {
        Map map = new Map(SAMPLE_INPUT);
        Tile expected = new Tile(map.xMax-1, map.yMax);
        Tile actual = map.getEndingTile();

        assertEquals("getEndingTile() incorrect", expected, actual);
    }

    @Test
    public void testSlideDown() {
        Map map = new Map(SAMPLE_INPUT);
        Tile start = new Tile(3, 3);
        Tile expected = new Tile(3, 5);
        Tile actual = map.slide(start, DOWN);

        assertEquals("slide(DOWN) over slope incorrect", expected, actual);
    }

    @Test
    public void testSlideDown2() {
        Map map = new Map(SAMPLE_INPUT);
        Tile start = new Tile(13, 11);
        Tile expected = new Tile(13, 13);
        Tile actual = map.slide(start, DOWN);

        assertEquals("slide(DOWN) over slope incorrect", expected, actual);
    }

    @Test
    public void testSlideRight() {
        Map map = new Map(SAMPLE_INPUT);
        Tile start = new Tile(9, 3);
        Tile expected = new Tile(11, 3);
        Tile actual = map.slide(start, RIGHT);

        assertEquals("slide(RIGHT) over slope incorrect", expected, actual);
    }

    @Test
    public void testSample() {
        Map map = new Map(SAMPLE_INPUT);
        int expected = 94;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile()).length();

        assertEquals("getLongestPath() incorrect", expected, actual);
    }

    @Test
    public void testPart1() {
        List<String> inputs = AdventUtils.loadResourceStrings("/2023/day23-p1.txt");
        Map map = new Map(inputs);
        int expected = 94;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile()).length();

        assertEquals("Part1 getLongestPath() incorrect", expected, actual);
    }

    /**
    @Test
    public void testGetShortestDistance() {
        Map map = new Map(10);
        assertEquals("getShortestDistance() incorrect", 11, map.getShortestDistance(new Tile(1,1), new Tile(7,4)));
    }

    @Test
    public void testStarOne() {
        Map map = new Map(1362);
        assertEquals("testStarOne() incorrect", 82, map.getShortestDistance(new Tile(1,1), new Tile(31,39)));
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
    */
}


