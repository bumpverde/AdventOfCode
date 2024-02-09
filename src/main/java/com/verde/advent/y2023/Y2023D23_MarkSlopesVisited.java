package com.verde.advent.y2023;

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
public class Y2023D23_MarkSlopesVisited {
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
        String path;

        public Tile(int x, int y) {
            this (x, y, 0);
        }

        public Tile(int x, int y, int level) {
            this(x, y, level, "");
        }
        public Tile(int x, int y, int level, String path) {
            this.x = x;
            this.y = y;
            this.level = level;
            this.path = path;
        }


        public Tile goLeft() { return new Tile(x-1, y, level + 1, path + LEFT); }
        public Tile goRight() { return new Tile(x+1, y, level + 1, path + RIGHT); }
        public Tile goUp() { return new Tile(x, y-1, level + 1, path + UP); }
        public Tile goDown() { return new Tile(x, y+1, level + 1, path + DOWN); }

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
            return String.format("(%d, %d, %d, %s)", x, y, level, path);
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

        /**
        public boolean isOpen(Tile t, Collection<Tile> tilesVisited) {
            if ((t.x < 0) || (t.x > xMax) || (t.y < 0) || (t.y > yMax)) {
                return false;
            }

            // Remember all the tiles visited in the open calculation
            tilesVisited.add(t);

            char content = getTileContents(t);
            if (content == PATH) {
                return true;
            }

            if (content == FOREST) {
                return false;
            }

            if (content == SLOPE_DOWN) {
                return isOpen(t.goDown(), tilesVisited);
            }

            if (content == SLOPE_UP) {
                return isOpen(t.goUp(), tilesVisited);
            }

            if (content == SLOPE_LEFT) {
                return isOpen(t.goLeft(), tilesVisited);
            }

            if (content == SLOPE_RIGHT) {
                return isOpen(t.goRight(), tilesVisited);
            }

            return false;
        }
         */

        public boolean inBounds(Tile t) {
            return (t.x >= 0) && (t.x <= xMax) && (t.y >= 0) && (t.y <= yMax);
        }

        public Tile slide(Tile t, char dir, Collection<Tile> tilesVisited) {
            // Move in the requested direction
            t = t.go(dir);

            if (!inBounds(t)) {
                return null;
            }

            // Remember all the tiles visited in the calculation to get to an open position
            tilesVisited.add(t);

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
                return (dir == UP) ? slide(t, UP, tilesVisited) : null;
            }

            // SLOPE_DOWN, so slide DOWN if we were headed that way
            if (content == SLOPE_DOWN) {
                return (dir == DOWN) ? slide(t, DOWN, tilesVisited) : null;
            }

            // SLOPE_LEFT, so slide LEFT if we were headed that way
            if (content == SLOPE_LEFT) {
                return (dir == LEFT) ? slide(t, LEFT, tilesVisited) : null;
            }

            // SLOPE_RIGHT, so slide RIGHT if we were headed that way
            if (content == SLOPE_RIGHT) {
                return (dir == RIGHT) ? slide(t, RIGHT, tilesVisited) : null;
            }

            return null;
        }

        public Tile getFirstPathTile(int y) {
            String row = tiles.get(y);
            int x = row.indexOf(PATH);

            return (x < 0) ? null : new Tile(x, y, 0);
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
            Set<Tile> visited = new HashSet<>();
            Set<String> finalPaths = new LinkedHashSet<>();

            BiConsumer<Tile,Character> pusher = (tile, dir) -> {
                System.out.printf("\tEvaluating %s moving %c...", tile, dir);

                // We need to make sure that all the tiles we potentially slid across
                // to get to the current tile have not been visited already.
                Collection<Tile> tilePlusSlides = new ArrayList<>();
                if (dir != null) {
                    tile = slide(tile, dir, tilePlusSlides);
                } else {
                    tilePlusSlides.add(tile);
                }

                boolean someAlreadyVisited = tilePlusSlides.stream().anyMatch(t -> visited.contains(t));
                if ((tile != null) && !someAlreadyVisited) {
                    // If we're recording all the paths, don't actually visit the final tile,
                    // but do record a final path if we see it.
                    if (!stopOnShortest && tile.equals(to)) {
                        finalPaths.add(tile.path);
                        System.out.printf("  Recording long path of length %d\n", tile.path.length());
                    } else {
                        queue.add(tile);
                        visited.addAll(tilePlusSlides);
                        System.out.printf("  Queueing %d\n", tilePlusSlides.size());
                    }
                } else {
                    System.out.printf("  Discarding: someVisited=%b\n", someAlreadyVisited);
                }
            };

            // Add the root, but don't try to move it yet
            pusher.accept(from, null);

            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Tile next = queue.remove();
                System.out.printf("Processing %s\n", next);

                // Visit the node to see if we're there yet.
                if (next.equals(to)) {
                    finalPaths.add(next.path);

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

        /**
        public int getShortestDistance(Tile from, Tile to) {
            Queue<Tile> queue = new LinkedList<Tile>();
            Set<Tile> visited = new HashSet<Tile>();
            
            Consumer<Tile> pusher = (tile) -> {
                if ((tile != null) && isOpen(tile, null) && !visited.contains(tile)) {
                    queue.add(tile);
                    visited.add(tile);
                }
            };
            
            // Add the root
            pusher.accept(from);
            
            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Tile next = queue.remove();
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
         */
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
        Collection<Tile> tilePlusSlides = new ArrayList<>();
        Tile start = new Tile(3, 3);
        Tile expected = new Tile(3, 5);
        Tile actual = map.slide(start, DOWN, tilePlusSlides);

        assertEquals("slide(DOWN) over slope has incorrect number of tiles traversed", 2, tilePlusSlides.size());
        assertEquals("slide(DOWN) over slope incorrect", expected, actual);
    }

    @Test
    public void testSlideDown2() {
        Map map = new Map(SAMPLE_INPUT);
        Collection<Tile> tilePlusSlides = new ArrayList<>();
        Tile start = new Tile(13, 11);
        Tile expected = new Tile(13, 13);
        Tile actual = map.slide(start, DOWN, tilePlusSlides);

        assertEquals("slide(DOWN) over slope has incorrect number of tiles traversed", 2, tilePlusSlides.size());
        assertEquals("slide(DOWN) over slope incorrect", expected, actual);
    }

    @Test
    public void testSlideRight() {
        Map map = new Map(SAMPLE_INPUT);
        Collection<Tile> tilePlusSlides = new ArrayList<>();
        Tile start = new Tile(9, 3);
        Tile expected = new Tile(11, 3);
        Tile actual = map.slide(start, RIGHT, tilePlusSlides);

        assertEquals("slide(RIGHT) over slope has incorrect number of tiles traversed", 2, tilePlusSlides.size());
        assertEquals("slide(RIGHT) over slope incorrect", expected, actual);
    }

    @Test
    public void testSample() {
        Map map = new Map(SAMPLE_INPUT);
        int expected = 94;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile()).length();

        assertEquals("getLongestPath() incorrect", expected, actual);
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


