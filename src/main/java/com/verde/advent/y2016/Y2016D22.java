package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;
import com.verde.advent.Point2D;

public class Y2016D22 {
    public static class Node {
        String name;
        Point2D pos;
        int size;
        int used;
        int avail;
        int use;
        
        public static int toInt(String s, String strip) {
            return Integer.parseInt(StringUtils.strip(s, strip)); 
        }
        
        public Node(String spec) {
            String[] parts = StringUtils.split(spec, " ");
            name = parts[0];
            
            String[] nameParts = StringUtils.split(name, "-");
            pos = new Point2D(toInt(nameParts[1], "x"), toInt(nameParts[2], "y")); 
            size = toInt(parts[1], "T");
            used = toInt(parts[2], "T");
            avail = toInt(parts[3], "T");
            use = toInt(parts[4], "%");
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
        
        @Override
        public String toString() {
            return String.format("%3d/%-3d", used, size);
        }
        
        public boolean isViable(Node rhs) {
            return !equals(rhs)
                    && (used > 0)
                    && (used <= rhs.avail);
        }
        
        public int distance(Node n) {
            return pos.getManhattanDistanceTo(n.pos);
        }
    };
    
    public static Set<Node> toSet(String[] inputs) {
        return
                Arrays
                .stream(inputs)
                .map(s -> new Node(s))
                .collect(Collectors.toSet());
    }
    
    public static Node[][] toGrid(String[] inputs) {
        Set<Node> nodes = toSet(inputs);
        int maxX = nodes.stream().mapToInt(n -> n.pos.getX()).max().orElse(0);
        int maxY = nodes.stream().mapToInt(n -> n.pos.getY()).max().orElse(0);

        Node[][] grid = new Node[maxX+1][maxY+1];
        nodes.forEach(n -> grid[n.pos.getX()][n.pos.getY()] = n);
        
        return grid;
    }

    public static void render(String[] inputs) {
        Node[][] grid = toGrid(inputs);
        Node goal = grid[0][grid[0].length-1];

        System.out.printf("   ");
        for (int x=0; x<grid.length; ++x) {
            System.out.printf("%-11d", x);
        }
        System.out.println();
        
        for (int y=0; y<grid[0].length; ++y) {
            for (int x=0; x<grid.length; ++x) {
                Node curr = grid[x][y];
                boolean viableWithGoal = goal.isViable(curr);
                
                if (x == 0) {
                    System.out.printf("%-2d ", y);
                }
                
                System.out.printf("%s %s   ", curr, viableWithGoal ? "!" : "");
            }
            System.out.println();
        }
    }
    
    public static Set<String> getViablePairs(String[] inputs) {
        Set<Node> nodes = toSet(inputs); 
        Set<String> viablePairs = new HashSet<String>();
        
        // Do an outer and inner traversal
        nodes.forEach(outer -> {
            nodes.forEach(inner -> {
                if (outer.isViable(inner)) {
                    viablePairs.add(outer.name + "," + inner.name);
                }
                if (inner.isViable(outer)) {
                    viablePairs.add(inner.name + "," + outer.name);
                }
            });
        });
                    
        
        return viablePairs;
    }
    
    @Test
    public void testStarOne() {
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        int expected = 860;
    
        assertEquals("testStarOne() incorrect", expected, getViablePairs(inputs).size());
    }

    @Test
    public void testStarTwo() {
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        render(inputs);
        
        // The only node on the grid that is viable with the goal is (27, 15) which is empty.
        // But, we can't just move it up, because it is blocked above by some very large, 
        // very full nodes that go all the way to the right edge. Going left, the full nodes 
        // stop at column 21. So, if we shift our empty viable node left over to column 20, 
        // we can then get it to the neighbor of the goal node. From there, each shuffle left 
        // of the goal node takes 5 moves (1 to swap and then 4 to reposition the empty node
        // to the left of the goal node for the next shuffle), which is exactly what is shown 
        // in the instruction example. So, we shuffle over from col 34 to col 1 (165 moves),
        // and then one final move to put the goal in the origin.
        Node[][] grid = toGrid(inputs);
        Node viableNode = grid[27][15];
        Node nodeToClearPath = grid[20][15];
        Node nodeAboveNodeToClearPath = grid[20][14];
        Node goalNeighbor = grid[33][0];

        // tried: 204, 201, 
        int distance = 
                viableNode.distance(nodeToClearPath) 
                + nodeAboveNodeToClearPath.distance(goalNeighbor)
                + 165 + 1;
        assertEquals("testStarTwo() incorrect", 200, distance);
    }

    public static final List<String> STAR_INPUTS = 
            AdventUtils.loadResourceStrings("/2016/day22-inputs.txt").stream().filter(s -> s.contains("/dev/")).collect(Collectors.toList());
}
