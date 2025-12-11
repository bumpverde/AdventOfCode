package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Y2023D08 {
    public static final String NODE_REGEX = "(\\w+)\\s*=\\s*\\((\\w+),\\s*(\\w+)\\)";
    public static final Pattern NODE_PATTERN = Pattern.compile(NODE_REGEX);

    public static final String BASIC_TEST_INPUTS1 = """
RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)""";

    public static final String BASIC_TEST_INPUTS2 = """
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)""";

    public static final String BASIC_TEST_INPUTS3 = """
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)""";

    public static Map<String,Node> nodesByName = new HashMap<>();
    public static String directions = "";

    public static class Node {
        public String name;
        public String left, right;

        public Node(String name, String left, String right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        public Node select(char dir) {
            String nodeName = (dir == 'R') ? right : left;
            return nodesByName.get(nodeName);
        }
    }

    public Node parseNode(String input) {
        Node node = null;
        Matcher matcher = NODE_PATTERN.matcher(input);
        if (matcher.matches()) {
            node = new Node(matcher.group(1), matcher.group(2), matcher.group(3));
            nodesByName.put(node.name, node);
        }

        return node;
    }

    public void init(Collection<String> inputs) {
        nodesByName.clear();
        inputs.stream().filter(s -> !s.trim().isEmpty()).forEach(s -> {
            if (s.contains("=")) {
                parseNode(s);
            } else {
                directions = s;
            }
        });

        System.out.printf("#nodes = %d\n", nodesByName.size());
    }

    public int getNumSteps(String from, String to) {
        int numSteps = 0;
        Node currNode = nodesByName.get(from);
        Node dstNode = nodesByName.get(to);

        while (currNode != dstNode) {
            int dirIndex = numSteps % directions.length();
            char dir = directions.charAt(dirIndex);
            currNode = currNode.select(dir);
            ++numSteps;
        }

        return numSteps;
    }

    // Get the number of steps from a starting node to a node with a name ending in "Z"
    // From https://www.reddit.com/r/adventofcode/comments/18df7px/2023_day_8_solutions/ (see rogual's comments)
    public long getZPeriod(Node n) {
        long numSteps = 0L;
        do {
            int dirIndex = (int) (numSteps % directions.length());
            char dir = directions.charAt(dirIndex);
            n = n.select(dir);
            ++numSteps;
        } while (! n.name.endsWith("Z"));

        return numSteps;
    }

    public long getNumStepsForParallelGhosts() {
        List<Node> nodes = nodesByName.values().stream().filter(n -> n.name.endsWith("A")).collect(Collectors.toList());
        Collection<Long> periods = nodes.stream().map(n -> getZPeriod(n)).collect(Collectors.toList());

        System.out.printf("Parallel processing %,d nodes\n", nodes.size());
        nodes.forEach(n -> System.out.printf("Z Period for %s = %,d\n", n.name, getZPeriod(n)));
        long numSteps = calculateLCM(periods);
        return numSteps;
    }

    // Method to calculate the Greatest Common Divisor (GCD): ChatGPT
    public static long calculateGCD(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Method to calculate the Least Common Multiple (LCM): ChatGPT
    public static long calculateLCM(Collection<Long> numbers) {
        long lcm = 1;

        for (long number : numbers) {
            lcm = lcm * number / calculateGCD(lcm, number);
        }

        return lcm;
    }

    @Test
    public void testBasicInputs1Part1() {
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS1, "\n")).collect(Collectors.toList());
        init(inputs);
        int expected = 2;
        int actual = getNumSteps("AAA", "ZZZ");
        assertEquals("Num steps incorrect for basic input1", expected, actual);
    }

    @Test
    public void testBasicInputs2Part1() {
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS2, "\n")).collect(Collectors.toList());
        init(inputs);
        int expected = 6;
        int actual = getNumSteps("AAA", "ZZZ");
        assertEquals("Num steps incorrect for basic input2", expected, actual);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day8-p1.txt");
        init(inputs);
        int expected = 20777;
        int actual = getNumSteps("AAA", "ZZZ");
        //System.out.printf("Part 1: %d\n", actual);
        assertEquals("Num steps incorrect for Part1", expected, actual);
    }

    @Test
    public void testBasicInputs3Part2() {
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS3, "\n")).collect(Collectors.toList());
        init(inputs);
        long expected = 6;
        long actual = getNumStepsForParallelGhosts();
        assertEquals("Num steps for parallel ghosts incorrect for basic input3", expected, actual);
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day8-p1.txt");
        init(inputs);
        long expected = 13289612809129L;
        long actual = getNumStepsForParallelGhosts();
        //System.out.printf("Part 2: %,d (%d)\n", actual, actual);
        assertEquals("Num steps for parallel ghosts incorrect for Part2", expected, actual);
    }

}
