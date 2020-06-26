import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false; // (like color in RB BST)

    private Node root; // root of the KdTree
    private int size; // size of KdTree

    // since we don't need to implement the rank() and select() operations,
    // there is no need to store the subtree size.
    private static class Node {
        private Point2D pt;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D pt, RectHV rect) {
            this.pt = pt;
            this.rect = rect;
        }
    }

    // construct an empty set of points
    public         KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public           boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public               int size() {
        return  size;
    }

    // add the point to the set (if it is not already in the set)
    public              void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls insert() with a null");

        root = insert(root, p, new RectHV(0, 0, 1, 1), VERTICAL);
    }

    private Node insert(Node x, Point2D p, RectHV rect, boolean orientation) {
        // if x is null we've reached the end and can add a new node
        if (x == null) {
            this.size++;
            return new Node(p, rect);
        }

        // if the node's point equals the point passed in
        // then return that node to avoid duplicates
        if (x.pt.equals(p))
            return  x;

        // determine if a node belongs to the left or right branch of the tree
        // based off it's orientation. The root node is VERTICAL and the
        // orientation
        // alternates between that and HORIZONTAL
        if (orientation == VERTICAL) {

            // if the current node is VERTICAL then the node it branches from
            // will be HORIZONTAL
            // so the x values are compared to determine which side to add the
            // new node to

            int cmp = Double.compare(p.x(), x.pt.x());
            if (cmp < 0) x.lb = insert(x.lb, p, new RectHV(x.rect.xmin(), x.rect.ymin(), x.pt.x(), x.rect.ymax()), HORIZONTAL);
            else  x.rt = insert(x.rt, p, new RectHV(x.pt.x(), x.rect.ymin(), x.rect.xmax(), x.rect.ymax()), HORIZONTAL);
        }
        else {

            // same as above except the current node is HORIZONTAL so the
            // branches will be VERTICAL
            // the y values are compared to determine which side to add the new
            // node to

            int cmp = Double.compare(p.y(), x.pt.y());
            if (cmp < 0) x.lb = insert(x.lb, p, new RectHV(x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.pt.y()), VERTICAL);
            else  x.rt = insert(x.rt, p, new RectHV(x.rect.xmin(), x.pt.y(), x.rect.xmax(), x.rect.ymax()), VERTICAL);
        }
        return x;
    }




    // does the set contain point p?
    public           boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls contains() with a null");

        return get(p);
    }

    private boolean get(Point2D p) {
        return get(root, p, VERTICAL);
    }

    private boolean get(Node x, Point2D p, boolean orientation) {
        if (x == null) return false;
        if (p.equals(x.pt)) return true;

        int cmp;
        if (orientation == VERTICAL)
            cmp = Double.compare(p.x(), x.pt.x());
        else
            cmp = Double.compare(p.y(), x.pt.y());

        if (cmp < 0) return get(x.lb, p, !orientation);
        else          return get(x.rt, p, !orientation);

    }

    // draw all points to standard draw
    public              void draw() {
        draw(root, VERTICAL);
    }

    // draws red lines for VERTICAL line segments
    // draws blue lines for HORIZONTAL line segments
    private void draw(Node x, boolean orientation) {

        // Red line
        if (orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.pt.x(), x.rect.ymin(), x.pt.x(), x.rect.ymax());
        }
        // Blue line
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(), x.pt.y(), x.rect.xmax(), x.pt.y());
        }

        if (x.lb != null)
            draw(x.lb, !orientation);
        if (x.rt != null)
            draw(x.rt, !orientation);

        // draw point last to be on top of line
        StdDraw.setPenColor(StdDraw.BLACK);
        x.pt.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("calls range() with a null");
        if (isEmpty()) return null;
        else {
            Queue<Point2D> points = new Queue<>();
            range(root, rect, points);
            return points;
        }
    }

    private void range(Node x, RectHV rect, Queue<Point2D> points) {

        if (x != null) {

            // если не пересекается, то и не рассматривать
            if (!x.rect.intersects(rect)) return;

            // Chek if Point in Node lies in given rectangle
            if (rect.contains(x.pt)) points.enqueue(x.pt);

            // Recursively search lb and rt
            range(x.lb, rect, points);
            range(x.rt, rect, points);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public           Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls nearest() with a null");
        if (isEmpty()) return null;
        else return nearest(root, p, root.pt, VERTICAL);
    }

    /*private Point2D nearest(Node node, Point2D p, Point2D nearest, boolean orientation){
        if(node == null)
            return nearest;
        else{
            if(node.pt.distanceSquaredTo(p) < nearest.distanceSquaredTo(p))	//If the distance from the node point is smaller than nearest found
                nearest = node.pt;
            if(node.lb != null && node.lb.rect.distanceSquaredTo(p) < node.pt.distanceSquaredTo(p)){	//If the distance from the nodes left child rect is smaller
                nearest = nearest(node.lb, p, nearest);
            }
            if(node.right != null && node.right.rect.distanceSquaredTo(p) < node.pt.distanceSquaredTo(p))	// if the distance from the nodes right child rect is smaller
                nearest = nearest(node.right, p , nearest);

            return nearest;
        }

    } */

     private Point2D nearest(Node x, Point2D p, Point2D min, boolean orientation) {
        if (x == null) return min;

        if(x.pt.distanceSquaredTo(p) < min.distanceSquaredTo(p))	//If the distance from the node point is smaller than nearest found
             min = x.pt;

        if (orientation == VERTICAL) {
            if (p.x() < x.pt.x()) {
                min = nearest(x.lb, p, min, HORIZONTAL);
                if (x.rt != null && min.distanceSquaredTo(p) > x.rt.rect.distanceSquaredTo(p))
                    min = nearest(x.rt, p, min, HORIZONTAL);
            }
            else {
                min = nearest(x.rt, p, min, HORIZONTAL);
                if (x.lb != null && min.distanceSquaredTo(p) > x.lb.rect.distanceSquaredTo(p))
                    min = nearest(x.lb, p, min, HORIZONTAL);
            }
        }
        else {
            if (p.y() < x.pt.y()) {
                min = nearest(x.lb, p, min, VERTICAL);
                if (x.rt != null && min.distanceSquaredTo(p) > x.rt.rect.distanceSquaredTo(p))
                    min = nearest(x.rt, p, min, VERTICAL);
            }
            else {
                min = nearest(x.rt, p, min, VERTICAL);
                if (x.lb != null && min.distanceSquaredTo(p) > x.lb.rect.distanceSquaredTo(p))
                    min = nearest(x.lb, p, min, VERTICAL);
            }
        }
        return min;
    }


    // unit testing of the methods (optional)
    public static void main(String[] args) { }
}
