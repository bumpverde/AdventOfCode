package com.verde.advent.y2016;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

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
