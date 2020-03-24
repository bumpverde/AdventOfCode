package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 12: Leonardo's Monorail ---
 * You finally reach the top floor of this building: a garden with a slanted glass ceiling. Looks like there are no more stars to be had.
 * 
 * While sitting on a nearby bench amidst some tiger lilies, you manage to decrypt some of the files you extracted from the servers downstairs.
 * 
 * According to these documents, Easter Bunny HQ isn't just this building - it's a collection of buildings in the nearby area. 
 * They're all connected by a local monorail, and there's another building not far from here! Unfortunately, being night, the monorail is currently not operating.
 * 
 * You remotely connect to the monorail control systems and discover that the boot sequence expects a password. 
 * The password-checking logic (your puzzle input) is easy to extract, but the code it uses is strange: 
 * it's assembunny code designed for the new computer you just assembled. You'll have to execute the code and get the password.
 * 
 * The assembunny code you've extracted operates on four registers (a, b, c, and d) that start at 0 and can hold any integer. 
 * However, it seems to make use of only a few instructions:
 * 
 * - cpy x y copies x (either an integer or the value of a register) into register y.
 * - inc x increases the value of register x by one.
 * - dec x decreases the value of register x by one.
 * - jnz x y jumps to an instruction y away (positive means forward; negative means backward), but only if x is not zero.
 * 
 * The jnz instruction moves relative to itself: an offset of -1 would continue at the previous instruction, 
 * while an offset of 2 would skip over the next instruction. For example:
 * 
 * cpy 41 a
 * inc a
 * inc a
 * dec a
 * jnz a 2
 * dec a
 * 
 * The above code would set register a to 41, increase its value by 2, decrease its value by 1, and then skip the last dec a 
 * (because a is not zero, so the jnz a 2 skips it), leaving register a at 42. When you move past the last instruction, the program halts.
 * 
 * After executing the assembunny code in your puzzle input, what value is left in register a?
 * 
 * Your puzzle answer was 318020.
 * 
 * --- Part Two ---
 * As you head down the fire escape to the monorail, you notice it didn't start; register c needs to be initialized to the position of the ignition key.
 * 
 * If you instead initialize register c to be 1, what value is now left in register a?
 * 
 * Your puzzle answer was 9227674.
 * 
 * @author bumpverde
 */
public class Y2016D12 {
    public static int getValue(Map<String,Integer> registers, String ref) {
        if (registers.containsKey(ref)) {
            return registers.get(ref);
        }
        
        // It's not a known register, so treat it as a literal int
        int value = 0;
        try {
            value = Integer.parseInt(ref);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        return value;
    }

    public static void adjustRegister(Map<String,Integer> registers, String ref, int delta) {
        Integer val = registers.get(ref);
        if (val == null) {
           val = new Integer(0); 
        }
        
        val += delta;
        registers.put(ref,  val);
    }

    public static String getRegister(String ref) {
        switch (ref) {
        case "a":
        case "b":
        case "c":
        case "d":
            return ref;
            
        default:
            System.out.printf("Unknown register reference '%s'\n", ref);
            return ref;
        }
    }
    
    public static Map<String,Integer> process(String[] instructions, int cInitVal) {
        // Set up the registers
        Map<String,Integer> registers = new HashMap<String,Integer>();
        registers.put("a", 0);
        registers.put("b", 0);
        registers.put("c", cInitVal);
        registers.put("d", 0);

        int instPtr = 0;
        while (instPtr < instructions.length) {
            String instruction = instructions[instPtr];
            String[] parts = StringUtils.split(instruction, " ");
            
            switch (parts[0]) {
            case "cpy":
                registers.put(getRegister(parts[2]), getValue(registers, parts[1])); 
                ++instPtr;
                break;

            case "inc":
                adjustRegister(registers, getRegister(parts[1]), 1);
                ++instPtr;
                break;
            
            case "dec":
                adjustRegister(registers, getRegister(parts[1]), -1);
                ++instPtr;
                break;
            
            case "jnz":     // jnz a 2
                if (getValue(registers, parts[1]) != 0) {
                    instPtr += AdventUtils.toInt(parts[2]);
                } else {
                    ++instPtr;
                }
                break;

            default:
                ++instPtr;
                break; 
            }
        }
        
        return registers;
    }
    
    @Test
    public void testProcess() {
        String[][] inputs = {
                {
                    "cpy 41 a",
                    "inc a",
                    "inc a",
                    "dec a",
                    "jnz a 2",
                    "dec a",
                },
                STAR_INPUTS,
                STAR_INPUTS,
        };
        int[] cRegInitVal = { 0, 0, 1 };
        int[] expected = { 42, 318020, 9227674 };
        for (int i=0; i<inputs.length; ++i) {
            Map<String,Integer> registers = process(inputs[i], cRegInitVal[i]);
            assertEquals("process() incorrect", expected[i], getValue(registers, "a"));
        }
    }

    public static final String[] STAR_INPUTS = {
            "cpy 1 a",
            "cpy 1 b",
            "cpy 26 d",
            "jnz c 2",
            "jnz 1 5",
            "cpy 7 c",
            "inc d",
            "dec c",
            "jnz c -2",
            "cpy a c",
            "inc a",
            "dec b",
            "jnz b -2",
            "cpy c b",
            "dec d",
            "jnz d -6",
            "cpy 19 c",
            "cpy 11 d",
            "inc a",
            "dec d",
            "jnz d -2",
            "dec c",
            "jnz c -5",
    };
}
