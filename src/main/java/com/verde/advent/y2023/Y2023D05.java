package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.assertEquals;

public class Y2023D05 {
    public static final String BASIC_TEST_INPUTS = """
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4""";

    public static class Range {
        long min, max;

        public Range(long min, long max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
        }

        public long getSpan() {
            return (max - min) + 1;
        }
        public boolean inRange(long v) { return v >= min && v <= max; }
    }

    public static class RangeMapping {
        public Range srcRange, dstRange;

        public RangeMapping(long srcMin, long dstMin, long span) {
            this.srcRange = new Range(srcMin, srcMin + span - 1);
            this.dstRange = new Range(dstMin, dstMin + span - 1);
        }

        public boolean containsSrc(long v) { return srcRange.inRange(v); }
        public long mapSrcToDst(long v) { return dstRange.min + (v - srcRange.min); }
    }

    public static class RangeMappings {
        public String srcName, dstName;
        public Collection<RangeMapping> mappings = new LinkedList<>();

        public RangeMappings(String srcName, String dstName) {
            this.srcName = srcName;
            this.dstName = dstName;
        }

        public RangeMapping getMapping(long srcVal) {
            return mappings.stream().filter(m -> m.containsSrc(srcVal)).findFirst().orElse(null);
        }

        public long mapSrcToDst(long srcVal) {
            // Find the RangeMapping for the value
            RangeMapping mapping = getMapping(srcVal);

            // Start with a result of the incoming srcVal
            long result = srcVal;

            // If there is a mapping, apply it.
            // If there is no mapping, we will return the srcVal
            if (mapping != null) {
                result = mapping.mapSrcToDst(srcVal);
                //System.out.printf("Using %s-%s mapping [%,d; %,d] to map %,d to [%,d: %,d]: result=%,d\n", srcName, dstName, mapping.srcRange.min, mapping.srcRange.max, srcVal, mapping.dstRange.min, mapping.dstRange.max, result);
            }

            return result;
        }
    }

    public long[] seedsToPlant = null;
    public Map<String,RangeMappings> rangeMappingsBySrc = null;

