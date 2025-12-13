package com.verde.advent.y2025;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Key invariant: no overlapping or touching ranges inside the set; they’re always merged.
 */
public final class RangeSet {
    // Always sorted, non-overlapping, non-adjacent
    private final NavigableSet<Range> ranges = new TreeSet<>();

    public RangeSet() {}

    public RangeSet(Collection<Range> initialRanges) {
        if (initialRanges != null) {
            for (Range r : initialRanges) {
                add(r);
            }
        }
    }

    /** Add a range, merging any overlaps/adjacent ranges. */
    public void add(Range range) {
        Objects.requireNonNull(range, "range must not be null");

        long start = range.getStart();
        long end   = range.getEnd();

        if (ranges.isEmpty()) {
            ranges.add(range);
            return;
        }

        // Start by looking at range whose start is <= new.start
        Range floor = ranges.floor(new Range(start, start));
        if (floor != null && floor.getEnd() + 1 >= start) {
            // Merge with floor
            start = Math.min(start, floor.getStart());
            end   = Math.max(end,   floor.getEnd());
            ranges.remove(floor);
        }

        // Merge with any subsequent overlapping/adjacent ranges
        Range current = ranges.ceiling(new Range(start, start));
        while (current != null && current.getStart() <= end + 1) {
            end = Math.max(end, current.getEnd());
            ranges.remove(current);
            current = ranges.ceiling(new Range(start, start));
        }

        ranges.add(new Range(start, end));
    }

    public void add(long start, long end) {
        add(new Range(start, end));
    }

    /** Add all ranges from another RangeSet. */
    public void addAll(RangeSet other) {
        Objects.requireNonNull(other, "other must not be null");
        for (Range r : other.ranges) {
            add(r);
        }
    }

    /** Subtract a single range from this set. */
    public void subtract(Range toSubtract) {
        Objects.requireNonNull(toSubtract, "toSubtract must not be null");
        if (ranges.isEmpty()) return;

        List<Range> toRemove = new ArrayList<>();
        List<Range> toAdd    = new ArrayList<>();

        // Find first possibly-affected range
        Range current = ranges.floor(new Range(toSubtract.getStart(), toSubtract.getStart()));
        if (current == null) {
            current = ranges.ceiling(new Range(toSubtract.getStart(), toSubtract.getStart()));
        }

        while (current != null && current.getStart() <= toSubtract.getEnd()) {
            // subtract toSubtract from current
            Collection<Range> pieces = current.subtract(toSubtract);
            toRemove.add(current);
            toAdd.addAll(pieces);
            current = ranges.higher(current);
        }

        ranges.removeAll(toRemove);
        for (Range r : toAdd) {
            // They shouldn't overlap each other, but using add() keeps invariants
            add(r);
        }
    }

    /** Subtract all ranges in another RangeSet. */
    public void subtractAll(RangeSet other) {
        Objects.requireNonNull(other, "other must not be null");
        for (Range r : other.ranges) {
            subtract(r);
        }
    }

    /** Check if a value is contained in any range. */
    public boolean contains(long value) {
        Range floor = ranges.floor(new Range(value, value));
        return floor != null && floor.getStart() <= value && value <= floor.getEnd();
    }

    /** Get an unmodifiable view of the ranges. */
    public Collection<Range> asRanges() {
        return Collections.unmodifiableCollection(ranges);
    }

    /** True if no ranges at all. */
    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    @Override
    public String toString() {
        return ranges.stream()
                .map(Range::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
