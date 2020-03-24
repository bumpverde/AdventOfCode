package com.verde.advent.y2016;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 5: How About a Nice Game of Chess? ---
 * You are faced with a security door designed by Easter Bunny engineers that seem to have acquired most of their security knowledge by watching hacking movies.
 * 
 * The eight-character password for the door is generated one character at a time by finding the MD5 hash of some Door ID (your puzzle input) and an increasing integer index (starting with 0).
 * 
 * A hash indicates the next character in the password if its hexadecimal representation starts with five zeroes. If it does, the sixth character in the hash is the next character of the password.
 * 
 * For example, if the Door ID is abc:
 * 
 * The first index which produces a hash that starts with five zeroes is 3231929, which we find by hashing abc3231929; the sixth character of the hash, and thus the first character of the password, is 1.
 * 5017308 produces the next interesting hash, which starts with 000008f82..., so the second character of the password is 8.
 * The third time a hash starts with five zeroes is for abc5278568, discovering the character f.
 * In this example, after continuing this search a total of eight times, the password is 18f47a30.
 * 
 * Given the actual Door ID, what is the password?
 * 
 * Your puzzle answer was 2414bc77.
 * 
 * --- Part Two ---
 * As the door slides open, you are presented with a second door that uses a slightly more inspired security mechanism. Clearly unimpressed by the last version (in what movie is the password decrypted in order?!), the Easter Bunny engineers have worked out a better solution.
 * 
 * Instead of simply filling in the password from left to right, the hash now also indicates the position within the password to fill. You still look for hashes that begin with five zeroes; however, now, the sixth character represents the position (0-7), and the seventh character is the character to put in that position.
 * 
 * A hash result of 000001f means that f is the second character in the password. Use only the first result for each position, and ignore invalid positions.
 * 
 * For example, if the Door ID is abc:
 * 
 * The first interesting hash is from abc3231929, which produces 0000015...; so, 5 goes in position 1: _5______.
 * In the previous method, 5017308 produced an interesting hash; however, it is ignored, because it specifies an invalid position (8).
 * The second interesting hash is at index 5357525, which produces 000004e...; so, e goes in position 4: _5__e___.
 * You almost choke on your popcorn as the final character falls into place, producing the password 05ace8e3.
 * 
 * Given the actual Door ID and this new method, what is the password? Be extra proud of your solution if it uses a cinematic "decrypting" animation.
 * 
 * Your puzzle answer was 437e60fc.
 *  
 * @author bumpverde
 */
public class Y2016D05 {
    public String getPassword(String input) {
        int index = 0;
        String password = "";

        while (password.length() < 8) {
            String message = String.format("%s%d", input, index); 
            
            String md5Hex = DigestUtils.md5Hex(message).toLowerCase();
            if (md5Hex.startsWith("00000")) {
                password += AdventUtils.toString(md5Hex.charAt(5));
                System.out.printf("\n%d: %s %s\n", index, md5Hex, password);
            }
            
            ++index;
            if ((index % 100000) == 0) {
                System.out.print(".");
            }
        }

        return password;
    }

    
    public String getPasswordInfilled(String input) {
        int index = 0;
        String[] password = { "_", "_", "_", "_", "_", "_", "_", "_" };

        Supplier<Long> numSet = () -> Arrays.stream(password).filter(s -> !s.equals("_")).count();
        Supplier<String> printer = () -> StringUtils.join(password, "");
        
        while (numSet.get() < 8) {
            String message = String.format("%s%d", input, index); 
            
            String md5Hex = DigestUtils.md5Hex(message).toLowerCase();
            if (md5Hex.startsWith("00000")) {
                char c5 = md5Hex.charAt(5);
                char c6 = md5Hex.charAt(6);
                
                if ((c5 >= '0') && (c5 < '8')) {
                    int pos = Integer.parseInt(AdventUtils.toString(c5));
                    
                    if ("_".equals(password[pos])) {
                        password[pos] = AdventUtils.toString(c6);
                        System.out.printf("\n%d: %s %s\n", index, md5Hex, printer.get());
                    }
                }
            }
            
            ++index;
            if ((index % 100000) == 0) {
                System.out.print(".");
            }
        }

        return printer.get();
    }
    
    @Test
    public void testPassword() {
        String[] inputs = {
                "wtnhxymk",
                "abc",
        };
        
        String[] expected = { "", "18f47a30" };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getPassword() incorrect", expected[i], getPassword(inputs[i]));
        }
    }

    @Test
    public void testPasswordInfilled() {
        String[] inputs = {
                "wtnhxymk",
                "abc",
        };
        
        String[] expected = { "437e60fc", "05ace8e3" };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getPassword() incorrect", expected[i], getPasswordInfilled(inputs[i]));
        }
    }
}
