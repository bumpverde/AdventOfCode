package com.verde.advent.y2018.d1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * --- Day 1: Chronal Calibration ---
 * "We've detected some temporal anomalies," one of Santa's Elves at the Temporal Anomaly Research and Detection Instrument Station tells you. 
 * She sounded pretty worried when she called you down here. "At 500-year intervals into the past, someone has been changing Santa's history!"
 * "The good news is that the changes won't propagate to our time stream for another 25 days, and we have a device" - she attaches something 
 * to your wrist - "that will let you fix the changes with no such propagation delay. It's configured to send you 500 years further into the 
 * past every few days; that was the best we could do on such short notice."
 * <p>
 * "The bad news is that we are detecting roughly fifty anomalies throughout time; the device will indicate fixed anomalies with stars. 
 * The other bad news is that we only have one device and you're the best person for the job! Good lu--" She taps a button on the device and 
 * you suddenly feel like you're falling. To save Christmas, you need to get all fifty stars by December 25th.
 * <p>
 * Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar; the second puzzle is unlocked 
 * when you complete the first. Each puzzle grants one star. Good luck!
 * <p>
 * After feeling like you've been falling for a few minutes, you look at the device's tiny screen. 
 * "Error: Device must be calibrated before first use. Frequency drift detected. Cannot maintain destination lock." 
 * Below the message, the device shows a sequence of changes in frequency (your puzzle input). A value like +6 means the current frequency 
 * increases by 6; a value like -3 means the current frequency decreases by 3.
 * <p>
 * For example, if the device displays frequency changes of +1, -2, +3, +1, then starting from a frequency of zero, the following changes would occur:
 * <pre>
 * Current frequency  0, change of +1; resulting frequency  1.
 * Current frequency  1, change of -2; resulting frequency -1.
 * Current frequency -1, change of +3; resulting frequency  2.
 * Current frequency  2, change of +1; resulting frequency  3.
 * In this example, the resulting frequency is 3.
 * </pre>
 * <p>
 * Here are other example situations:
 * <pre>
 * +1, +1, +1 results in  3
 * +1, +1, -2 results in  0
 * -1, -2, -3 results in -6
 * </pre>
 * Starting with a frequency of zero, what is the resulting frequency after all of the changes in frequency have been applied?
 * 
 * @author bverde
 */
public class Day1 {
    /**
     * Given an array of ints, sum them up.
     * This is the first star.
     *
     * @param inputs
     * @return the sum of the inputs
     */
    public static long sum(int[] inputs) {
        return Arrays.stream(inputs).sum();
    }
    
    /**
     * Find the first repeated sum that occurs at least repetitionCount times.
     * This is the second star.
     * 
     * @param inputs
     * @param repetitionCount
     * @return first repeated sum that occurs at least repetitionCount times
     */
    public static int findFirstRepeatedSum(int[] inputs, int repetitionCount) {
        // Keep track of the sum counts
        Map<Integer,Integer> sumCounts = new HashMap<Integer,Integer>();
        
        int sum = 0;
        boolean done = false;
        for (int i=0; !done; ++i) {
            // Update current count of sum
            int count = sumCounts.containsKey(sum) ? sumCounts.get(sum) : 0;
            sumCounts.put(sum,  ++count);
//            System.out.printf("Seeing sum %d for the %d time\n", sum, count);
            
            if (! (done = count == repetitionCount)) {
                sum += inputs[i % inputs.length];
            }
        }
        
//        System.out.printf("Sum %d was the first to be repeated %d times\n\n", sum, repetitionCount);
        return sum;
    }
}
