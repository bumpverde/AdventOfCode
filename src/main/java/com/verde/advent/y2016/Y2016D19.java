package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class Y2016D19 {
    public int getWinner(int numElves) {
        // Represent the circle as an array, but the last elf takes from the first.
        // The elf to the "left" of index i is i+1.
        int[] presents = new int[numElves];
        
        // Keep track of the names.
        // The elves are 1-based, not 0-based, so name them accordingly
        int[] names = new int[numElves];
        Arrays.setAll(names, (index) -> index + 1);
        
        // Dole out the gifts - gotta keep the elves happy!
        Arrays.setAll(presents, (index) -> 1);
        
        // Steal away until there is only one elf left with all the presents
        int numRounds = 0;
        while (Arrays.stream(presents).filter(x -> x > 0).count() > 1) {
            for (int i=0; i<numElves; ++i) {
                // You can only take if you still have some
                if (presents[i] > 0) {
                    // Try to find the next elf to your left that has presents
                    int elfToTheLeft = (i+1) % numElves;
                    while ((presents[elfToTheLeft] == 0) && (elfToTheLeft != i)) {
                        elfToTheLeft = (elfToTheLeft + 1) % numElves;
                    }
                    
                    // If there is someone left to steal from, go for it
                    if (elfToTheLeft != i) {
                        presents[i] += presents[elfToTheLeft];  // happiness!
                        presents[elfToTheLeft] = 0;             // sadness...
                    }
                }
            }
            
            ++numRounds;
        }            

        // Determine the winner, whose names are stored in the names array
        int winner = -1;
        for (int i=0; i<presents.length; ++i) {
            if (presents[i] > 0) {
                winner = names[i];
                break;
            }
        }
        
        System.out.printf("Elf #%d won after %d rounds\n",  winner, numRounds);
        
        return winner;
    }
    
    public int getWinnerTricky(int numElves) {
        // Represent the circle as a list, and use the elves names as their presents
        List<Integer> presents = new ArrayList<Integer>();
        IntStream.range(0, numElves).forEach(i -> presents.add(i+1));
        
        // Steal away until there is only one elf left with all the presents
        int currElf = 1;        // always start with Elf1  ??
        int indexCurrElf = 0;   // always start with Elf1
        int numKilled = 0;
        while (presents.size() > 1) {
            // In this version, the circle is compacted every time an elf is stolen from.
            // So, there are never blank spaces. To choose who to steal from, look across 
            // the circle and choose the closes to your left. Which means, divide by two
            // and round down (which integer division will do for free). If there are 
            int distanceAcross = presents.size() / 2;
            int indexToStealFrom = (indexCurrElf + distanceAcross) % presents.size();
            int elfToStealFrom = presents.get(indexToStealFrom);

            // Remember the name of the next elf whose up, so we can find him (positions will change)
            int indexNextElf = (indexCurrElf + 1) % presents.size();
            if (indexNextElf == indexToStealFrom) {
                indexNextElf = (indexNextElf + 1) % presents.size();
            }
            int nameNextElf = presents.get(indexNextElf);
            
//            System.out.printf("Elf %d at %d is about to take from Elf %d at %d (%d will be the next current elf)\n", currElf, indexCurrElf, elfToStealFrom, indexToStealFrom, nameNextElf);
            
            // To kill the elf, just remove him from the presents
            presents.remove(indexToStealFrom);
            if ((++numKilled % 10000) == 0) {
                System.out.printf("%d\n ", numKilled);
            }
            
            // Now find out where the next elf now is
            indexCurrElf = presents.indexOf(nameNextElf);
            currElf = presents.get(indexCurrElf);
//            System.out.printf("Current Elf is now %d at %d\n", currElf, indexCurrElf);
        }            

        // Determine the winner, whose names are stored in the names array
        int winner = presents.get(0);
        
        System.out.printf("Elf #%d won\n",  winner);
        
        return winner;
    }
    
    @Test
    public void testGetWinner() {
        int[] inputs = { 2, 3, 4, 5, 6, 7, 9, 20, 30, };
        int[] expected = { 1, 3, 1, 3, 5, 7, 3, 9, 29 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("getWinner(%d) incorrect", inputs[i]), expected[i], getWinner(inputs[i]));
        }
    }

    @Test
    public void testGetWinnerTricky() {
        int[] inputs = { 5, 1000000 };
        int[] expected = { 2, 10 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals(String.format("getWinnerTricky(%d) incorrect", inputs[i]), expected[i], getWinnerTricky(inputs[i]));
        }
    }

    @Test
    public void testStarOne() {
        int input = 3014387;
        int expected = 1834471;
        assertEquals("star one incorrect", expected, getWinner(input));
    }

    @Test
    public void testStarTwo() {
        System.out.println("Runnin star two...");
        int input = 3014387;
        int expected = 1834471;
        assertEquals("star two incorrect", expected, getWinnerTricky(input));
    }
}
