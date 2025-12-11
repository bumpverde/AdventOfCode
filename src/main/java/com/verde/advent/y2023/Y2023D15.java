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

public class Y2023D15 {
    public static final String BASIC_TEST_INPUTS1 = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7";

    public static class Lens {
        public String label;
        public int focalLength;

        public Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }
    }

    // The "front" is slot 0, the "back" is slot 8.
    // So, moving a lense forward means towards slot 0 and back means towards slot 8.
    public static class Box {
        public Lens[] lenses;
        public int boxNum;

        public Box(int boxNum) {
            this.boxNum = boxNum;
            lenses = new Lens[9];
            for (int i=0; i< lenses.length; ++i) {
                lenses[i] = null;
            }
        }

        public int indexOf(String label) {
            for (int i=0; i< lenses.length; ++i) {
                if ((lenses[i] != null) && lenses[i].label.equals(label)) {
                    return i;
                }
            }

            return -1;
        }

        public void addLens(String label, int focalLength) {
            int index = indexOf(label);
            Lens newLens = new Lens(label, focalLength);

            // If there is already a lens in the box with the same label, replace the old lens with the new lens:
            // remove the old lens and put the new lens in its place, not moving any other lenses in the box.
            if (index != -1) {
                lenses[index] = newLens;
            } else {
                // If there is not already a lens in the box with the same label, add the lens to the box immediately
                // behind any lenses already in the box. Don't move any of the other lenses when you do this.
                // If there aren't any lenses in the box, the new lens goes all the way to the front of the box.
                // So, take first available spot from the front...
                for (int i = 0; i < lenses.length; ++i) {
                    if (lenses[i] == null) {
                        lenses[i] = newLens;
                        break;
                    }
                }
            }
        }

        public void removeLens(String label) {
            int index = indexOf(label);
            if (index != -1) {
                lenses[index] = null;

                // Move all the lenses behind the one being removed forward one
                for (int i=index; i<lenses.length-1; ++i) {
                    lenses[i] = lenses[i+1];
                }
            }
        }

        public long getFocusingPower() {
            long lensPowers = 0L;
            for (int i=0; i<lenses.length; ++i) {
                Lens lens = lenses[i];
                if (lens != null) {
                    lensPowers += (i+1) * lens.focalLength;
                }
            }

            return (1 + boxNum) * lensPowers;
        }

        public boolean hasLenses() {
            for (int i=0; i<lenses.length; ++i) {
                Lens lens = lenses[i];
                if (lens != null) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Box %d: ", boxNum));
            for (int i=0; i<lenses.length; ++i) {
                Lens lens = lenses[i];
                if (lens != null) {
                    sb.append(String.format(" [%s %d]", lens.label, lens.focalLength));
                }
            }

            return sb.toString();
        }
    }

    public static class BoxRepo {
        public ArrayList<Box> boxes = new ArrayList<>();

        public BoxRepo(Collection<String> steps, boolean verbose) {
            for (int i = 0; i < 256; ++i) {
                boxes.add(new Box(i));
            }

            steps.forEach(s -> {
                processStep(s);

                if (verbose) {
                    System.out.printf("After \"%s\":\n", s);
                    System.out.println(toString());
                }
            });
        }

        public Box getBox(String label) {
            return boxes.get(computeHash(label));
        }

        public void processStep(String step) {
            if (step.contains("-")) {
                String label = StringUtils.split(step, "-")[0];
                getBox(label).removeLens(label);
            } else {
                String[] parts = StringUtils.split(step, "=");
                String label = parts[0];
                int focalLength = Integer.valueOf(parts[1]);
                getBox(label).addLens(label, focalLength);
            }
        }

        public long getFocusingPower() {
            return boxes.stream().mapToLong(b -> b.getFocusingPower()).sum();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boxes.stream().filter(b -> b.hasLenses()).forEach(b -> sb.append(b.toString()).append("\n"));
            return sb.toString();
        }
    }

    public static Collection<String> parseSteps(Collection<String> inputs) {
        Collection<String> steps = new ArrayList<>();
        for (String line : inputs) {
            String[] parts = StringUtils.split(line, ",");
            for (String step : parts) {
                steps.add(step);
            }
        }

        return steps;
    }

    public static int computeHash(String s) {
        int hash = 0;

        for (int i=0; i<s.length(); ++i) {
            int c = (int) s.charAt(i);
            hash += c;
            hash *= 17;
            hash %= 256;
        }

        return hash;
    }

    public static int computeHashSum(Collection<String> steps) {
        return steps.stream().mapToInt(s -> computeHash(s)).sum();
    }

    @Test
    public void testBasicHashPart1() {
        String input = "HASH";
        int expected = 52;
        int actual = computeHash(input);
        assertEquals("Hash incorrect for basic input1", expected, actual);
    }

    @Test
    public void testBasicInputs1Part1() {
        String input = BASIC_TEST_INPUTS1;
        Collection<String>inputs = new ArrayList<>(); inputs.add(input);
        Collection<String> steps = parseSteps(inputs);

        int expected = 1320;
        int actual = computeHashSum(steps);
        assertEquals("Hash incorrect for basic input1", expected, actual);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day15-p1.txt");
        Collection<String> steps = parseSteps(inputs);

        int expected = 513172;
        int actual = computeHashSum(steps);
        System.out.printf("Part 1: %d\n", actual);
        //assertEquals("Num steps incorrect for Part1", expected, actual);
    }

    @Test
    public void testBasicInputs1Part2() {
        String input = BASIC_TEST_INPUTS1;
        Collection<String> inputs = new ArrayList<>(); inputs.add(input);
        Collection<String> steps = parseSteps(inputs);
        BoxRepo boxes = new BoxRepo(steps, true);

        long expected = 145;
        long actual = boxes.getFocusingPower();
        assertEquals("Lens focusing power incorrect for basic input1", expected, actual);
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day15-p1.txt");
        Collection<String> steps = parseSteps(inputs);
        BoxRepo boxes = new BoxRepo(steps, false);

        long expected = 237806;
        long actual = boxes.getFocusingPower();
        System.out.printf("Part 2: %,d (%d)\n", actual, actual);
        assertEquals("Lens focusing power incorrect for Part2", expected, actual);
    }
}
