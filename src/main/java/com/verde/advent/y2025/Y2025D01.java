package com.verde.advent.y2025;

import com.verde.advent.AdventUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * If you are asked to prove you own this account by an Advent of Code administrator:
 * Owner proof: ownerproof-5552648-1765493107-db687b167ccf
 */
public class Y2025D01 {
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2025/day1-p1.txt");
    public static int STARTING_POS = 50;
    public static int NUM_SLOTS = 100;

    public int calcOffset(String cmd, boolean modOffset) {
        int offset = Integer.parseInt(cmd.substring(1));

        // Keep it in the range, which we only do for star1
        if (modOffset) {
            offset = offset % NUM_SLOTS;
        }

        // Left is negative, right is positive
        if (cmd.startsWith("L")) {
            offset = -offset;
        }

        return offset;
    }

    public int computePasswordStar1(Collection<String> cmds) {
        int numZeros = 0;
        int currPos = STARTING_POS;

        for (String cmd : cmds) {
            int offset =  calcOffset(cmd, true);
            currPos += offset;

            if (currPos < 0) {
                currPos = NUM_SLOTS - (Math.abs(currPos) %  NUM_SLOTS);
            } else if (currPos > 0) {
                currPos %= NUM_SLOTS;
            }

            // Did we land on 0?
            if (currPos == 0) {
                numZeros++;
            }

            System.out.printf("Rotated %s by %d to point at %d - %d zeros\n", cmd, offset, currPos, numZeros);
        }

        return numZeros;
    }

    public int computePasswordStar2(Collection<String> cmds) {
        int numZeros = 0;
        int currPos = STARTING_POS;

        for (String cmd : cmds) {
            int offset =  calcOffset(cmd, false); // don't mod the offset for star2
            int increment = (offset < 0) ? -1 : 1;
            int nIters = Math.abs(offset);

            while (--nIters >= 0) {
                currPos += increment;

                if (currPos < 0) {
                    currPos = NUM_SLOTS - 1;
                } else if (currPos >= NUM_SLOTS) {
                    currPos = 0;
                }

                // Did we land on 0?
                if (currPos == 0) {
                    numZeros++;
                }
            }

            System.out.printf("Rotated %s by %d to point at %d - %d zeros\n", cmd, offset, currPos, numZeros);
        }

        return numZeros;
    }

    public Collection<String> getSampleInputs() {
        Collection<String> inputs = new LinkedList<>();
        inputs.add("L68");
        inputs.add("L30");
        inputs.add("R48");
        inputs.add("L5");
        inputs.add("R60");
        inputs.add("L55");
        inputs.add("L1");
        inputs.add("L99");
        inputs.add("R14");
        inputs.add("L82");

        return inputs;
    }

    @Test
    public void testSampleInputsStar1() {
        int password = computePasswordStar1(getSampleInputs());
        Assert.assertEquals("sample password is incorrect", 3, password);
    }

    @Test
    public void testStar1() {
        int password = computePasswordStar1(STAR_INPUTS);
        Assert.assertEquals("star1 password is incorrect", 1139, password);
    }

    @Test
    public void testSampleInputsStar2() {
        int password = computePasswordStar2(getSampleInputs());
        Assert.assertEquals("sample password is incorrect", 6, password);
    }

    @Test
    public void testStar2() {
        int password = computePasswordStar2(STAR_INPUTS);
        Assert.assertEquals("star2 password is incorrect", 1139, password);
    }
}
