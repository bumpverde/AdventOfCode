package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.verde.advent.AdventUtils;

import javaslang.Tuple2;

/**
 * --- Day 6: Signals and Noise ---
 * Something is jamming your communications with Santa. Fortunately, your signal is only partially jammed, 
 * and protocol in situations like this is to switch to a simple repetition code to get the message through.
 * 
 * In this model, the same message is sent repeatedly. You've recorded the repeating message signal (your puzzle input),
 * but the data seems quite corrupted - almost too badly to recover. Almost.
 * 
 * All you need to do is figure out which character is most frequent for each position. For example, suppose you had recorded the following messages:
 * 
 * eedadn
 * drvtee
 * eandsr
 * raavrd
 * atevrs
 * tsrnev
 * sdttsa
 * rasrtv
 * nssdts
 * ntnada
 * svetve
 * tesnvt
 * vntsnd
 * vrdear
 * dvrsen
 * enarar
 * 
 * The most common character in the first column is e; in the second, a; in the third, s, and so on. 
 * Combining these characters returns the error-corrected message, easter.
 * 
 * Given the recording in your puzzle input, what is the error-corrected version of the message being sent?
 * 
 * Your puzzle answer was mshjnduc.
 * 
 * --- Part Two ---
 * Of course, that would be the message - if you hadn't agreed to use a modified repetition code instead.
 * 
 * In this modified code, the sender instead transmits what looks like random data, but for each character, 
 * the character they actually want to send is slightly less likely than the others. Even after signal-jamming noise, 
 * you can look at the letter distributions in each column and choose the least common letter to reconstruct the original message.
 * 
 * In the above example, the least common character in the first column is a; in the second, d, and so on. 
 * Repeating this process for the remaining characters produces the original message, advent.
 * 
 * Given the recording in your puzzle input and this new decoding methodology, what is the original message that Santa is trying to send?
 * 
 * Your puzzle answer was apfeeebz.
 * 
 * @author bumpverde
 */
public class Y2016D06 {
    
    public static String getDecoded(String[] inputs, boolean mostFrequent) {
        // Explode a String into Tuple2's, encoding the position and character at the position
        Function<String,Collection<Tuple2<Integer,Character>>> explode = (s) -> {
            Collection<Tuple2<Integer,Character>> charPosTuples = new LinkedList<Tuple2<Integer,Character>>();
            
            for (int i=0; i<s.length(); ++i) {
                charPosTuples.add(new Tuple2<Integer,Character>(i, s.charAt(i)));
            }
            
            return charPosTuples;
        };
        
        // Compute the character frequencies on a per-column basis
        Map<Integer, Map<Character, Long>> countsByCharacterByCol = 
                Arrays
                .stream(inputs)
                .map(s -> explode.apply(s))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(t -> t._1(),
                            Collectors.groupingBy(t -> t._2(), Collectors.counting())));
                        
        // To build the result
        StringBuilder sb = new StringBuilder();

        // A sorter that will do descending if looking for most frequent, else ascending
        Comparator<Long> sorter = (v1, v2) -> mostFrequent ? Long.compare(v2, v1) : Long.compare(v1, v2);
        
        // Sort the results to get the highest (or lowest) frequency ones
        countsByCharacterByCol.entrySet().stream().sorted((e1, e2) -> Integer.compare(e1.getKey(), e2.getKey())).forEach(entry -> {
            int column = entry.getKey();
            Character selected =
                    entry.getValue().entrySet()
                    .stream()
                    .sorted((e1, e2) -> sorter.compare(e1.getValue(), e2.getValue()))
                    .findFirst().map(e -> e.getKey()).orElse(null);
            
            System.out.printf("%d=%c\n", column, selected);
            sb.append(selected);
        });
        
        // Return what has been built
        return sb.toString();
    }
    
    @Test
    public void testGetDecoded() {
        String[][] inputs = {
                {
                    "eedadn",
                    "drvtee",
                    "eandsr",
                    "raavrd",
                    "atevrs",
                    "tsrnev",
                    "sdttsa",
                    "rasrtv",
                    "nssdts",
                    "ntnada",
                    "svetve",
                    "tesnvt",
                    "vntsnd",
                    "vrdear",
                    "dvrsen",
                    "enarar",
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
                {
                    "eedadn",
                    "drvtee",
                    "eandsr",
                    "raavrd",
                    "atevrs",
                    "tsrnev",
                    "sdttsa",
                    "rasrtv",
                    "nssdts",
                    "ntnada",
                    "svetve",
                    "tesnvt",
                    "vntsnd",
                    "vrdear",
                    "dvrsen",
                    "enarar",
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
        };
        
        boolean[] mostFrequent = { true, true, false, false };
        String[] expected = { "easter", "mshjnduc", "advent", "apfeeebz" };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getDecoded() incorrect", expected[i], getDecoded(inputs[i], mostFrequent[i]));
        }
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day6-inputs.txt");
}
