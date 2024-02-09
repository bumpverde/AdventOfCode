package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import com.verde.advent.Printer;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * @author bumpperde
 */
public class Y2023D23 {
    // NOTE: Y2016D17.java has a longest path solution

    /** Use this instead of System.out */
    final static Printer out = Printer.fromStdOut();
    final static Printer debug = Printer.fromDevNull();

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
    public static final char[] DIRECTIONS = { UP, DOWN, LEFT, RIGHT };

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

    public static class Stats{
        public int max = 0;
        public long nIters = 0L;

        public boolean addLength(int v) {
            boolean hasNewMax = (v > max);
            max = Math.max(max, v);
            return hasNewMax;
        }

        public void inc() {
            ++nIters;
        }
    }

    public static class Edge {
        Tile from, to;
        char direction;

        public Edge(Tile from, Tile to, char direction) {
            this.from = from;
            this.to = to;
            this.direction = direction;
        }
    }

    public static class Graph {
        public Set<Tile> nodes = new LinkedHashSet<>();
        public HashMap<Tile,Collection<Edge>> neighborsByNode = new HashMap<>();
    }

    public static class Tile {
        int x, y, level;
        String path;
        BitSet visited;

        public Tile(int x, int y) {
            this (x, y, 0);
        }

        public Tile(int x, int y, int level) {
            this(x, y, level, "", null);
        }

        public Tile(int x, int y, int level, String path, BitSet visited) {
            this.x = x;
            this.y = y;
            this.level = level;
            this.path = path;
            this.visited = (visited == null) ? null : (BitSet) visited.clone();
        }

        public int distance(Tile t) {
            return Math.abs(x - t.x) + Math.abs(y - t.y);
        }

        public boolean isVisited(Tile t, Map map) {
            if (visited != null) {
                int position = t.y * map.numCols + t.x;
                return visited.get(position);
            }
            return false;
        }

        public void markVisited(Tile t, Map map) {
            if (visited != null) {
                int position = t.y * map.numCols + t.x;
                visited.set(position, true);
            }
        }

        public void inheritVisited(Tile t) {
            visited = (BitSet) t.visited.clone();
        }

