package com.verde.advent.y2021;

import com.verde.advent.AdventUtils;
import com.verde.advent.Point2D;
import com.verde.advent.Point3D;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class Y2021D02 {
    private static final String UP="up", DOWN="down", FORWARD="forward";

    private static final Point2D UP_DIR2D = new Point2D(0, -1);
    private static final Point2D DOWN_DIR2D = new Point2D(0, 1);
    private static final Point2D FORWARD_DIR2D = new Point2D(1, 0);
    private static final Map<String, Point2D> vectorsByDir2D = new HashMap<String,Point2D>();
    static {
        vectorsByDir2D.put(UP, UP_DIR2D);
        vectorsByDir2D.put(DOWN, DOWN_DIR2D);
        vectorsByDir2D.put(FORWARD, FORWARD_DIR2D);
    };

    private static final Point3D UP_DIR3D = new Point3D(0, 0, -1);
    private static final Point3D DOWN_DIR3D = new Point3D(0, 0, 1);
    private static final Point3D FORWARD_DIR3D = new Point3D(1, 0, 0);
    private static final Map<String, Point3D> vectorsByDir3D = new HashMap<String,Point3D>();
    static {
        vectorsByDir3D.put(UP, UP_DIR3D);
        vectorsByDir3D.put(DOWN, DOWN_DIR3D);
        vectorsByDir3D.put(FORWARD, FORWARD_DIR3D);
    };

    public Point2D processDirections2D(Collection<String> directions, Point2D pos) {
        for (String cmd : directions) {
            String[] parts = cmd.split(" ");
            String dir = parts[0];
            int scale = Integer.valueOf(parts[1]);

            // Determine the delta to apply based on direction
            Point2D delta = vectorsByDir2D.get(dir).scale(scale);

            // Compute new pos based on delta
            pos = pos.add(delta);

        }
        return pos;
    }

    public Point3D processDirections3D(Collection<String> directions, Point3D pos) {
        for (String cmd : directions) {
            String[] parts = cmd.split(" ");
            String dir = parts[0];
            int scale = Integer.valueOf(parts[1]);
            Point3D incoming = pos.copy();

            // Determine the delta to apply based on direction
            Point3D delta = vectorsByDir3D.get(dir);
            delta = delta.scale(scale);

            // Compute new pos based on delta
            pos = pos.add(delta);

            // Apply extra processing for forward
            if (dir.equals("forward")) {
                Point3D depthDelta = new Point3D(0, pos.getZ() * scale, 0);
                pos = pos.add(depthDelta);
            }

//            System.out.printf("Incoming=%s; after cmd=%s w/scale=%d and delta=%s, pos=%s\n", incoming, dir, scale, delta, pos);
        }
        return pos;
    }

    @Test
    public void testProcessDirections2D() {
        Collection<String> cmds = new LinkedList<String>();
        cmds.add("forward 5");
        cmds.add("down 5");
        cmds.add("forward 8");
        cmds.add("up 3");
        cmds.add("down 8");
        cmds.add("forward 2");

        Point2D expectedPos = new Point2D(15, 10);
        Point2D actualPos = processDirections2D(cmds, Point2D.ORIGIN);
        int expectedProduct = 150;

        Assert.assertEquals("processDirections() incorrect", expectedPos, actualPos);
        Assert.assertEquals("processDirections() incorrect", expectedProduct, actualPos.getX() * actualPos.getY());
    }

    @Test
    public void testStar1() {
        Collection<String> cmds = STAR_INPUTS;
        Point2D actualPos = processDirections2D(cmds, Point2D.ORIGIN);

        System.out.println(actualPos);
        assertEquals("STAR1 incorrect", 1636725, actualPos.getX() * actualPos.getY());
    }

    @Test
    public void testProcessDirections3D() {
        Collection<String> cmds = new LinkedList<String>();
        cmds.add("forward 5");
        cmds.add("down 5");
        cmds.add("forward 8");
        cmds.add("up 3");
        cmds.add("down 8");
        cmds.add("forward 2");

        Point3D expectedPos = new Point3D(15, 60, 10);
        Point3D actualPos = processDirections3D(cmds, Point3D.ORIGIN);
        int expectedProduct = 900;

        Assert.assertEquals("processDirections3D() incorrect", expectedPos, actualPos);
        Assert.assertEquals("processDirections3D() incorrect", expectedProduct, actualPos.getX() * actualPos.getY());
    }

    @Test
    public void testStar2() {
        Collection<String> cmds = STAR_INPUTS;
        Point3D actualPos = processDirections3D(cmds, Point3D.ORIGIN);

        System.out.println(actualPos);
        assertEquals("STAR2 incorrect", 1636725, actualPos.getX() * actualPos.getY());
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2021/day2-inputs.txt");
}
