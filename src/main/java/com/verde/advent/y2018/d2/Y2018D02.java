package com.verde.advent.y2018.d2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * --- Day 2: Inventory Management System ---
 * You stop falling through time, catch your breath, and check the screen on the device. 
 * "Destination reached. Current Year: 1518. Current Location: North Pole Utility Closet 83N10." 
 * You made it! Now, to find those anomalies.
 * <p>
 * Outside the utility closet, you hear footsteps and a voice. "...I'm not sure either. 
 * But now that so many people have chimneys, maybe he could sneak in that way?" Another voice responds, 
 * "Actually, we've been working on a new kind of suit that would let him fit through tight spaces like 
 * that. But, I heard that a few days ago, they lost the prototype fabric, the design plans, everything! 
 * Nobody on the team can even seem to remember important details of the project!"
 * <p>
 * Wouldn't they have had enough fabric to fill several boxes in the warehouse? They'd be stored together, 
 * so the box IDs should be similar. Too bad it would take forever to search the warehouse for two similar 
 * box IDs..." They walk too far away to hear any more.
 * <p>
 * Late at night, you sneak to the warehouse - who knows what kinds of paradoxes you could cause if you were 
 * discovered - and use your fancy wrist device to quickly scan every box and produce a list of the likely 
 * candidates (your puzzle input).
 * <p>
 * To make sure you didn't miss any, you scan the likely candidate boxes again, counting the number that 
 * have an ID containing exactly two of any letter and then separately counting those with exactly three 
 * of any letter. You can multiply those two counts together to get a rudimentary checksum and compare 
 * it to what your device predicts.
 * <p>
 * For example, if you see the following box IDs:
 * <ul>
 *   <li>abcdef contains no letters that appear exactly two or three times.</li>
 *   <li>bababc contains two a and three b, so it counts for both.</li>
 *   <li>abbcde contains two b, but no letter appears exactly three times.</li>
 *   <li>abcccd contains three c, but no letter appears exactly two times.</li>
 *   <li>aabcdd contains two a and two d, but it only counts once.</li>
 *   <li>abcdee contains two e.</li>
 *   <li>ababab contains three a and three b, but it only counts once.</li>
 * </ul>
 * <p>
 * Of these box IDs, four of them contain a letter which appears exactly twice, 
 * and three of them contain a letter which appears exactly three times.
 * Multiplying these together produces a checksum of 4 * 3 = 12.
 * <p>
 * What is the checksum for your list of box IDs?
 * 
 * --- Part Two ---
 * Confident that your list of box IDs is complete, you're ready to find the boxes full of prototype fabric.
 * 
 * The boxes will have IDs which differ by exactly one character at the same position in both strings. 
 * For example, given the following box IDs:
 * abcde
 * fghij
 * klmno
 * pqrst
 * fguij
 * axcye
 * wvxyz
 * 
 * The IDs abcde and axcye are close, but they differ by two characters (the second and fourth). 
 * However, the IDs fghij and fguij differ by exactly one character, the third (h and u). 
 * Those must be the correct boxes.
 * 
 * What letters are common between the two correct box IDs? (In the example above, this is found by 
 * removing the differing character from either ID, producing fgij.)
 * 
 * @author bverde
 *
 */
public class Y2018D02 {
    // Return the number of chars whose num appearances match exactly
    public static int numCharsWithFrequency(Map<Character,Long> countsByChar, int frequency) {
        return (int) countsByChar.values().stream().filter(count -> count == frequency).count();
    }
    
    public static long checkSum(String[] inputs, int freq1, int freq2) {
        int numFreq1= 0, numFreq2 = 0;
        for (String s : inputs) {
            // Compute the number of appearances of each character
            Map<Character,Long> countsByChar = 
                    s
                    .chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            // Update the frequencies sums
            if (numCharsWithFrequency(countsByChar, freq1) > 0) {
                ++numFreq1;
            }
            
            if (numCharsWithFrequency(countsByChar, freq2) > 0) {
                ++numFreq2;
            };

            System.out.printf("%s: %d %d-count, %d %d-count\n", s, numFreq1, freq1, numFreq2, freq2);
        }

        // Compute the checksum
        return numFreq1 * numFreq2; 
    }
    
    public static String getPrototypicalChars(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < s1.length(); ++i) {
            if (s1.charAt(i) == s2.charAt(i)) {
                sb.append(s1.charAt(i));
            }
        }
        
        return (sb.length() == s1.length() -1) ? sb.toString() : null;  
    }
    
    public static boolean isPrototype(String s1, String s2) {
        return getPrototypicalChars(s1, s2) != null;
    }
    
    public static List<String> getPrototypes(String[] inputs) {
        List<String> protos = new LinkedList<String>();
        for (int i=0; i<inputs.length; ++i) {
            for (int j=i; j<inputs.length; ++j) {
                if (isPrototype(inputs[i], inputs[j])) {
                    protos.add(inputs[i]);
                    protos.add(inputs[j]);
                }
            }
        }
        
        return protos;
    }

    public static String getPrototypicalChars(String[] inputs) {
        List<String> protos = getPrototypes(inputs);
        return 
                (protos == null) || (protos.size() != 2)
                ? null
                : getPrototypicalChars(protos.get(0), protos.get(1));
    }
}
