package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D15 {
    public static class Disc {
        int numPositions;
        int posTimeSpec;
        int posNumSpec;

        public Disc(String[] parts) {
            this(AdventUtils.toInt(parts[0]), AdventUtils.toInt(parts[1]), AdventUtils.toInt(parts[2]));
        }

        public Disc(int numPositions, int posTimeSpec, int posNumSpec) {
            this.numPositions = numPositions;
            this.posTimeSpec = posTimeSpec;
            this.posNumSpec = posNumSpec;
        }
        
        public int getPosition(int time) {
            return (posNumSpec + time) % numPositions;
        }
        
        @Override 
        public String toString() {
            return String.format("[%s, %s, %s]", numPositions, posTimeSpec, posNumSpec);
        }
    }

    public static boolean clearsDiscs(List<String> inputs, int t) {
        List<Disc> discs = inputs.stream().map(s -> new Disc(s.split(","))).collect(Collectors.toList());

        int startPosition = 0;
        AtomicInteger time = new AtomicInteger(t);
        return discs.stream().allMatch(d -> d.getPosition(time.incrementAndGet()) == startPosition);
    }

    public static int getFirstClearedTime(List<String> inputs) {
        int i=0;
        while (!clearsDiscs(inputs, i)) {
            ++i;
        }

        return i;
    }
    
    @Test
    public void testGetPosition() {
        Disc d1 = new Disc(5, 0, 4);
        assertEquals("d1 position incorrect t=0", 4, d1.getPosition(0));
        assertEquals("d1 position incorrect t=1", 0, d1.getPosition(1));
        assertEquals("d1 position incorrect t=2", 1, d1.getPosition(2));
        assertEquals("d1 position incorrect t=3", 2, d1.getPosition(3));
        assertEquals("d1 position incorrect t=4", 3, d1.getPosition(4));
        assertEquals("d1 position incorrect t=5", 4, d1.getPosition(5));

        Disc d2 = new Disc(2, 0, 1);
        assertEquals("d2 position incorrect t=0", 1, d2.getPosition(0));
        assertEquals("d2 position incorrect t=1", 0, d2.getPosition(1));
        assertEquals("d2 position incorrect t=2", 1, d2.getPosition(2));
        assertEquals("d2 position incorrect t=3", 0, d2.getPosition(3));
    }

    @Test
    public void testClearsDiscs() {
        List<String> discs = Stream.of("5, 0, 4", "2, 0, 1").collect(Collectors.toList());
        assertEquals("clearsDisc t=0 incorrect", false, clearsDiscs(discs, 0));
        assertEquals("clearsDisc t=5 incorrect", true, clearsDiscs(discs, 5));
    }
    
    @Test
    public void testStarOne() {
        List<String> inputs = STAR_INPUTS;
    
        assertEquals("getStarOne() incorrect", 148737, getFirstClearedTime(inputs));
    }
    
    @Test
    public void testStarTwo() {
        List<String> inputs = STAR_INPUTS;
        inputs.add("11, 0, 0");
    
        assertEquals("getStarOne() incorrect", 2353212, getFirstClearedTime(inputs));
    }
    
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day15-inputs.txt");
}
