package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 7: Internet Protocol Version 7 ---
 * While snooping around the local network of EBHQ, you compile a list of IP addresses (they're IPv7, of course; IPv6 is much too limited). You'd like to figure out which IPs support TLS (transport-layer snooping).
 * 
 * An IP supports TLS if it has an Autonomous Bridge Bypass Annotation, or ABBA. An ABBA is any four-character sequence which consists of a pair of two different characters followed by the reverse of that pair, such as xyyx or abba. However, the IP also must not have an ABBA within any hypernet sequences, which are contained by square brackets.
 * 
 * For example:
 * 
 * abba[mnop]qrst supports TLS (abba outside square brackets).
 * abcd[bddb]xyyx does not support TLS (bddb is within square brackets, even though xyyx is outside square brackets).
 * aaaa[qwer]tyui does not support TLS (aaaa is invalid; the interior characters must be different).
 * ioxxoj[asdfgh]zxcvbn supports TLS (oxxo is outside square brackets, even though it's within a larger string).
 * How many IPs in your puzzle input support TLS?
 * 
 * Your puzzle answer was 105.
 * 
 * --- Part Two ---
 * You would also like to know which IPs support SSL (super-secret listening).
 * 
 * An IP supports SSL if it has an Area-Broadcast Accessor, or ABA, anywhere in the supernet sequences (outside any square bracketed sections), and a corresponding Byte Allocation Block, or BAB, anywhere in the hypernet sequences. An ABA is any three-character sequence which consists of the same character twice with a different character between them, such as xyx or aba. A corresponding BAB is the same characters but in reversed positions: yxy and bab, respectively.
 * 
 * For example:
 * 
 * aba[bab]xyz supports SSL (aba outside square brackets with corresponding bab within square brackets).
 * xyx[xyx]xyx does not support SSL (xyx, but no corresponding yxy).
 * aaa[kek]eke supports SSL (eke in supernet with corresponding kek in hypernet; the aaa sequence is not related, because the interior character must be different).
 * zazbz[bzb]cdb supports SSL (zaz has no corresponding aza, but zbz has a corresponding bzb, even though zaz and zbz overlap).
 * How many IPs in your puzzle input support SSL?
 * 
 * Your puzzle answer was 258.
 *  
 * @author bumpverde
 */
public class Y2016D07 {
    public static final Collection<String> EMPTY_LIST = new LinkedList<String>();
    
    public static class Block {
        public String address;
        public boolean isHypernet;
        public Collection<String> abbas;
        
        public Block(String address, boolean isHypernet) {
            this.address = address;
            this.isHypernet = isHypernet;
            this.abbas = AdventUtils.genSubStrings(address, 4).stream().filter(Block::isABBA).collect(Collectors.toList());
        }
      
        public Collection<String> getABBAs() {
            return abbas;
        }
        
        public static boolean isABBA(String s) {
            if ((s.length() != 4) || (s.charAt(0) == s.charAt(1)) ) {
                return false;
            }
            
            String p1 = s.substring(0, 2);
            String s2 = p1.concat(StringUtils.reverse(p1));
            return s.equals(s2);
        }

        public Collection<String> getABAs() {
            return AdventUtils.genSubStrings(address, 3).stream().filter(Block::isABA).collect(Collectors.toList());
        }
        
        public static boolean isABA(String s) {
            return (s.length() == 3) 
                    && (s.charAt(0) == s.charAt(2)) 
                    && (s.charAt(0) != s.charAt(1));
        }

        public static boolean isBAB(String bab, String aba) {
            return isABA(bab) 
                    && isABA(aba)
                    && (bab.charAt(0) == aba.charAt(1)) 
                    && (bab.charAt(1) == aba.charAt(0));
        }

        public static String getBAB(String aba) {
            if (! isABA(aba)) {
                return null;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(aba.charAt(1)).append(aba.charAt(0)).append(aba.charAt(1));
            return sb.toString();
        }
    }
    
    public static Collection<Block> parseBlocks(String s) {
        Collection<Block> blocks = new LinkedList<Block>();
        
        StringBuilder sb = new StringBuilder();
        boolean isHypernet = false;
        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);

            if (c == '[') {
                blocks.add(new Block(sb.toString(), false));
                sb = new StringBuilder();
                isHypernet = true;
            } else if (c == ']') {
                blocks.add(new Block(sb.toString(), true));
                sb = new StringBuilder();
                isHypernet = false;
            } else {
                sb.append(c);
            }
        }
        
        // Don't forget any last piece
        if (sb.length() > 0) {
            blocks.add(new Block(sb.toString(), isHypernet));
        }
        
        return blocks;
    }

    
    public static boolean supportsTLS(String spec) {
        Collection<Block> blocks = parseBlocks(spec);

        // Determine if there are any ABBAs in hypernets (that will blow it) 
        Collection<Block> hypernetBlocksWithABBAs = blocks.stream().filter(b -> b.isHypernet && !b.getABBAs().isEmpty()).collect(Collectors.toList());

        // Determine if there are any ABBAs in non-hypernets (that is required) 
        Collection<Block> nonHypernetBlocksWithABBAs = blocks.stream().filter(b -> !b.isHypernet && !b.getABBAs().isEmpty()).collect(Collectors.toList());
        
        return hypernetBlocksWithABBAs.isEmpty() && !nonHypernetBlocksWithABBAs.isEmpty();
    }
    
    public static boolean supportsSSL(String spec) {
        Collection<Block> blocks = parseBlocks(spec);

        // Determine all of the ABAs outside of hypernets
        Set<String> ABAs = blocks.stream().filter(b -> !b.isHypernet).map(b -> b.getABAs()).flatMap(Collection::stream).collect(Collectors.toSet());

        // Determine all of the potential BABs inside of hypernets (the potentials are just ABAs)
        Set<String> BABs = blocks.stream().filter(b -> b.isHypernet).map(b -> b.getABAs()).flatMap(Collection::stream).collect(Collectors.toSet());

        // The block supports SSL if there are any ABAs that have a corresponding BAB 
        return ABAs.stream().anyMatch(aba -> BABs.contains(Block.getBAB(aba)));
    }
    
    @Test
    public void testIsAbba() {
        String[] inputs = { "abba", "mnop", "bddb", "ioxxoj" };
        boolean[] expected = { true, false, true, true };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("isAbba( " + inputs[i] + " ) incorrect", expected[i], Block.isABBA(inputs[i]));
        }
    }    

    @Test
    public void testIsAba() {
        String[] inputs = { "aba", "bab", "xyx", "yxy", "a", "ab", "abc", "xyxz" };
        boolean[] expected = { true, true, true, true, false, false, false, false};
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("isABA( " + inputs[i] + " ) incorrect", expected[i], Block.isABA(inputs[i]));
        }
    }    
    @Test
    public void testGetBAB() {
        String[] inputs = { "aba", "bab", "xyx", "yxy", "a", "ab", "abc", "xyxz" };
        String[] expected = { "bab", "aba", "yxy", "xyx", null, null, null, null };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getBAB( " + inputs[i] + " ) incorrect", expected[i], Block.getBAB(inputs[i]));
        }
    }    

    @Test
    public void testSupportTLS() {
//        STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
        
        String[] inputs = {
                "abba[mnop]qrst",
                "abcd[bddb]xyyx",
                "aaaa[qwer]tyui",
                "ioxxoj[asdfgh]zxcvbn"
        };
        
        boolean[] expected = { true, false, false, true };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("supportsTLS(" + inputs[i] + ") incorrect", expected[i], supportsTLS(inputs[i]));
        }
    }    
    
    @Test
    public void testSupportSSL() {
        String[] inputs = {
                "aba[bab]xyz",
                "xyx[xyx]xyx",
                "aaa[kek]eke",
                "zazbz[bzb]cdb"
        };
        
        boolean[] expected = { true, false, true, true };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("supportsSSL(" + inputs[i] + ") incorrect", expected[i], supportsSSL(inputs[i]));
        }
    }    
    @Test
    public void testStarOne() {
        long numTLS = STAR_INPUTS.stream().filter(s -> supportsTLS(s)).count();
        System.out.printf("Number of TLS supported addresses for star one: %d\n", numTLS);
        assertEquals("TLS computation is incorrect", 105, numTLS);
    }
    
    @Test
    public void testStarTwo() {
        long numSSL = STAR_INPUTS.stream().filter(s -> supportsSSL(s)).count();
        System.out.printf("Number of SSL supported addresses for star one: %d\n", numSSL);
        assertEquals("SSL computation is incorrect", 258, numSSL);
    }
    
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day7-inputs.txt");

}
