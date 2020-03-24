package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;
import com.verde.advent.Point2D;

/**
 * --- Day 22: Grid Computing ---
 * You gain access to a massive storage cluster arranged in a grid; each storage node is only connected 
 * to the four nodes directly adjacent to it (three if the node is on an edge, two if it's in a corner).
 * 
 * You can directly access data only on node /dev/grid/node-x0-y0, but you can perform some limited actions on the other nodes:
 * - You can get the disk usage of all nodes (via df). The result of doing this is in your puzzle input.
 * - You can instruct a node to move (not copy) all of its data to an adjacent node (if the destination node has enough space to receive the data). The sending node is left empty after this operation.
 * - Nodes are named by their position: the node named node-x10-y10 is adjacent to nodes node-x9-y10, node-x11-y10, node-x10-y9, and node-x10-y11.
 * 
 * Before you begin, you need to understand the arrangement of data on these nodes. Even though you can only move data between directly connected nodes, 
 * you're going to need to rearrange a lot of the data to get access to the data you need. Therefore, you need to work out how you might be able to shift data around.
 * 
 * To do this, you'd like to count the number of viable pairs of nodes. A viable pair is any two nodes (A,B), regardless of whether they are directly connected, such that:
 * - Node A is not empty (its Used is not zero).
 * - Nodes A and B are not the same node.
 * - The data on node A (its Used) would fit on node B (its Avail).
 * 
 * How many viable pairs of nodes are there?
 * 
 * Your puzzle answer was 860.
 * 
 * --- Part Two ---
 * Now that you have a better understanding of the grid, it's time to get to work.
 * 
 * Your goal is to gain access to the data which begins in the node with y=0 and the highest x (that is, the node in the top-right corner).
 * 
 * For example, suppose you have the following grid:
 * 
 * Filesystem            Size  Used  Avail  Use%
 * /dev/grid/node-x0-y0   10T    8T     2T   80%
 * /dev/grid/node-x0-y1   11T    6T     5T   54%
 * /dev/grid/node-x0-y2   32T   28T     4T   87%
 * /dev/grid/node-x1-y0    9T    7T     2T   77%
 * /dev/grid/node-x1-y1    8T    0T     8T    0%
 * /dev/grid/node-x1-y2   11T    7T     4T   63%
 * /dev/grid/node-x2-y0   10T    6T     4T   60%
 * /dev/grid/node-x2-y1    9T    8T     1T   88%
 * /dev/grid/node-x2-y2    9T    6T     3T   66%
 * 
 * In this example, you have a storage grid 3 nodes wide and 3 nodes tall. The node you can access directly, node-x0-y0, is almost full. 
 * The node containing the data you want to access, node-x2-y0 (because it has y=0 and the highest x value), contains 6 terabytes of data -
 *  enough to fit on your node, if only you could make enough space to move it there.
 * 
 * Fortunately, node-x1-y1 looks like it has enough free space to enable you to move some of this data around. In fact, it seems like all 
 * of the nodes have enough space to hold any node's data (except node-x0-y2, which is much larger, very full, and not moving any time soon).
 * So, initially, the grid's capacities and connections look like this:
 * 
 * ( 8T/10T) --  7T/ 9T -- [ 6T/10T]
 *     |           |           |
 *   6T/11T  --  0T/ 8T --   8T/ 9T
 *     |           |           |
 *  28T/32T  --  7T/11T --   6T/ 9T
 *  
 * The node you can access directly is in parentheses; the data you want starts in the node marked by square brackets.
 * 
 * In this example, most of the nodes are interchangable: they're full enough that no other node's data would fit, 
 * but small enough that their data could be moved around. Let's draw these nodes as .. The exceptions are the empty node, 
 * which we'll draw as _, and the very large, very full node, which we'll draw as #. Let's also draw the goal data as G. Then, it looks like this:
 * 
 * (.) .  G
 *  .  _  .
 *  #  .  .
 *  
 * The goal is to move the data in the top right, G, to the node in parentheses. To do this, we can issue some commands to the grid and rearrange the data:
 * - Move data from node-y0-x1 to node-y1-x1, leaving node node-y0-x1 empty:
 * (.) _  G
 *  .  .  .
 *  #  .  .
 * - Move the goal data from node-y0-x2 to node-y0-x1:
 * (.) G  _
 *  .  .  .
 *  #  .  .
 *  - At this point, we're quite close. However, we have no deletion command, so we have to move some more data around. So, next, we move the data from node-y1-x2 to node-y0-x2:
 * (.) G  .
 *  .  .  _
 *  #  .  .
 * - Move the data from node-y1-x1 to node-y1-x2:
 * (.) G  .
 *  .  _  .
 *  #  .  .
 * - Move the data from node-y1-x0 to node-y1-x1:
 * (.) G  .
 *  _  .  .
 *  #  .  .
 * - Next, we can free up space on our node by moving the data from node-y0-x0 to node-y1-x0:
 * (_) G  .
 *  .  .  .
 *  #  .  .
 * - Finally, we can access the goal data by moving the it from node-y0-x1 to node-y0-x0:
 * (G) _  .
 *  .  .  .
 *  #  .  .
 *  
 * So, after 7 steps, we've accessed the data we want. Unfortunately, each of these moves takes time, and we need to be efficient:
 * 
 * What is the fewest number of steps required to move your goal data to node-x0-y0?
 * 
 * Your puzzle answer was 200.
 * 
 * @author bumpverde
 */
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
