package com.verde.advent;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Basic operations on Sets of objects, like union(), intersection(), and difference().
 * 
 * @author bverde
 */
public class SetUtils {
    /**
     * Return the union of two sets.
     * 
     * @param a Set A
     * @param b Set B
     * @return a Set that is the union of a and b - the Set of all elements in either A or B.
     */
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> res = new LinkedHashSet<T>(a);
        for (T element : b) {
            res.add(element);
        }
        
        return res;
    }

    /**
     * Return the intersection of two sets.
     * 
     * @param a Set A
     * @param b Set B
     * @return a Set that is the intersection of a and b - the Set of all elements in both A and B
     */
    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> res = new LinkedHashSet<T>();
        for (T element : a) {
            if (b.contains(element)) {
                res.add(element);
            }
        }
        
        return res;
    }

    /**
     * Return the difference of two sets.
     * 
     * @param a Set A
     * @param b Set B
     * @return the Set that is the difference of a and b, which is the set of all elements in A that are not in B
     */
    public static <T> Set<T> difference(Set<T> a, Set<T> b) {
        Set<T> res = new LinkedHashSet<T>(a);
        for (T element : b) {
            res.remove(element);
        }
        
        return res;
    }
    
    /**
     * Return the first element in a {@link Collection}, or null if the Collection is empty.
     * 
     * @param collection the {@link Collection}; assumed not null
     * @return the first element in a {@link Collection}, or null if the Collection is empty
     */
    public static <T> T getFirst(Collection<T> collection) {
        return collection.stream().findFirst().orElse(null);
    }
    
    /**
     * Sort a {@link Set} given a {@link Comparator}.
     * 
     * @param set the {@link Set} to sort
     * @param sorter a {@link Comparator} 
     * @return the sorted {@link Set}
     */
    public static <T> Set<T> sort(Set<T> set, Comparator<? super T> sorter) {
        Set<T> sorted = new LinkedHashSet<T>();
        set.stream().sorted(sorter).forEach(s -> sorted.add(s));
        return sorted;
    }
}
