
/****************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:
 *  Dependencies:
 *  Author:
 *  Date:
 *
 *  Data structure for maintaining a set of 2-D points,
 *    including rectangle and nearest-neighbor queries
 *
 *************************************************************************/

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> set;

    // construct an empty set of points
    public         PointSET() {
        set = new SET<Point2D>();
    }

    // is the set empty?
    public           boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public               int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public              void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls insert() with a null");

        if (!contains(p))
        set.add(p);
    }

    // does the set contain point p?
    public           boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls contains() with a null");

        return set.contains(p);
    }

    // draw all points to standard draw
    public              void draw() {
        for (Point2D p : set)
            p.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("calls range() with a null");

        if (set.isEmpty()) return null;
        else {
            SET<Point2D> tmp = new SET<>();
            for (Point2D p : set) {
                if (rect.contains(p))
                    tmp.add(p);
            }
            return tmp;
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public           Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls nearest() with a null");

        if (set.isEmpty()) return null;
        else {
            Point2D neighbor = null;

            for (Point2D n : set) {
                if (neighbor == null || p.distanceSquaredTo(n) < p.distanceSquaredTo(neighbor))
                    neighbor = n;
            }
            return neighbor;
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args)  { }
}