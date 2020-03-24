package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * --- Day 14: One-Time Pad ---
 * In order to communicate securely with Santa while you're on this mission, you've been using a one-time pad that you generate using a pre-agreed algorithm. 
 * Unfortunately, you've run out of keys in your one-time pad, and so you need to generate some more.
 * 
 * To generate keys, you first get a stream of random data by taking the MD5 of a pre-arranged salt (your puzzle input) 
 * and an increasing integer index (starting with 0, and represented in decimal); the resulting MD5 hash should be represented as a string of lowercase hexadecimal digits.
 * 
 * However, not all of these MD5 hashes are keys, and you need 64 new keys for your one-time pad. A hash is a key only if:
 * - It contains three of the same character in a row, like 777. Only consider the first such triplet in a hash.
 * - One of the next 1000 hashes in the stream contains that same character five times in a row, like 77777.
 * 
 * Considering future hashes for five-of-a-kind sequences does not cause those hashes to be skipped; 
 * instead, regardless of whether the current hash is a key, always resume testing for keys starting with the very next hash.
 * 
 * For example, if the pre-arranged salt is abc:
 * - The first index which produces a triple is 18, because the MD5 hash of abc18 contains ...cc38887a5.... However, index 18 does not count as a key for your one-time pad, because none of the next thousand hashes (index 19 through index 1018) contain 88888.
 * - The next index which produces a triple is 39; the hash of abc39 contains eee. It is also the first key: one of the next thousand hashes (the one at index 816) contains eeeee.
 * - None of the next six triples are keys, but the one after that, at index 92, is: it contains 999 and index 200 contains 99999.
 * - Eventually, index 22728 meets all of the criteria to generate the 64th key.
 * 
 * So, using our example salt of abc, index 22728 produces the 64th key.
 * 
 * Given the actual salt in your puzzle input, what index produces your 64th one-time pad key?
 * 
 * Your puzzle answer was 23769.
 * 
 * --- Part Two ---
 * Of course, in order to make this process even more secure, you've also implemented key stretching.
 * 
 * Key stretching forces attackers to spend more time generating hashes. Unfortunately, it forces everyone else to spend more time, too.
 * 
 * To implement key stretching, whenever you generate a hash, before you use it, you first find the MD5 hash of that hash, 
 * then the MD5 hash of that hash, and so on, a total of 2016 additional hashings. Always use lowercase hexadecimal representations of hashes.
 * 
 * For example, to find the stretched hash for index 0 and salt abc:
 * - Find the MD5 hash of abc0: 577571be4de9dcce85a041ba0410f29f.
 * - Then, find the MD5 hash of that hash: eec80a0c92dc8a0777c619d9bb51e910.
 * - Then, find the MD5 hash of that hash: 16062ce768787384c81fe17a7a60c7e3.
 * - ...repeat many times...
 * - Then, find the MD5 hash of that hash: a107ff634856bb300138cac6568c0f24.
 * 
 * So, the stretched hash for index 0 in this situation is a107ff.... 
 * In the end, you find the original hash (one use of MD5), then find the hash-of-the-previous-hash 2016 times, for a total of 2017 uses of MD5.
 * 
 * The rest of the process remains the same, but now the keys are entirely different. Again for salt abc:
 * - The first triple (222, at index 5) has no matching 22222 in the next thousand hashes.
 * - The second triple (eee, at index 10) hash a matching eeeee at index 89, and so it is the first key.
 * - Eventually, index 22551 produces the 64th key (triple fff with matching fffff at index 22859.
 * 
 * Given the actual salt in your puzzle input and using 2016 extra MD5 calls of key stretching, what index now produces your 64th one-time pad key?
 * 
 * Your puzzle answer was 20606.
 *  
 * @author bumpverde
 */
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
