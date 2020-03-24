package com.verde.advent.y2018.d3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * --- Day 3: No Matter How You Slice It ---
 * The Elves managed to locate the chimney-squeeze prototype fabric for Santa's suit 
 * (thanks to someone who helpfully wrote its box IDs on the wall of the warehouse in the middle of the night). 
 * Unfortunately, anomalies are still affecting them - nobody can even agree on how to cut the fabric.
 * 
 * The whole piece of fabric they're working on is a very large square - at least 1000 inches on each side.
 * 
 * Each Elf has made a claim about which area of fabric would be ideal for Santa's suit. 
 * All claims have an ID and consist of a single rectangle with edges parallel to the edges of the fabric. 
 * Each claim's rectangle is defined as follows:
 * 
 * The number of inches between the left edge of the fabric and the left edge of the rectangle.
 * The number of inches between the top edge of the fabric and the top edge of the rectangle.
 * The width of the rectangle in inches.
 * The height of the rectangle in inches.
 * 
 * A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3 inches from the left edge, 
 * 2 inches from the top edge, 5 inches wide, and 4 inches tall. Visually, it claims the square inches of 
 * fabric represented by # (and ignores the square inches of fabric represented by .) in the diagram below:
 * 
 * ...........
 * ...........
 * ...#####...
 * ...#####...
 * ...#####...
 * ...#####...
 * ...........
 * ...........
 * ...........
 * 
 * The problem is that many of the claims overlap, causing two or more claims to cover part of the same areas. 
 * For example, consider the following claims:
 * #1 @ 1,3: 4x4
 * #2 @ 3,1: 4x4
 * #3 @ 5,5: 2x2
 * Visually, these claim the following areas:
 * 
 * ........
 * ...2222.
 * ...2222.
 * .11XX22.
 * .11XX22.
 * .111133.
 * .111133.
 * ........
 * 
 * The four square inches marked with X are claimed by both 1 and 2. 
 * (Claim 3, while adjacent to the others, does not overlap either of them.)
 * 
 * If the Elves all proceed with their own plans, none of them will have enough fabric. 
 * How many square inches of fabric are within two or more claims?
 * 
 * Your puzzle answer was 109716.
 * The first half of this puzzle is complete! It provides one gold star: *
 * 
 * --- Part Two ---
 * Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square 
 * inch of fabric with any other claim. If you can somehow draw attention to it, maybe the Elves 
 * will be able to make Santa's suit after all!
 * 
 * For example, in the claims above, only claim 3 is intact after all claims are made.
 * 
 * What is the ID of the only claim that doesn't overlap?
 * 
 * @author bverde
 */
public class Y2018D03 {
    protected static Set<String>[][] stakeClaims(String[] specs) {
        List<Rect> claims =
                Stream
                .of(specs)
                .map(Rect::fromSpec)
                .collect(Collectors.toList());
        
        // Compute the extents of all the claims
        AtomicReference<Rect> extentsRef = new AtomicReference<Rect>(null);
        claims.forEach(r -> extentsRef.set(r.union(extentsRef.get())));
        Rect extents = extentsRef.get();
        System.out.printf("Extents: %s\n", extents);
        
        // Create a 2D array of the set of claim IDs that will store the count of the number of claims on the squares
        @SuppressWarnings("unchecked")
        Set<String>[][] claimIdsPerInch = new Set[extents.getXMax()][extents.getYMax()];
        for (int x=0; x<extents.getXMax(); ++x) {
            for (int y=0; y<extents.getYMax(); ++y) {
                claimIdsPerInch[x][y] = new HashSet<String>();
            }
        }
        
        // Go thru all the claims and record the claim IDs for each square inch
        claims.forEach(r -> {
            for (int x=r.getXMin(); x<r.getXMax(); ++x) {
                for (int y=r.getYMin(); y<r.getYMax(); ++y) {
                    claimIdsPerInch[x][y].add(r.getId());
                }
            }
        });

        return claimIdsPerInch;
    }
    
    public static int getNumSquareInchesWithMultipleClaims(String[] specs, int minClaims) {
        Set<String>[][] claimIdsPerInch = stakeClaims(specs);

        // Count all the square inches with >= the min number of claims
        int squareInches = 0;
        for (int x=0; x<claimIdsPerInch.length; ++x) {
            for (int y=0; y<claimIdsPerInch[0].length; ++y) {
                if (claimIdsPerInch[x][y].size() >= minClaims) {
                    ++squareInches;
                }
            }
        }

        return squareInches;
    }

