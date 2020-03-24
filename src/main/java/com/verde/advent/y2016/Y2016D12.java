package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;


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
