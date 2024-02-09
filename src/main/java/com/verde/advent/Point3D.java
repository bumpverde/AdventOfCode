package com.verde.advent;

public class Point3D implements Comparable<Point3D> {
    public static final Point3D ORIGIN = new Point3D(0, 0, 0);

    private int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }

    public int getY() { return y; }

    public int getZ() { return z; }

    public Point3D copy() {
        return new Point3D(getX(), getY(), getZ());
    }

    public Point3D add(Point3D rhs) {
        return (rhs == null) ? this : add(rhs.getX(), rhs.getY(), rhs.getZ());
    }
    
    public Point3D add(int x, int y, int z) {
        return new Point3D(getX() + x, getY() + y, getZ() + z);
    }
    
    public Point3D scale(int m) {
        return new Point3D(getX() * m, getY() * m, getZ() * m);
    }
    
    public int getManhattanDistanceToOrigin() {
        return getManhattanDistanceTo(ORIGIN);
    }
    
    public int getManhattanDistanceTo(Point3D p2) {
        return Math.abs(getX() - p2.getX()) + Math.abs(getY() - p2.getY()) + Math.abs(getZ() - p2.getZ());
    }
    
    @Override
    public int compareTo(Point3D rhs) {
        // Sort first on the Z coordinate
        if (getZ() != rhs.getZ()) {
            return getZ() - rhs.getZ();
        }

        // Then sort on the Y coordinate
        if (getY() != rhs.getY()) {
            return getY() - rhs.getY();
        }

        // Then sort on the X coordinate
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
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Point3D)) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        Point3D rhs = (Point3D) o;
        return (x == rhs.x) && (y == rhs.y) && (z == rhs.z);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", getX(), getY(), getZ());
    }
}