    public static String getSoleClaim(String[] specs) {
        Set<String>[][] claimIdsPerInch = stakeClaims(specs);

        // Record all the IDs that have a primary claim on a square (these are potentials
        // at this point, since they may be shared in other places). Also record all the
        // claims that are known to be shared, so we can remove those later.
        Set<String> soleClaims = new HashSet<String>();
        Set<String> multiClaims = new HashSet<String>();
        for (int x=0; x<claimIdsPerInch.length; ++x) {
            for (int y=0; y<claimIdsPerInch[0].length; ++y) {
                Set<String> claims = claimIdsPerInch[x][y];
                
                if (claims.size() == 1) {
                    soleClaims.addAll(claims);
                } else if (claims.size() > 1) {
                    multiClaims.addAll(claims);
                }
            }
        }
        
        // Make sure to clear out any claims that were discovered to be shared
        soleClaims.removeAll(multiClaims);
        
        System.out.printf("Sole Claims Discovered (%d):", soleClaims.size());
        soleClaims.forEach(sc -> System.out.printf(" %s", sc));
        System.out.println();
        
        String claim = soleClaims.stream().findFirst().orElse(null);        
        return claim;
    }
    
    /**
     * Rect is a rectangle that supports basic shape intersection methods.
     */
    public static class Rect {
        /** Match specs like "#3 @ 5,5: 2x2" */
        private static final String SPEC_REGEX = "#(\\d*)\\s@\\s(\\d*),(\\d*):\\s(\\d*)x(\\d*)";
        private static final Pattern SPEC_PATTERN = Pattern.compile(SPEC_REGEX);
        
        private String id;
        private int xMin, yMin; // min
        private int xMax, yMax; // max
        
        public static Rect fromSpec(String spec) {
            Matcher m = SPEC_PATTERN.matcher(spec);
            if (! m.matches()) {
                return null;
            }
            
            try {
                String id = m.group(1);
                int x = Integer.parseInt(m.group(2).trim());
                int y = Integer.parseInt(m.group(3).trim());
                int width = Integer.parseInt(m.group(4).trim());
                int height = Integer.parseInt(m.group(5).trim());
            
                return new Rect(id, x, y, x + width, y + height);
            } catch (Exception e) {
                return null;
            }
        }
        
        public Rect(int x1, int y1, int x2, int y2) {
            this("unset", x1, y1, x2, y2);
        }
            
        public Rect(String id, int x1, int y1, int x2, int y2) {
            this.id = id;
            this.xMin = Math.min(x1,  x2);
            this.yMin = Math.min(y1,  y2);
            this.xMax = Math.max(x1,  x2);
            this.yMax = Math.max(y1,  y2);
        }

        public String getId () {
            return id;
        }
        
        public int getXMin() {
            return xMin;
        }

        public int getYMin() {
            return yMin;
        }

        public int getXMax() {
            return xMax;
        }

        public int getYMax() {
            return yMax;
        }

        @Override
        public String toString() {
            return String.format("Min:(%d, %d), Max: (%d, %d)", getXMin(), getYMin(), getXMax(), getYMax());
        }
        
        public boolean contains(int x, int y) {
            return inRange(x, xMin, xMax) && inRange(y, yMin, yMax);
        }
        
        public static boolean inRange(int val, int min, int max) {
            return (val >= min) && (val <= max);
        }
        
        public int area() {
            return Math.abs(xMax - xMin) * Math.abs(yMax - yMin);
        }
        
        public Rect union(Rect r) {
            // Sanity check
            if (r == null) {
                return this;
            }
            
            return new Rect(
                    Math.min(getXMin(), r.getXMin()), 
                    Math.min(getYMin(), r.getYMin()), 
                    Math.max(getXMax(), r.getXMax()), 
                    Math.max(getYMax(), r.getYMax())); 
        }

        public Rect intersect(Rect r) {
            // Sanity check
            if (r == null) {
                return this;
            }
            
            // bottom-left point of intersection rectangle 
            int x5 = Math.max(xMin, r.xMin); 
            int y5 = Math.max(yMin, r.yMin); 
          
            // top-right point of intersection rectangle 
            int x6 = Math.min(xMax, r.xMax); 
            int y6 = Math.min(yMax, r.yMax); 
          
            return (x5 >= x6) || (y5 >= y6) 
                    ? null
                    : new Rect(x5, y5, x6, y6);
        }
    }
}
