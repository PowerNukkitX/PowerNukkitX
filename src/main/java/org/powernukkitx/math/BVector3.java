package org.powernukkitx.math;

import org.powernukkitx.api.DoNotModify;
import org.powernukkitx.level.Location;

import static java.lang.StrictMath.asin;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.toDegrees;
import static java.lang.StrictMath.toRadians;

/**
 * A vector calculation tool that integrates the conversion functions of yaw and pitch and coordinate space at the same time
 */


public final class BVector3 {
    /**
     * the unit vector of a vector
     */
    private Vector3 vector3;//normalized direction vector with a modulus of 1
    private double yaw;//-90 270
    private double pitch;//-90 90
    /**
     * the modulus of the vector
     */
    private double length;

    /**
     * Initialize BVector3 by passing in the yaw and pitch of Location<br>
     * The module length of BVector3 returned by this method is 1
     *
     * @param location the location
     * @return the b vector 3
     */
    public static BVector3 fromLocation(Location location) {
        return fromLocation(location, 1);
    }

    /**
     * Initialize BVector3 by passing in the yaw and pitch of the Location<br>
     * The module length of the BVector3 returned by this method is the length value passed in
     *
     * @param location the location
     * @return the b vector 3
     */
    public static BVector3 fromLocation(Location location, double length) {
        return new BVector3(location.getYaw(), location.getPitch(), length);
    }

    /**
     * Initialize BVector3 by passing in yaw and pitch<br>
     * The module length of BVector3 returned by this method is 1
     *
     * @param yaw   the yaw (-90 270)
     * @param pitch the pitch
     * @return the b vector 3
     */
    public static BVector3 fromAngle(double yaw, double pitch) {
        return new BVector3(yaw, pitch, 1);
    }

    /**
     * Initialize B Vector 3 with the vector coordinates passed in
     *
     * @param pos the vector coordinates
     * @return the b vector 3
     */
    public static BVector3 fromPos(Vector3 pos) {
        return new BVector3(pos);
    }

    /**
     * Initialize B Vector 3 with the vector coordinates passed in
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the b vector 3
     */
    public static BVector3 fromPos(double x, double y, double z) {
        return fromPos(new Vector3(x, y, z));
    }

    /**
     * Initialize B Vector 3 by the modulus of the incoming yaw, pitch and vector
     *
     * @param yaw    the yaw
     * @param pitch  the pitch
     * @param length the vector modulus
     */
    private BVector3(double yaw, double pitch, double length) {
        this.vector3 = getDirectionVector(yaw, pitch);
        this.yaw = getYawFromVector(this.vector3);
        this.pitch = getPitchFromVector(this.vector3);
        this.length = length;
    }

    /**
     * Initialize B Vector 3 with the vector coordinates passed in
     *
     * @param vector3 the vector coordinates
     */
    private BVector3(Vector3 vector3) {
        this.yaw = getYawFromVector(vector3);
        this.pitch = getPitchFromVector(vector3);
        this.vector3 = getDirectionVector(yaw, pitch);
        this.length = vector3.length();
    }

