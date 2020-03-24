package com.verde.advent.y2019.d4;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Y2019D04 {
    public long numValidPasswords(int min, int max) {
        Predicate<String> hasTwoAdjacent = (s) -> {
            for (int i=0; i<s.length()-1; ++i) {
                if (s.charAt(i) == s.charAt(i+1)) {
                    return true;
                }
            } 
            return false;
        };

        Predicate<String> isNotDecreasing = (s) -> {
            for (int i=0; i<s.length()-1; ++i) {
                if (s.charAt(i) < s.charAt(i+1)) {
                    return false;
                }
            } 
            return true;
        };
        
        return 
                IntStream
                .rangeClosed(min, max)
                .mapToObj(i -> String.valueOf(i))
                .filter(s -> s.length() == 6)
                .filter(hasTwoAdjacent)
                .filter(isNotDecreasing)
                .count();
    }
}
