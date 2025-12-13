package com.verde.advent.y2025;

import com.verde.advent.AdventUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Y2025D05 {
    public static final Collection<String> SAMPLE_INPUTS =
            List.of("3-5",
                    "10-14",
                    "16-20",
                    "12-18",
                    "",
                    "1",
                    "5",
                    "8",
                    "11",
                    "17",
                    "32");
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2025/day5-p1.txt");

    public Collection<Range> parseRanges(Collection<String> inputs) { //, , Set<String> ingredientIds) {
        return inputs
                .stream()
                .filter(Range::isRangeSpec)
                .map(Range::from)
                .collect(Collectors.toList());
    }

    public Set<Long> parseIngredients(Collection<String> inputs) {
        return inputs
                .stream()
                .filter(spec -> !Range.isRangeSpec(spec) && !spec.isBlank())
                .map(s -> Long.parseLong(s))
                .collect(Collectors.toSet());
    }

    public boolean isFreshIngredient(long ingredient, Collection<Range> ranges) {
        return ranges.stream().anyMatch(r -> r.contains(ingredient));
    }

    public long countFreshIngredients(Collection<Range> ranges, Set<Long> ingredients) {
        return ingredients.stream().filter(id -> isFreshIngredient(id, ranges)).count();
    }

    public long countFreshIngredients(Collection<Range> ranges) {
        // We could iterate over all the ingredients in the overall range
        // and check each one for freshness, but that is very expensive.
        // Instead, create a RangeSet out of all the fresh ingredient ranges,
        // which will handle all the merges so that we end up with non-overlapping ranges.
        RangeSet freshMergedRanges = new RangeSet(ranges);

        // Sum up the counts of the merged ranges
        return freshMergedRanges.asRanges().stream().mapToLong(Range::getCount).sum();
    }

    @Test
    public void testSampleStar1() {
        Collection<Range> ranges = parseRanges(SAMPLE_INPUTS);
        Set<Long> ingredients = parseIngredients(SAMPLE_INPUTS);
        long actual = countFreshIngredients(ranges, ingredients);
        long expected = 3;
        Assert.assertEquals("sample star1 fresh ingredient count is incorrect", expected, actual);
    }

    @Test
    public void testStar1() {
        Collection<Range> ranges = parseRanges(STAR_INPUTS);
        Set<Long> ingredients = parseIngredients(STAR_INPUTS);
        long actual = countFreshIngredients(ranges, ingredients);
        long expected = 770;
        Assert.assertEquals("star1 fresh ingredient count is incorrect", expected, actual);
    }

    @Test
    public void testSampleStar2() {
        Collection<Range> ranges = parseRanges(SAMPLE_INPUTS);
        long actual = countFreshIngredients(ranges);
        long expected = 14;
        Assert.assertEquals("sample star2 fresh ingredient count is incorrect", expected, actual);
    }

    @Test
    public void testStar2() {
        Collection<Range> ranges = parseRanges(STAR_INPUTS);
        long actual = countFreshIngredients(ranges);
        long expected = 357674099117260L;
        Assert.assertEquals("star2 fresh ingredient count is incorrect", expected, actual);
    }
}
