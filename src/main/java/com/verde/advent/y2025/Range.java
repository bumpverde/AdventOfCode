package com.verde.advent.y2025;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Range implements Comparable<Range> {
    private long start;
    private long end;

    public Range(long start, long end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public static boolean isRangeSpec(String rangeSpec) {
        return rangeSpec.contains("-");
    }

    public static Range from(String rangeSpec) {
        if (! isRangeSpec(rangeSpec)) {
            return null;
        }

        String[] rangeSpecs = rangeSpec.split("-");
        long start = Long.parseLong(rangeSpecs[0]);
        long end = Long.parseLong(rangeSpecs[1]);

        return new Range(start, end);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getCount() {
        return (end - start) + 1;
    }

    public boolean contains(long value) {
        return value >= start && value <= end;
    }

    @Override
    public int compareTo(Range o) {
        int cmp = Long.compare(this.start, o.start);
        if (cmp != 0) return cmp;
        return Long.compare(this.end, o.end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;
        Range range = (Range) o;
        return start == range.start && end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + "]";
    }

    public static long min(Collection<Range> ranges) {
        return ranges.stream().mapToLong(Range::getStart).min().orElse(0L);
    }

    public static long max(Collection<Range> ranges) {
        return ranges.stream().mapToLong(Range::getEnd).max().orElse(0L);
    }

    public Collection<Range> subtract(Range b) {
        return Range.subtract(this, b);
    }

    /**
     * Return a Collection of the Ranges in a that are NOT present in b.
     *
     * @param a the Range to subtract from
     * @param b the Range to subtract
     * @return a Collection of the Ranges that are in a but are NOT in b
     */
    public static Collection<Range> subtract(Range a, Range b) {
        Objects.requireNonNull(a, "a range must not be null");
        Objects.requireNonNull(b, "b range must not be null");

        List<Range> result = new ArrayList<>(2);

        long aStart = a.start;
        long aEnd   = a.end;

        long bStart = b.start;
        long bEnd   = b.end;

        // Case 1: No overlap at all → return this range unchanged
        if (bEnd < aStart || bStart > aEnd) {
            result.add(new Range(aStart, aEnd));
            return result;
        }

        // Case 2: Overlap exists → compute leftover portions

        // Left side remainder, if any
        if (bStart > aStart) {
            long leftStart = aStart;
            long leftEnd = bStart - 1;
            if (leftStart <= leftEnd) {
                result.add(new Range(leftStart, leftEnd));
            }
        }

        // Right side remainder, if any
        if (bEnd < aEnd) {
            long rightStart = bEnd + 1;
            long rightEnd = aEnd;
            if (rightStart <= rightEnd) {
                result.add(new Range(rightStart, rightEnd));
            }
        }

        return result;
    }

    public Collection<Range> union(Range b) {
        return Range.union(this, b);
    }

    /**
     * Union of two ranges (inclusive, merging if overlapping or adjacent).
     */
    public static Collection<Range> union(Range a, Range b) {
        Objects.requireNonNull(a, "a must not be null");
        Objects.requireNonNull(b, "b must not be null");

        long aStart = a.start;
        long aEnd   = a.end;

        long bStart = b.start;
        long bEnd   = b.end;

        // Check for overlap or adjacency
        // Overlap/adjacency happens if: a.start <= b.end+1 AND b.start <= a.end+1
        if (aStart <= bEnd + 1 && bStart <= aEnd + 1) {
            long mergedStart = Math.min(aStart, bStart);
            long mergedEnd   = Math.max(aEnd, bEnd);

            return List.of(new Range(mergedStart, mergedEnd));
        }

        // Otherwise they are disjoint — return them in sorted order
        if (aStart < bStart) {
            return List.of(a, b);
        } else {
            return List.of(b, a);
        }
    }
}
