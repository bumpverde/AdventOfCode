package com.verde.advent.day2;


/**
 * PART1:
 * The spreadsheet consists of rows of apparently-random numbers. 
 * To make sure the recovery process is on the right track, they need you to calculate the spreadsheet's checksum. 
 * For each row, determine the difference between the largest value and the smallest value; the checksum is the sum of all of these differences.
 * 
 * For example, given the following spreadsheet:
 * 
 * 5 1 9 5
 * 7 5 3
 * 2 4 6 8
 * 
 * The first row's largest and smallest values are 9 and 1, and their difference is 8.
 * The second row's largest and smallest values are 7 and 3, and their difference is 4.
 * The third row's difference is 6.
 * In this example, the spreadsheet's checksum would be 8 + 4 + 6 = 18.
 * 
 * 
 * PART2:
 * "Based on what we're seeing, it looks like all the User wanted is some information about the evenly divisible values in the spreadsheet. 
 * Unfortunately, none of us are equipped for that kind of calculation - most of us specialize in bitwise operations."
 * 
 * It sounds like the goal is to find the only two numbers in each row where one evenly divides the other - 
 * that is, where the result of the division operation is a whole number. 
 * They would like you to find those numbers on each line, divide them, and add up each line's result.
 * 
 * For example, given the following spreadsheet:
 * 
 * 5 9 2 8
 * 9 4 7 3
 * 3 8 6 5
 * 
 * In the first row, the only two numbers that evenly divide are 8 and 2; the result of this division is 4.
 * In the second row, the two numbers are 9 and 3; the result is 3.
 * In the third row, the result is 2.
 * In this example, the sum of the results would be 4 + 3 + 2 = 9.
 * 
 * @author bverde
 */
public class Day2 {
    public static int compute1(int[][] input) throws RuntimeException {
        int sum = 0;
        for (int row=0; row<input.length; ++row) {
            int[] vals = input[row];
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            
            // Compute min/max
            for (int col=0; col<vals.length; ++col) {
                min = Math.min(vals[col], min);
                max = Math.max(vals[col], max);
            }
            
            int diff = max - min;
            sum += diff;
        }
        
        return sum;
    }
    
    public static int compute2(int[][] input) throws RuntimeException {
        int sum = 0;
        for (int row=0; row<input.length; ++row) {
            int[] vals = input[row];
            
            // check 
            for (int col=0; col<vals.length; ++col) {
                int v1 = vals[col];
                for (int check=col+1; check<vals.length; ++check) {
                    int v2 = vals[check];
                    if ((v1 % v2) == 0) {
                        sum += v1 / v2;
                    } else if ((v2 % v1) == 0) {
                        sum += v2 / v1;
                    }
                }
            }
        }
        
        return sum;
    }
}
