package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Y2023D12 {
    public static final String INPUTS= """
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1""";

    public static class ConditionRecord {
        public String input;
        public String springs;
        public int[] auditLog;

        public static ConditionRecord forPart2(String input) {
            String[] parts = StringUtils.split(input, " ");
            StringBuilder springs = new StringBuilder(parts[0]);
            StringBuilder audits = new StringBuilder(parts[1]);
            for (int i=0; i<4; ++i) {
                springs.append('?').append(parts[0]);
                audits.append(",").append(parts[1]);
            }

            return new ConditionRecord(springs.toString() + " " + audits.toString());
        }

        public ConditionRecord(String input) {
            this.input = input;
            String[] parts = StringUtils.split(input, " ");
            springs = parts[0];

            String[] auditParts = StringUtils.split(parts[1], ",");
            auditLog = new int[auditParts.length];
            for (int i = 0; i < auditParts.length; ++i) {
                try {
                    auditLog[i] = Integer.parseInt(auditParts[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isValid(String variant) {
            /**
            int[] computedDamagedSectionSizes = getDamagedGroupSizes(variant);
            return Arrays.equals(computedDamagedSectionSizes, auditLog);
             */
            return matchesDamagedGroups(variant, auditLog, s -> {});
        }

        public boolean isValid() {
            return isValid(springs);
        }

        public long getNumArrangements() {
            AtomicLong totalCount = new AtomicLong(0);
            AtomicLong validCount = new AtomicLong(0);

            //            System.out.printf("\nVariants of %s:\n", springs);
            getAllVariants(springs, s -> {
                boolean valid = isValid(s);
                validCount.addAndGet(valid ? 1 : 0);
                totalCount.incrementAndGet();
//                System.out.printf("\t%s: %b, %d\n", s, valid, count.get());
            });
            System.out.printf("\tNum Arrangements '%s': total=%,d  valid=%,d\n", springs, totalCount.get(), validCount.get());
            return validCount.get();
        }

        public long getNumArrangementsDepthFirst() {
            AtomicLong totalCount = new AtomicLong(0);
            AtomicLong validCount = new AtomicLong(0);

            //            System.out.printf("\nVariants of %s:\n", springs);
            getAllVariantsDepthFirst(springs, v -> false, s -> {
                boolean valid = isValid(s);
                validCount.addAndGet(valid ? 1 : 0);
                totalCount.incrementAndGet();
//                System.out.printf("\t%s: %b, %d\n", s, valid, count.get());
            });
            System.out.printf("\tNum Arrangements '%s': total=%,d  valid=%,d\n", springs, totalCount.get(), validCount.get());
            return validCount.get();
        }
    }

    public static final String REGEX = "(?:^|\\?)(\\.+|\\#+)(?:\\.|\\?|$)";  // Chat GPT
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    public static class Counter implements Consumer<String> {
        public long count = 0, mod=-1;
        public Consumer<String> modCallback = null;

        public Counter() {
            mod = -1;
            modCallback = null;
        }

        public Counter(long mod, Consumer<String> modCallback) {
            this.mod = mod;
            this.modCallback = modCallback;
        }

        @Override
        public void accept(String s) {
            ++count;
            if ((mod > 0) && ((count % mod) == 0)) {
                modCallback.accept(s);
            }
        }
    }

    public static class Recorder implements Consumer<String> {
        public LinkedList<String> accepted = new LinkedList<>();

        @Override
        public void accept(String s) {
            accepted.add(s);
        }
    }

    public static Consumer<String> reporter(String name) {
        return s -> System.out.printf("%s: %s\n", name, s);
    }

    public static String replaceAll(String s, String replaceThis, String withThat) {
        while (s.indexOf(replaceThis) != -1) {
            s = StringUtils.replace(s, replaceThis, withThat);
        }
        return s;
    }

    public static void variantsToPrune(String s, int[] auditLog, Consumer<String> prune) {
    }

    public static boolean matchesDamagedGroups(String s, int[] auditLog, Consumer<String> onPrune) {
        //ArrayList<Integer> sizes = new ArrayList<>(s.length());

        int currDamagedGroupIndex = -1;
        int currDamagedGroupSize = 0;
        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '.') {
                // End the current group (if valid)
                if (currDamagedGroupSize > 0) {
                    ++currDamagedGroupIndex; // add
                    //sizes.add(currDamagedGroupSize);
                    //if (sizes.size() > auditLog.length) {
                    //    return false;
                    //}
                    if ((currDamagedGroupIndex >= auditLog.length) || (auditLog[currDamagedGroupIndex] != currDamagedGroupSize)) {
                        onPrune.accept(s);  // exceeded number of groups or group didn't match, hard prune
                        return false;
                    }
                    currDamagedGroupSize = 0;  // start the next group
                    // Check for an early bail if the pieces don't match
                    //if ((++currDamagedGroupIndex >= auditLog.length) || (auditLog[currDamagedGroupIndex] != currDamagedGroupSize)) {
                    //    return false;
                    //}
                    //currDamagedGroupSize = 0;
                }
            } else if (c == '#') {
                ++currDamagedGroupSize;
            }
        }

        // Add in the last group (if valid)
        if (currDamagedGroupSize > 0) {
            ++currDamagedGroupIndex; // add
            //sizes.add(currDamagedGroupSize);
            //if (sizes.size() > auditLog.length) {
            //    return false;
            //}
            if ((currDamagedGroupIndex >= auditLog.length) || (auditLog[currDamagedGroupIndex] != currDamagedGroupSize)) {
                onPrune.accept(s);  // exceeded number of groups or group didn't match, hard prune
                return false;
            }

            //if ((++currDamagedGroupIndex >= auditLog.length) || (auditLog[currDamagedGroupIndex] != currDamagedGroupSize)) {
            //    return false;
            //}
        }

        if (currDamagedGroupIndex != auditLog.length-1) {
            if (currDamagedGroupIndex > auditLog.length-1) {
                onPrune.accept(s);  // exceeded number of groups, hard prune
            }

            return false;
        }

        /**
        for (int i=0; i<auditLog.length; ++i) {
            if (auditLog[i] != sizes.get(i)) {
                return false;
            }
        }
         */
        return true;
        /**
        int[] intSizes = new int[sizes.size()];
        for (int i=0; i<sizes.size(); ++i) {
            intSizes[i] = sizes.get(i);
        }

        return intSizes;
         **/
        //return currDamagedGroupIndex == auditLog.length-1;
    }

    public static void getNextVariants(String s, Consumer<String> onComplete, Consumer<String> onIncomplete) {
        // Find the next undetermined spot. If there are none left, s is complete.
        int index = s.indexOf('?');
        if (index == -1) {
            onComplete.accept(s);
            return;
        }

        // Else, replace the first '?' it with a good and damaged variant
        String prefix = s.substring(0, index);
        String suffix = s.substring(index + 1);
        String good = new StringBuilder(prefix).append('.').append(suffix).toString();
        String damaged = new StringBuilder(prefix).append('#').append(suffix).toString();

        // Let the appropriate Consumer know the results
        Consumer<String> consumer = good.contains("?") ? onIncomplete : onComplete;
        consumer.accept(good);
        consumer.accept(damaged);
    }

    public static void getAllVariantsDepthFirst(String s, Predicate<String> shouldPrune, Consumer<String> onComplete) {
        // Find the next undetermined spot. If there are none left, s is complete.
        int index = s.indexOf('?');
        if (index == -1) {
            onComplete.accept(s);
            return;
        }

        // This is not yet complete, but see if we should prune the tree of possibilities right here
        if (shouldPrune.test(s)) {
            return;
        }

        // Else, replace the first '?' it with a good and damaged variant
        String prefix = s.substring(0, index);
        String suffix = s.substring(index + 1);
        String good = new StringBuilder(prefix).append('.').append(suffix).toString();
        String damaged = new StringBuilder(prefix).append('#').append(suffix).toString();

        // And recurse...
        getAllVariantsDepthFirst(good, shouldPrune, onComplete);
        getAllVariantsDepthFirst(damaged, shouldPrune, onComplete);
    }


    public static void getAllVariants(String s, Consumer<String> onComplete) {
        Recorder incomplete = new Recorder();

        // Compute all variants
        do {
            getNextVariants(s, onComplete, incomplete);
            s = incomplete.accepted.isEmpty() ? null : incomplete.accepted.pop();
        } while (s != null);
    }

    @Test
    public void testGetNextVariantsUndetermined() {
        String s = "?";
        Recorder complete = new Recorder();
        Recorder incomplete = new Recorder();

        getNextVariants(s, complete, incomplete);
        assertEquals("complete incorrect", 2, complete.accepted.size());
        assertEquals("complete incorrect", true, complete.accepted.contains("."));
        assertEquals("complete incorrect", true, complete.accepted.contains("#"));
        assertEquals("incomplete incorrect", 0, incomplete.accepted.size());
    }

    @Test
    public void testGetNextVariantsUndeterminedMulti() {
        String s = "?.#??#.##...???";
        getAllVariantsDepthFirst(s, variant -> false, reporter("complete"));
    }

    public void testGetNextVariantsDetermined() {
        String[] inputs = { ".", "#" };

        for (String s : inputs) {
            Recorder complete = new Recorder();
            Recorder incomplete = new Recorder();

            getNextVariants(s, complete, incomplete);
            assertEquals("complete incorrect", 1, complete.accepted.size());
            assertEquals("complete incorrect", s, complete.accepted.get(0));
            assertEquals("incomplete incorrect", 0, incomplete.accepted.size());
        }
    }

    @Test
    public void testGetNextVariantsDeterminedReporter() {
        String[] inputs = { ".", "#", "?" };

        for (String s : inputs) {
            Consumer<String> complete = reporter("complete");
            Consumer<String> incomplete = reporter("incomplete");

            getNextVariants(s, complete, incomplete);
        }
    }

    @Test
    public void testGetDamagedGroupSizes() {
        String[] inputs = {".", "#", "..###.####....##.#..##"};
        int[][] expected = {
                {},
                {1},
                {3, 4, 2, 1, 2},
        };

        for (int i=0; i<inputs.length; ++i) {
            String s = inputs[i];
            boolean matches = matchesDamagedGroups(s, expected[i], variant -> {});
            assertEquals(String.format("%d: matches incorrect", i), true, matches);
        }
    }

    @Test
    public void testBasicInputsPart1() {
        Collection<String> inputs = Stream.of(StringUtils.split(INPUTS, "\n")).collect(Collectors.toList());
        Collection<ConditionRecord> records = inputs.stream().map(s -> new ConditionRecord(s)).collect(Collectors.toList());
        long numArrangements =
                records
                        .stream()
                        .mapToLong(conditionRecord -> conditionRecord.getNumArrangements())
                        .sum();
        assertEquals("basic inputs incorrect", 21, numArrangements);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day12-p1.txt");
        Collection<ConditionRecord> records = inputs.stream().map(s -> new ConditionRecord(s)).collect(Collectors.toList());
        long numArrangements =
                records
                        .stream()
                        .mapToLong(conditionRecord -> conditionRecord.getNumArrangementsDepthFirst())
                        .sum();
        System.out.printf("Part1: %d\n", numArrangements);
//        assertEquals("basic inputs incorrect", 21, numArrangements);
    }

    @Test
    public void testBasicInputsCountsPart2() {
        Collection<String> inputs = Stream.of(StringUtils.split(INPUTS, "\n")).collect(Collectors.toList());
        inputs
                .stream()
                .map(s -> ConditionRecord.forPart2(s))
                .forEach(cr -> {
                    int numWild = StringUtils.countMatches(cr.springs, '?');
                    long numVariants = (long) Math.pow(2.0, (double) numWild);
                    System.out.printf("Recursion Depth: %d, # Variants '%s': %,d\n", cr.input.length(), cr.input, numVariants);
                });
    }

    @Test
    public void testBasicInputsPart2() {
        Collection<String> inputs = Stream.of(StringUtils.split(INPUTS, "\n")).collect(Collectors.toList());
        Collection<ConditionRecord> records = inputs.stream().map(s -> ConditionRecord.forPart2(s)).collect(Collectors.toList());
        long numArrangements =
                records
                        .stream()
                        .mapToLong(conditionRecord -> conditionRecord.getNumArrangements())
                        .sum();
        assertEquals("basic inputs incorrect", 525152, numArrangements);
    }
}
