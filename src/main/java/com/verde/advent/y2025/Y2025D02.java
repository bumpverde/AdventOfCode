package com.verde.advent.y2025;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.LongStream;

public class Y2025D02 {
    public final String STAR_INPUT = "1-14,46452718-46482242,16-35,92506028-92574540,1515128146-1515174322,56453-79759,74-94,798-971,49-66,601-752,3428-4981,511505-565011,421819-510058,877942-901121,39978-50500,9494916094-9494978970,7432846301-7432888696,204-252,908772-990423,21425-25165,1030-1285,7685-9644,419-568,474396757-474518094,5252506279-5252546898,4399342-4505058,311262290-311393585,1895-2772,110695-150992,567521-773338,277531-375437,284-364,217936-270837,3365257-3426031,29828-36350";
    public final String SAMPLE_INPUT = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124";

    public LongStream range(String rangeSpec) {
        String[] rangeSpecs = rangeSpec.split("-");
        long start = Long.parseLong(rangeSpecs[0]);
        long end = Long.parseLong(rangeSpecs[1]);

        return LongStream.rangeClosed(start, end);
    }

    public String[] chopEqualParts(String s, int partSize) {
        // If it can't be split up equally, return null
        if ((s.length() % partSize) != 0) {
            return null;
        }

        int numParts = s.length() / partSize;
        String[] result = new String[numParts];
        for (int i = 0; i < numParts; i++) {
            result[i] = s.substring(i * partSize, (i + 1) * partSize);
        }

        return result;
    }

    public boolean isInvalidId(long id) {
        String strRep = Long.toString(id);
        int len = strRep.length();

        // If the string has an odd number of chars, then it can't be split into
        // two equal repating strings, so it is valid.
        if ((len % 2) != 0) {
            return false;
        }

        // Split the string in half.
        // The id is invalid if the halfs match
        String h1 =  strRep.substring(0, len / 2);
        String h2 = strRep.substring(len / 2);
        boolean invalid = h1.equals(h2);
        return invalid;
    }

    public boolean isInvalidIdStar2(long id) {
        String strRep = Long.toString(id);
        int len = strRep.length();

        // Start looking for patterns of 1, 2, 3, ... chars up to half the input.
        // If any of the substrings are all equal, then the ID is invalid.
        for (int chunkSize=1; chunkSize<= len/2; chunkSize++) {
            String[] chunks = chopEqualParts(strRep, chunkSize);

            // Put all the chunks into a Set and verify that there is only one member.
            // If so, all the chunks are equal and the ID is invalid.
            if (chunks != null) {
                Set<String> chunkIds =  new HashSet<>(List.of(chunks));
                if (chunkIds.size() == 1) {
                    return true;
                }
            }
        }

        // If we get here, the ID is valid
        return false;
    }


    public BigInteger sumInvalidIds(String allSpecs, Predicate<Long> isInvalid) {
        AtomicReference<BigInteger> sum = new AtomicReference<>(BigInteger.ZERO);
        String[] specs = allSpecs.split(",");

        for (String spec : specs) {
            System.out.printf("Processing spec %s\n", spec);

            LongStream range = range(spec);
            range.forEach(id -> {
                if (isInvalid.test(id)) {
                    System.out.printf("\tInvalid ID: %d\n", id);
                    sum.set(sum.get().add(BigInteger.valueOf(id)));
                }
            });
        }

        return sum.get();
    }

    @Test
    public void testSampleInputsStar1() {
        BigInteger sum = sumInvalidIds(SAMPLE_INPUT, id -> isInvalidId(id));
        BigInteger expected = BigInteger.valueOf(1227775554L);
        Assert.assertEquals("star1 sample sum is incorrect", expected, sum);
    }

    @Test
    public void testStar1() {
        BigInteger sum = sumInvalidIds(STAR_INPUT, id -> isInvalidId(id));
        BigInteger expected = BigInteger.valueOf(8576933996L);
        Assert.assertEquals("star1 sum is incorrect", expected, sum);
    }

    @Test
    public void testSampleInputsStar2() {
        BigInteger sum = sumInvalidIds(SAMPLE_INPUT, id -> isInvalidIdStar2(id));
        BigInteger expected = BigInteger.valueOf(4174379265L);
        Assert.assertEquals("star2 sample sum is incorrect", expected, sum);
    }

    @Test
    public void testStar2() {
        BigInteger sum = sumInvalidIds(STAR_INPUT, id -> isInvalidIdStar2(id));
        BigInteger expected = BigInteger.valueOf(8576933996L);
        Assert.assertEquals("star2 sum is incorrect", expected, sum);
    }
}