        public Tile goLeft() { return new Tile(x-1, y, level + 1, path + LEFT, null); }
        public Tile goRight() { return new Tile(x+1, y, level + 1, path + RIGHT, null); }
        public Tile goUp() { return new Tile(x, y-1, level + 1, path + UP, null); }
        public Tile goDown() { return new Tile(x, y+1, level + 1, path + DOWN, null); }

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
            int numVisited = (visited == null) ? -1 : visited.cardinality();
            return String.format("(%d, %d, %d, %d visited, %s (%d))", x, y, level, numVisited, path, path.length());
        }
    }
    
    public static class Map {
        public List<String> tiles;
        public int numCols, numRows;
        public int xMax, yMax;
        public boolean slopesAreSlippery = true;
        public Graph graph;

        public Map(List<String> input) {
            this(input, true);
        }

        public Map(List<String> input, boolean slopesAreSlippery) {
            tiles = input;
            this.slopesAreSlippery = slopesAreSlippery;

            numCols = tiles.get(0).length();
            xMax = numCols - 1;

            numRows = tiles.size();
            yMax = numRows - 1;

            buildGraph();
        }

        protected void buildGraph() {
            graph = new Graph();
            for (int x=0; x<=xMax; ++x) {
                for (int y=0; y<=yMax; ++y) {
                    Tile tile = new Tile(x, y);

                    if (isOpen(tile)) {
                        graph.nodes.add(tile);

                        Collection<Edge> neighbors = new ArrayList<>(4);
                        for (char dir : DIRECTIONS) {
                            Tile neighbor = slide(tile, dir);
                            if (neighbor != null) {
                                neighbors.add(new Edge(tile, neighbor, dir));
                            }
                        }
                        graph.neighborsByNode.put(tile, neighbors);
                    }
                }
            }
        }

        public char getTileContents(Tile t) {
            String row = tiles.get(t.y);
            char c = row.charAt(t.x);

            // If the slopes are not slippery convert all slopes to a regular PATH, but honor FOREST blocks
            if (!slopesAreSlippery && (c != FOREST)) {
                c = PATH;
            }

            return c;
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

        public int getBitPosition(Tile t) {
            int position = t.y * numCols + t.x;
            return position;
        }

        public int getLongestPath(Tile from, Tile to, boolean breadthFirst) {
            int longestPath = breadthFirst ? getAllPathsBreadthFirst(from, to, false) : dfs2(from, to); //getAllPathsDepthFirst(from, to);
            return longestPath;
        }

        public int getAllPathsBreadthFirst(Tile from, Tile to, boolean stopOnShortest) {
            Queue<Tile> queue = new LinkedList<>();
            Stats stats = new Stats();

            BiConsumer<Tile, Character> pusher = (parent, dir)-> {
                Tile child = (dir == null) ? parent : slide(parent, dir);
                if ((parent == null) || (child == null)) {
                    debug.printf(() -> String.format("  Discarding out of bounds child while moving %c from %s\n", dir, parent));
                    return;
                }

                boolean open = isOpen(child);
                boolean alreadyVisited = parent.isVisited(child, this);
                if (open && !alreadyVisited) {
                    parent.markVisited(child, this);
                    child.inheritVisited(parent);
                    queue.add(child);
                    debug.printf(() -> String.format("  Queuing and marking visited %s\n", child));
                } else {
                    debug.printf(() -> String.format("  Discarding open=%b, visited=%b %s\n", open, alreadyVisited, child));
                }
            };

            // Add the root
            from.visited = new BitSet(numCols * numRows);
            pusher.accept(from, null);

            // Do breadth-first traversal until we reach the destination
            while (queue.peek() != null) {
                Tile next = queue.remove();
                debug.printf(() -> String.format("Processing %s\n", next));

                // Are we at the end?
                if (next.equals(to)) {
                    stats.addLength(next.path.length());
                    out.printf("  Recording long path of length %d\n", next.path.length());

                    if (stopOnShortest) {
                        return stats.max;
                    }
                }

                pusher.accept(next, UP);
                pusher.accept(next, DOWN);
                pusher.accept(next, LEFT);
                pusher.accept(next, RIGHT);
            }

            // Return what was discovered
            return stats.max;
        }

        public int getAllPathsDepthFirst(Tile from, Tile to) {
            Stack<Tile> stack = new Stack<>();
            Stats stats = new Stats();

            BiConsumer<Tile, Character> pusher = (parent, dir)-> {
                Tile child = (dir == null) ? parent : slide(parent, dir);
                if ((parent == null) || (child == null)) {
                    debug.printf(() -> String.format("  Discarding blocked/out-of-bounds child while moving %c from %s\n", dir, parent));
                    return;
                }

                if (parent.isVisited(child, this)) {
                    debug.printf(() -> String.format("  Discarding already visited child %s\n", child));
                } else {
                    child.inheritVisited(parent);
                    stack.push(child);
                    debug.printf(() -> String.format("  Queuing %s\n", child));
                }
            };

            // Set up the root's visited BitSet and push it onto the stack
            from.visited = new BitSet(numCols * numRows);
            pusher.accept(from, null);

            // Process the stack, depth first
            while (!stack.isEmpty()) {
                Tile tile = stack.pop();
                stats.inc();
                debug.printf(() -> String.format("Processing %s, %d queued, %,d iters\n", tile, stack.size(), stats.nIters));

                boolean open = isOpen(tile);
                boolean alreadyVisited = tile.isVisited(tile, this);
                if (open && !alreadyVisited) {
                    tile.markVisited(tile, this);

                    if (tile.equals(to)) {
                        if (stats.addLength(tile.path.length())) {
                            out.printf("  New MAX: %d (%,d steps)\n", stats.max, stats.nIters);
                        }
                        debug.printf(() -> String.format("  Recording long path of length %d (max: %d, nIters=%,d)\n", tile.path.length(), stats.max, stats.nIters));
                    } else {
                        pusher.accept(tile, UP);
                        pusher.accept(tile, DOWN);
                        pusher.accept(tile, LEFT);
                        pusher.accept(tile, RIGHT);
                    }
                } else {
                    debug.printf(() -> String.format("  Discarding open=%b, visited=%b %s\n", open, alreadyVisited, tile));
                }
            }

            // Return what was discovered
            return stats.max;
        }

        public void dfs(Tile tile, Tile destination, Set<String> finalPaths) {
            tile.markVisited(tile, this);

            // Are we at the end?
            if (tile.equals(destination)) {
                finalPaths.add(tile.path);
                out.printf("  Recording long path of length %d\n", tile.path.length());
            } else {
                // Explore the adjacent rooms
                dfsExploreAdjacent(tile, UP, destination, finalPaths);
                dfsExploreAdjacent(tile, DOWN, destination, finalPaths);
                dfsExploreAdjacent(tile, LEFT, destination, finalPaths);
                dfsExploreAdjacent(tile, RIGHT, destination, finalPaths);
            }
        }

        public void dfsExploreAdjacent(Tile parent, char dir, Tile destination, Set<String> finalPaths) {
            Tile child = slide(parent, dir);
            if ((parent == null) || (child == null)) {
                debug.printf(() -> String.format("  Discarding out of bounds child while moving %c from %s\n", dir, parent));
                return;
            }

            boolean open = isOpen(child);
            boolean alreadyVisited = parent.isVisited(child, this);
            if (open && !alreadyVisited) {
                child.inheritVisited(parent);
                dfs(child, destination, finalPaths);
            } else {
                debug.printf(() -> String.format("  Discarding open=%b, visited=%b %s\n", open, alreadyVisited, child));
            }
        }

        public int dfs2(Tile start, Tile end) {
            Stats stats = new Stats();
            BitSet visited = new BitSet(numCols * numRows);

            visited.set(getBitPosition(start));
            dfs2Worker(start, end, 0, 0, visited, stats);
            return stats.max;
        }

        public void dfs2Worker(Tile current, Tile end, int distance, int depth, BitSet visited, Stats stats) {
            out.printf("%5d Processing : %d (%,d steps)\n", depth, stats.max, stats.nIters);
            if (current.equals(end)) {
                if (stats.addLength(distance)) {
                    out.printf("  New MAX: %d (%,d steps)\n", stats.max, stats.nIters);
                }
                debug.printf(() -> String.format("  Recording final path of length %d (max: %d, nIters=%,d)\n", distance, stats.max, stats.nIters));
                return;
            }

            for (Edge edge : graph.neighborsByNode.get(current)) {
                Tile neighbor = edge.to;
                int bitPos = getBitPosition(neighbor);
                if (! visited.get(bitPos)) {
                    stats.inc();
                    visited.set(bitPos);
                    dfs2Worker(neighbor, end, distance + current.distance(neighbor), depth + 1, visited, stats);
                    visited.clear(bitPos);
                }
            }
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
    public void testExamplePart1() {
        Map map = new Map(SAMPLE_INPUT);
        int expected = 94;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile(), false);

        assertEquals("Example Part1 getLongestPath() incorrect", expected, actual);
    }

    @Test
    public void testPart1() {
        List<String> inputs = AdventUtils.loadResourceStrings("/2023/day23-p1.txt");
        Map map = new Map(inputs);
        int expected = 2214;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile(), false);

        assertEquals("Part1 getLongestPath() incorrect", expected, actual);
    }

    @Test
    public void testExamplePart2() {
        Map map = new Map(SAMPLE_INPUT, false);
        int expected = 154;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile(), false);

        assertEquals("Example Part2 getLongestPath() incorrect", expected, actual);
    }

    @Test
    public void testPart2() {
        List<String> inputs = AdventUtils.loadResourceStrings("/2023/day23-p1.txt");
        Map map = new Map(inputs, false);
        int expected = 2214;
        int actual = map.getLongestPath(map.getStartingTile(), map.getEndingTile(), false);

        assertEquals("Part2 getLongestPath() incorrect", expected, actual);
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


