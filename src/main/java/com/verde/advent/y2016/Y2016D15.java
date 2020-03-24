package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 15: Timing is Everything ---
 * The halls open into an interior plaza containing a large kinetic sculpture. The sculpture is in a sealed enclosure and seems to 
 * involve a set of identical spherical capsules that are carried to the top and allowed to bounce through the maze of spinning pieces.
 * 
 * Part of the sculpture is even interactive! When a button is pressed, a capsule is dropped and tries to fall through slots in a set 
 * of rotating discs to finally go through a little hole at the bottom and come out of the sculpture. If any of the slots aren't aligned 
 * with the capsule as it passes, the capsule bounces off the disc and soars away. You feel compelled to get one of those capsules.
 * 
 * The discs pause their motion each second and come in different sizes; they seem to each have a fixed number of positions at which they stop. 
 * You decide to call the position with the slot 0, and count up for each position it reaches next.
 * 
 * Furthermore, the discs are spaced out so that after you push the button, one second elapses before the first disc is reached, 
 * and one second elapses as the capsule passes from one disc to the one below it. So, if you push the button at time=100, 
 * then the capsule reaches the top disc at time=101, the second disc at time=102, the third disc at time=103, and so on.
 * 
 * The button will only drop a capsule at an integer time - no fractional seconds allowed.
 * 
 * For example, at time=0, suppose you see the following arrangement:
 * - Disc #1 has 5 positions; at time=0, it is at position 4.
 * - Disc #2 has 2 positions; at time=0, it is at position 1.
 * 
 * If you press the button exactly at time=0, the capsule would start to fall; it would reach the first disc at time=1. 
 * Since the first disc was at position 4 at time=0, by time=1 it has ticked one position forward. As a five-position disc, 
 * the next position is 0, and the capsule falls through the slot.
 * 
 * Then, at time=2, the capsule reaches the second disc. The second disc has ticked forward two positions at this point: 
 * it started at position 1, then continued to position 0, and finally ended up at position 1 again. Because there's 
 * only a slot at position 0, the capsule bounces away.
 * 
 * If, however, you wait until time=5 to push the button, then when the capsule reaches each disc, the first disc will have ticked 
 * forward 5+1 = 6 times (to position 0), and the second disc will have ticked forward 5+2 = 7 times (also to position 0). 
 * In this case, the capsule would fall through the discs and come out of the machine.
 * 
 * However, your situation has more than two discs; you've noted their positions in your puzzle input.
 * What is the first time you can press the button to get a capsule?
 * 
 * Your puzzle answer was 148737.
 * 
 * --- Part Two ---
 * After getting the first capsule (it contained a star! what great fortune!), the machine detects your success and begins to rearrange itself.
 * 
 * When it's done, the discs are back in their original configuration as if it were time=0 again, but a new disc with 11 positions and starting 
 * at position 0 has appeared exactly one second below the previously-bottom disc.
 * 
 * With this new disc, and counting again starting from time=0 with the configuration in your puzzle input, what is the first time you can press the button to get another capsule?
 * 
 * Your puzzle answer was 2353212.
 * 
 * @author bumpverde
 */
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
