package com.verde.advent;

public class Point2D implements Comparable<Point2D> {
    public static final Point2D ORIGIN = new Point2D(0, 0);
    
    private int x, y;
    
    public Point2D(int x, int y) {
        this.x = x; 
        this.y = y;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point2D copy() {
        return new Point2D(getX(), getY());
    }

    public Point2D add(Point2D rhs) {
        return (rhs == null) ? this : add(rhs.getX(), rhs.getY());
    }
    
    public Point2D add(int x, int y) {
        return new Point2D(getX() + x, getY() + y);
    }
    
    public Point2D scale(int m) {
        return new Point2D(getX() * m, getY() * m);
    }
    
    public int getManhattanDistanceToOrigin() {
        return getManhattanDistanceTo(ORIGIN);
    }
    
    public int getManhattanDistanceTo(Point2D p2) {
        return Math.abs(getX() - p2.getX()) + Math.abs(getY() - p2.getY());
    }
    
    @Override
    public int compareTo(Point2D rhs) {
        // Sort first on the Y coordinate
        if (getY() != rhs.getY()) {
            return getY() - rhs.getY();
        }

        // The sort on the X coordinate
        if (getX() != rhs.getX()) {
            return getX() - rhs.getX();
        }
        
        return 0;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Point2D)) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        Point2D rhs = (Point2D) o;
        return (x == rhs.x) && (y == rhs.y);
    }
}