    /**
     * Set Yaw
     *
     * @param yaw the yaw
     * @return the yaw
     */
    public BVector3 setYaw(double yaw) {
        this.vector3 = getDirectionVector(yaw, this.pitch);
        //recalculate the equivalent yaw value within range
        this.yaw = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * Set pitch.
     *
     * @param pitch the pitch
     * @return the pitch
     */
    public BVector3 setPitch(double pitch) {
        this.vector3 = getDirectionVector(this.yaw, pitch);
        //recalculate the equivalent pitch value within range
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * Rotate Yaw
     *
     * @param yaw the yaw
     * @return the b vector 3
     */
    public BVector3 rotateYaw(double yaw) {
        this.yaw += yaw;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        //recalculate the equivalent yaw value within range
        this.yaw = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * Rotate Pitch
     *
     * @param pitch the pitch
     * @return the b vector 3
     */
    public BVector3 rotatePitch(double pitch) {
        this.pitch += pitch;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        //recalculate the equivalent pitch value within range
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * Rotate yaw and pitch
     *
     * @param yaw   the yaw
     * @param pitch the pitch
     * @return the b vector 3
     */
    public BVector3 rotate(double yaw, double pitch) {
        this.pitch += pitch;
        this.yaw += yaw;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        //recalculate the equivalent pitch value within range
        this.pitch = getPitchFromVector(this.vector3);
        this.pitch = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * Vector addition
     *
     * @return the resulting vector
     */
    public BVector3 add(double x, double y, double z) {
        var pos = this.vector3.multiply(this.length);
        pos.add(x, y, z);
        this.yaw = getYawFromVector(pos);
        this.pitch = getPitchFromVector(pos);
        this.vector3 = pos.normalize();
        this.length = pos.length();
        return this;
    }

    /**
     * Vector addition
     *
     * @return the resulting vector
     */
    public BVector3 add(Vector3 vector3) {
        return add(vector3.x, vector3.y, vector3.z);
    }

    /**
     * Adding the direction vector of the specified modulus length to Vector3(0, 0, 0)<br> actually returns the coordinates of this vector
     *
     * @return the vector 3
     */
    public Vector3 addToPos() {
        return new Vector3(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
    }

    /**
     * Add the coordinates of this vector to pos
     *
     * @param pos the pos
     * @return the vector 3
     */
    public Vector3 addToPos(Vector3 pos) {
        return pos.add(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
    }

    /**
     * Set the modulus of this vector
     *
     * @param length the length
     * @return the length
     */
    public BVector3 setLength(double length) {
        this.length = length;
        return this;
    }

    /**
     * Increase the modulus of this vector<p/>
     * Of course you can also pass in a negative number, but make sure the final length is greater than 0!
     *
     * @param length the modulus to increase/decrease
     * @return itself
     */
    public BVector3 extend(double length) {
        if ((this.length + length) <= 0)
            throw new IllegalArgumentException("Vector length must bigger than zero");
        this.length += length;
        return this;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    /**
     * Get the unit direction vector
     *
     * @return the direction vector
     */
    public Vector3 getDirectionVector() {
        return vector3.clone();
    }

    /**
     * Get the uncloned unit direction vector
     *
     * @return the direction vector
     */
    @DoNotModify
    public Vector3 getUnclonedDirectionVector() {
        return vector3;
    }

    /**
     * Calculate the equivalent Vector3 direction vector from yaw and pitch
     *
     * @param yaw   yaw
     * @param pitch pitch
     * @return the Vector3 direction vector
     */
    public static Vector3 getDirectionVector(double yaw, double pitch) {
        var pitch0 = toRadians(pitch + 90);
        var yaw0 = toRadians(yaw + 90);
        var x = sin(pitch0) * cos(yaw0);
        var z = sin(pitch0) * sin(yaw0);
        var y = cos(pitch0);
        return new Vector3(x, y, z).normalize();
    }

    /**
     * Calculate yaw from the direction vector
     *
     * @param vector the direction vector
     * @return yaw
     */
    public static double getYawFromVector(Vector3 vector) {
        double length = vector.x * vector.x + vector.z * vector.z;
        // avoid NaN
        if (length == 0) {
            return 0;
        }
        double yaw = toDegrees(asin(-vector.x / sqrt(length)));
        return -vector.z > 0.0D ? 180.0D - yaw : StrictMath.abs(yaw) < 1E-10 ? 0 : yaw;
    }

    /**
     * Calculate the pitch by the direction vector
     *
     * @param vector the direction vector
     * @return pitch
     */
    public static double getPitchFromVector(Vector3 vector) {
        double length = vector.x * vector.x + vector.z * vector.z + vector.y * vector.y;
        // avoid NaN
        if (length == 0) {
            return 0;
        }
        var pitch = toDegrees(asin(-vector.y / sqrt(length)));
        return StrictMath.abs(pitch) < 1E-10 ? 0 : pitch;
    }
}
