package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class Y2016D14 {
    public static class HashGenerator implements Supplier<String> {
        String seed;
        int index;
        boolean stretched;
        static Map<String,String> cache = new HashMap<String,String>(); // shared
        
        public HashGenerator(String seed, int index, boolean stretched) {
            this.seed = seed;
            this.index = index;
            this.stretched = stretched;
        }
        
        @Override
        public String get() {
            String message = String.format("%s%d", seed, index++);

            String key = String.format("%s-%b", message, stretched);
            String md5Hex = cache.get(key);
            if (md5Hex != null) {
                return md5Hex;
            }
            
            md5Hex = DigestUtils.md5Hex(message).toLowerCase();
            
            if (stretched) {
                for (int i=0; i<2016; ++i) {
                    md5Hex = DigestUtils.md5Hex(md5Hex).toLowerCase();
                }
            }
            
            cache.put(key, md5Hex);
            
            return md5Hex;
        }
    }
    
    public static List<Integer> getKeyIndices(String seed, int count, boolean stretched) {
        HashGenerator hashGen1 = new HashGenerator(seed, 0, stretched);
        List<Integer> indices = new LinkedList<Integer>();
        
        while (indices.size() < count) {
            String hash = hashGen1.get();
            int hash1Index = hashGen1.index - 1; 
            
            // Look for 3-digit repeats
            boolean hashIsKey = false;
            boolean seenTriplet = false;
            for (int i=0; !hashIsKey && !seenTriplet && i<hash.length()-2; ++i) {
                char c = hash.charAt(i);
                if ((c == hash.charAt(i+1)) && (c == hash.charAt(i+2))) {
                    // Only process the first triplet - this tripped me up!
                    seenTriplet = true;
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(c).append(c).append(c).append(c).append(c);
                    String match = sb.toString();
                    
                    // Check the next 1000 hashes for the 5-digit repeats (using a different hasher)
                    HashGenerator hashGen2 = new HashGenerator(seed, hashGen1.index, stretched);
                    for (int h=0; !hashIsKey && h<1000; ++h) {
                        String hash2 = hashGen2.get();
                        int hash2Index = hashGen2.index - 1;
                        
                        if (hash2.contains(match)) {
                            hashIsKey = true;
                            indices.add(hash1Index);
                            System.out.printf("Discovered key #%d %s at index %d (hash2 index %d (%s) contains %s, distance=%d)\n", 
                                    indices.size()-1, hash, hash1Index, hash2Index, hash2, match, hash2Index-hash1Index);
                        }
                    }
                }
            }
        }
        
        return indices;
    }
    
    @Test
    public void testGenKeyIndices() {
        List<Integer> indices = getKeyIndices("abc", 64, false);
        assertEquals("index[0] is incorrect", 39, (int) indices.get(0));
        assertEquals("index[1] is incorrect", 92, (int) indices.get(1));
        assertEquals("index[63] is incorrect", 22728, (int) indices.get(63));
    }

    @Test
    public void testGenKeyIndicesStretched() {
        List<Integer> indices = getKeyIndices("abc", 64, true);
        assertEquals("index[0] is incorrect", 10, (int) indices.get(0));
        assertEquals("index[63] is incorrect", 22551, (int) indices.get(63));
    }

    @Test
    public void testSaltOne() {
        List<Integer> indices = getKeyIndices("cuanljph", 64, false);
        assertEquals("index[63] is incorrect", 23769, (int) indices.get(63));
    }

    @Test
    public void testSaltTwo() {
        List<Integer> indices = getKeyIndices("cuanljph", 64, true);
        assertEquals("index[63] is incorrect", 20606, (int) indices.get(63));
    }
}
