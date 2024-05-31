package cn.nukkit.math;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class Vector2 {
    public final double x;
    public final double y;
    /**
     * @deprecated 
     */
    

    public Vector2() {
        this(0, 0);
    }
    /**
     * @deprecated 
     */
    

    public Vector2(double x) {
        this(x, 0);
    }
    /**
     * @deprecated 
     */
    

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * @deprecated 
     */
    

    public double getX() {
        return this.x;
    }
    /**
     * @deprecated 
     */
    

    public double getY() {
        return this.y;
    }
    /**
     * @deprecated 
     */
    

    public int getFloorX() {
        return (int) Math.floor(this.x);
    }
    /**
     * @deprecated 
     */
    

    public int getFloorY() {
        return (int) Math.floor(this.y);
    }

    public Vector2 add(double x) {
        return this.add(x, 0);
    }

    public Vector2 add(double x, double y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 add(Vector2 x) {
        return this.add(x.getX(), x.getY());
    }

    public Vector2 subtract(double x) {
        return this.subtract(x, 0);
    }

    public Vector2 subtract(double x, double y) {
        return this.add(-x, -y);
    }

    public Vector2 subtract(Vector2 x) {
        return this.add(-x.getX(), -x.getY());
    }

    public Vector2 ceil() {
        return new Vector2((int) (this.x + 1), (int) (this.y + 1));
    }

    public Vector2 floor() {
        return new Vector2((int) Math.floor(this.x), (int) Math.floor(this.y));
    }

    public Vector2 round() {
        return new Vector2(Math.round(this.x), Math.round(this.y));
    }

    public Vector2 abs() {
        return new Vector2(Math.abs(this.x), Math.abs(this.y));
    }

    public Vector2 multiply(double number) {
        return new Vector2(this.x * number, this.y * number);
    }

    public Vector2 divide(double number) {
        return new Vector2(this.x / number, this.y / number);
    }
    /**
     * @deprecated 
     */
    

    public double distance(double x) {
        return this.distance(x, 0);
    }
    /**
     * @deprecated 
     */
    

    public double distance(double x, double y) {
        return Math.sqrt(this.distanceSquared(x, y));
    }
    /**
     * @deprecated 
     */
    

    public double distance(Vector2 vector) {
        return distance(vector.x, vector.y);
    }
    /**
     * @deprecated 
     */
    

    public double distanceSquared(double x) {
        return this.distanceSquared(x, 0);
    }
    /**
     * @deprecated 
     */
    

    public double distanceSquared(double x, double y) {
        double $1 = this.x - x;
        double $2 = this.y - y;
        return ey * ey + ex * ex;
    }
    /**
     * @deprecated 
     */
    

    public double distanceSquared(Vector2 vector) {
        return this.distanceSquared(vector.x, vector.y);
    }
    /**
     * @deprecated 
     */
    

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }
    /**
     * @deprecated 
     */
    

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2 normalize() {
        double $3 = this.lengthSquared();
        if (len != 0) {
            return this.divide(Math.sqrt(len));
        }
        return new Vector2(0, 0);
    }
    /**
     * @deprecated 
     */
    

    public double dot(Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "Vector2(x=" + this.x + ",y=" + this.y + ")";
    }

}