    public void init(Collection<String> inputs, boolean seedsAreRanges) {
        AtomicReference<RangeMappings> currMappings = new AtomicReference<>(null);
        rangeMappingsBySrc = new HashMap<String,RangeMappings>();

        inputs.stream().filter(s -> !s.trim().isEmpty()).forEach(s -> {
            if (s.startsWith("seeds:")) {
                // Set up the seeds
                String[] seeds = StringUtils.split(s.substring("seeds: ".length()), " ");
                seedsToPlant = new long[seeds.length];
                for (int i = 0; i < seeds.length; ++i) {
                    try {
                        seedsToPlant[i] = Long.parseLong(seeds[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (s.endsWith("map:")) {
                // Start a new collection of mappings
                String[] parts = StringUtils.split(s,"-");
                String src = parts[0];
                String dst = StringUtils.remove(parts[2], " map:");
                currMappings.set(new RangeMappings(src, dst));
                rangeMappingsBySrc.put(src, currMappings.get());
            } else {
                // Set a specific mapping
                String[] parts = StringUtils.split(s, " ");
                try {
                    long dstMin = Long.parseLong(parts[0]);
                    long srcMin = Long.parseLong(parts[1]);
                    long span = Long.parseLong(parts[2]);
                    currMappings.get().mappings.add(new RangeMapping(srcMin, dstMin, span));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.printf("#inputs = %d, #mappings = %d, #seeds = %,d, seedsAreRanges=%b\n", inputs.size(), rangeMappingsBySrc.size(), seedsToPlant.length, seedsAreRanges);
    }

    public static long map(String srcName, String dstName, Map<String, RangeMappings> allRangeMappings, long srcVal) {
        boolean debug = false;
        if (debug) {
            System.out.printf("%s=%,d", srcName, srcVal);
        }

        long mappedVal = srcVal;
        RangeMappings stepMappings = null;
        do {
            String nextSrcName = (stepMappings == null) ? srcName : stepMappings.dstName;
            stepMappings = allRangeMappings.get(nextSrcName);
            if (stepMappings != null) {
                mappedVal = stepMappings.mapSrcToDst(mappedVal);
            }

            if (debug) {
                System.out.printf(" -> %s=%,d", stepMappings.dstName, mappedVal);
                RangeMapping rangeMapping = stepMappings.getMapping(mappedVal);
                if (rangeMapping != null) {
                    System.out.printf(" [%,d -> %,d: %,d]", rangeMapping.srcRange.min, rangeMapping.dstRange.min, rangeMapping.srcRange.getSpan());
                }
            }
        } while (!stepMappings.dstName.equals(dstName));

        if (debug) {
            System.out.println();
        }

        if (mappedVal == 0) {
            System.out.printf("Seed %,d mapped to 0!\n", srcVal);
        }

        return mappedVal;
    }

    public Range getNextSeedRange(long[] seedsToPlant, int currSeedIndex, boolean seedsAreRanges) {
        long currSeed = seedsToPlant[currSeedIndex];
        Range range = !seedsAreRanges
                ? new Range(currSeed, currSeed)
                : new Range(currSeed, currSeed + seedsToPlant[currSeedIndex + 1] - 1);
        return range;
    }

    public long getClosestLocation(boolean seedsAreRanges, long[] expected) {
        long totalSeeds = 0;
        System.out.printf("Processing seed ranges:\n");
        for (int i=0; i<seedsToPlant.length; ++i) {
            Range seedRange = getNextSeedRange(seedsToPlant, i, seedsAreRanges);
            System.out.printf("\t[%,d - %,d]:\t\t%,d seeds\n", seedRange.min, seedRange.max, seedRange.getSpan());
            totalSeeds += seedRange.getSpan();
            i += seedsAreRanges ? 1 : 0;
        }
        System.out.printf("Total seeds to process: %,d\n\n", totalSeeds);

        long minLocation = Long.MAX_VALUE;
        for (int i=0; i<seedsToPlant.length; ++i) {
            Range seedRange = getNextSeedRange(seedsToPlant, i, seedsAreRanges);
            i += seedsAreRanges ? 1 : 0;
            System.out.printf("Processing seed range [%,d - %,d] (%,d seeds)\n", seedRange.min, seedRange.max, seedRange.getSpan());
            for (long seed = seedRange.min; seed <= seedRange.max; ++seed) {
                long result = map("seed", "location", rangeMappingsBySrc, seed);
                minLocation = Math.min(minLocation, result);
                if (expected != null) {
                    assertEquals(String.format("Seed %d is incorrect", seed), expected[i], result);
                }

                if ((seed % 10_000_000) == 0) {
                    System.out.printf("\tseed = %,d, minLocation = %,d\n", seed, minLocation);
                }
            }
        }

        return minLocation;
    }

    @Test
    public void testBasicInputsPart1() {
        long[] expected = { 82, 43, 86, 35 };
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS, "\n")).collect(Collectors.toList());
        init(inputs, false);
        getClosestLocation(false, expected);
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day5-p1.txt");

        init(inputs, false);
        long actual = getClosestLocation(false, null);
        System.out.printf("%d\n", actual);
    }

    @Test
    public void testBasicInputsPart2() {
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS, "\n")).collect(Collectors.toList());
        init(inputs, true);
        long actual = getClosestLocation(true, null);
        System.out.printf("%d\n", actual);
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day5-p1.txt");
        long expected = 12_634_632L;

        // I am seeing seed 2,281,290,972 map to the location 0, which becomes the "closest location"
        // for all the seeds, but which is not the answer. Since all these mappings produce positive
        // numbers, and if one maps to 0, how can the closest location then not be 0?
        //seedsToPlant = new long[] { 2_281_290_972L, 1L };  // reset seeds to debug how this seed maps to location 0
        init(inputs, true);
        long actual = getClosestLocation(true, null);
        System.out.printf("%d\n", actual);
    }
}
