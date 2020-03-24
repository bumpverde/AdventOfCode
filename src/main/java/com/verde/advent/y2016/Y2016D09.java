package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D09 {
    private static class DecompressedMarker {
        public int count;
        public String text;
        
        public DecompressedMarker(String text, int count) {
            this.text = text;
            this.count = count;
        }
    }
    
    public static String decompress(String s, boolean decompressInnerMarkers) {
        StringBuilder sb = new StringBuilder();
        
        for (AtomicInteger i = new AtomicInteger(0); i.get() < s.length(); ) {
            char c = s.charAt(i.get());
            
            if (c == '(') {
                DecompressedMarker dm = decompressMarker(s, i, decompressInnerMarkers);
                for (int count=0; count<dm.count; ++count) {
                    sb.append(dm.text);
                }
            } else {
                if (!Character.isWhitespace(c)) {
                    sb.append(c);
                }
                
                i.incrementAndGet();
            }
        }
        
        return sb.toString();
    }

    public static long decompressedLength(String s, boolean decompressInnerMarkers) {
        long len = 0;
        for (AtomicInteger i = new AtomicInteger(0); i.get() < s.length(); ) {
            char c = s.charAt(i.get());
            
            if (c == '(') {
                len += decompressedMarkerLength(s, i, decompressInnerMarkers);
            } else {
                if (!Character.isWhitespace(c)) {
                    ++len;
                }
                
                i.incrementAndGet();
            }
        }
        
        return len;
    }

    private static DecompressedMarker decompressMarker(String s, AtomicInteger index, boolean decompressInnerMarkers) {
        int i = index.get();
        int end = s.indexOf(')', i);
        if (end == -1) {
            throw new IllegalStateException("missing ) starting at index " + i);
        }
        
        String spec = s.substring(i + 1, end);
        String[] parts = spec.split("x");
        if (parts.length != 2) {
            throw new IllegalStateException("incorrect compression spec: " + spec);
        }
        
        // Process the marker
        i = end + 1;
        int len = AdventUtils.toInt(parts[0]);
        int count = AdventUtils.toInt(parts[1]);
        String copy = s.substring(i, i+len);
    
        // Move past the marker
        i += len;
        index.set(i);
        
        // If we're on another marker and we're decompressing them, then recurse
        if (!copy.isEmpty() && (copy.charAt(0) == '(') && decompressInnerMarkers) {
          String decompressed = decompress(copy, decompressInnerMarkers);
          return new DecompressedMarker(decompressed, count);
        } else {
            return new DecompressedMarker(copy, count);
        }
    }
    
    
    private static long decompressedMarkerLength(String s, AtomicInteger index, boolean decompressInnerMarkers) {
        int i = index.get();
        int end = s.indexOf(')', i);
        if (end == -1) {
            throw new IllegalStateException("missing ) starting at index " + i);
        }
        
        String spec = s.substring(i + 1, end);
        String[] parts = spec.split("x");
        if (parts.length != 2) {
            throw new IllegalStateException("incorrect compression spec: " + spec);
        }
        
        // Process the marker
        i = end + 1;
        int len = AdventUtils.toInt(parts[0]);
        int count = AdventUtils.toInt(parts[1]);
        String copy = s.substring(i, i+len);
    
        // Move past the marker
        i += len;
        index.set(i);
        
        // If we're on another marker and we're decompressing them, then recurse
        if (!copy.isEmpty() && (copy.charAt(0) == '(') && decompressInnerMarkers) {
          return decompressedLength(copy, decompressInnerMarkers) * count;
        } else {
            return copy.length() * count;
        }
    }
    
    
    @Test
    public void testDecompressOne() {
        String[] inputs = {
                "ADVENT",
                "A(1x5)BC",
                "(3x3)XYZ",
                "A(2x2)BCD(2x2)EFG",
                "(6x1)(1x3)A",
                "X(8x2)(3x3)ABCY",
        };
        String[] expected = {
                "ADVENT",
                "ABBBBBC",
                "XYZXYZXYZ",
                "ABCBCDEFEFG",
                "(1x3)A",
                "X(3x3)ABC(3x3)ABCY",
        };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("decompress incorrect", expected[i], decompress(inputs[i], false));
        }
    }
    
    @Test
    public void testDecompressTwo() {
        String[] inputs = {
                "X(8x2)(3x3)ABCY",
                "ADVENT",
                "A(1x5)BC",
                "(3x3)XYZ",
                "A(2x2)BCD(2x2)EFG",
                "(6x1)(1x3)A",
        };
        String[] expected = {
                "XABCABCABCABCABCABCY",
                "ADVENT",
                "ABBBBBC",
                "XYZXYZXYZ",
                "ABCBCDEFEFG",
                "AAA",
        };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("decompress incorrect", expected[i], decompress(inputs[i], true));
        }

        String longSpec = "(27x12)(20x12)(13x14)(7x10)(1x12)A";
        String actual = decompress(longSpec, true);
        assertEquals("longSpec1 length incorrect", 241920, actual.length());
        assertEquals("longSpec1 compressedLength() incorrect", 241920, decompressedLength(longSpec, true)); 
        
        longSpec = "(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN";
        actual = decompress(longSpec, true);
        System.out.println(actual);
        assertEquals("longSpec2 length incorrect", 445, actual.length());
    }
    
    @Test
    public void testStarOne() {
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        String actual = decompress(inputs[0], false);
        System.out.printf("star one decompressed length: %d\n", actual.length());
        assertEquals("star one decompress() is incorrect", 102239, actual.length());
    }

    @Test
    public void testStarTwo() {
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        long actual = decompressedLength(inputs[0], true);
        System.out.printf("star two decompressed length: %d\n", actual);
        assertEquals("star two decompressedLength() is incorrect", 10780403063L, actual);
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day9-inputs.txt");
}
    
