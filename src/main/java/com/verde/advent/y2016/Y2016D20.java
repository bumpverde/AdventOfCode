package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D20 {
    public static class Range {
        long min, max;
        
        public static Range newRange(String rep) {
            String[] parts = rep.split("-");
            return new Range(AdventUtils.toLong(parts[0]), AdventUtils.toLong(parts[1]));
        }
            
        public Range(long min, long max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
        }
        
        public long getSpan() {
            return (max - min) + 1;
        }
        
        public List<Range> subtract(Range r2) {
            long a = min;
            long b = max;
            long c = r2.min;
            long d = r2.max;
            
            if ((d<a || b<c) || (c<a && a<b && b<d)) {
                return null;
            }
            
            List<Range> pieces = new LinkedList<Range>();
            
            // https://math.stackexchange.com/questions/1261613/can-you-help-me-subtract-intervals
            if (a<=c && c<=b && b<=d) {
                if (a <= c-1) {
                    pieces.add(new Range(a, c-1));
                }
            } else if (c<=a && a<=d && d<=b ) {
                if (d+1 <= b) {
                    pieces.add(new Range(d+1, b));
                }
            } else if ( a<=c && c<=d && d<=b) {
                if (a <= c-1) {
                    pieces.add(new Range(a, c-1));
                }
                if (d+1 <= b) {
                    pieces.add(new Range(d+1, b));
                }
            } 
            
            return pieces;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            long result = 1;
            result = prime * result + max;
            result = prime * result + min;
            return (int) result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Range other = (Range) obj;
            if (max != other.max)
                return false;
            if (min != other.min)
                return false;
            return true;
        }
        
        public static Comparator<Range> getComparator() {
            return (r1, r2) -> {
                if (r1.min < r2.min) {
                    return -1;
                }

                if (r1.min > r2.min) {
                    return 1;
                }

                return Long.compare(r1.min,  r2.min);
            };
        }
        
        @Override
        public String toString() {
            return String.format("[%d, %d]", min, max);
        }
    }

    public static Set<Range> computeOpenRanges(Range initialRange, Collection<String> blackListSpecs) {
        List<Range> blackLists = blackListSpecs.stream().map(s -> Range.newRange(s)).collect(Collectors.toList());
        
        Set<Range> openRanges = new TreeSet<Range>(Range.getComparator());
        openRanges.add(initialRange);
    
        // For each blacklist entry, remove it from each of the remaining open ranges
        for (Range blackList : blackLists) {
            Set<Range> nextPassOpenRanges = new TreeSet<Range>(Range.getComparator());
            
            for (Range open : openRanges) { 
                Collection<Range> splits = open.subtract(blackList);
                
                // If there was an intersection resulting in split parts, add those to the next pass
                if (splits != null) {
                    nextPassOpenRanges.addAll(splits);
                } else {
                    // Else, the open range is un-obstructed, so add it in for the next pass
                    nextPassOpenRanges.add(open);
                }
            }
            
            // Reset for the next pass
            openRanges = nextPassOpenRanges;
        }
        
        
        return openRanges;
    }
    
    public static long computeMinOpenIp(Range initialRange, Collection<String> blackLists) {
        return computeOpenRanges(initialRange, blackLists).stream().findFirst().map(r -> r.min).orElse(-1L);
    }
    
    @Test
    public void testComputeMinOpenIp() {
        Collection<String> inputs = Stream.of("5 - 8", "0 - 2", "4 - 7").collect(Collectors.toList());
        assertEquals("computeMinOpenIp is incorrect", 3L, computeMinOpenIp(Range.newRange("0 - 9"), inputs));
    }
    
    @Test
    public void testStarOne() {
        assertEquals("star one is incorrect", 32259706L, computeMinOpenIp(IP_RANGE, STAR_INPUTS));
    }
    
    @Test
    public void testStarTwo() {
        Set<Range> openRanges = computeOpenRanges(IP_RANGE, STAR_INPUTS);
        long count = openRanges.stream().mapToLong(Range::getSpan).sum();
        long expected = 100444411L; // incorrect
        assertEquals("star two is incorrect", expected, count);
    }
    
    static Range IP_RANGE = new Range(0L, 4294967295L);

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day20-inputs.txt");
}
