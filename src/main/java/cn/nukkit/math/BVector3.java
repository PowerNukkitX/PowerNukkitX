package cn.nukkit.math;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.level.Location;

import static java.lang.StrictMath.asin;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.toDegrees;
import static java.lang.StrictMath.toRadians;

/**
 * 向量计算工具，同时整合了yaw和pitch与坐标空间的转换功能
 * <p>
 * A vector calculation tool that integrates the conversion functions of yaw and pitch and coordinate space at the same time
 */


public final class BVector3 {
    /**
     * 向量的单位向量
     * <p>
     * the unit vector of a vector
     */
    private Vector3 vector3;//标准化的方向向量,模长为1
    private double yaw;//-90 270
    private double pitch;//-90 90
    /**
     * 向量的模
     */
    private double length;

    /**
     * 通过传入的Location的yaw与pitch初始化BVector3<br>
     * 此方法返回的BVector3的模长为1
     * <p>
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
     * 通过传入的Location的yaw与pitch初始化BVector3<br>
     * 此方法返回的BVector3的模长为传入的length值
     * <p>
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
     * 通过传入的yaw与pitch初始化BVector3<br>
     * 此方法返回的BVector3的模长为1
     * <p>
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
     * 通过传入的向量坐标初始化BVector3
     * <p>
     * Initialize B Vector 3 with the vector coordinates passed in
     *
     * @param pos 向量坐标
     * @return the b vector 3
     */
    public static BVector3 fromPos(Vector3 pos) {
        return new BVector3(pos);
    }

    /**
     * 通过传入的向量坐标初始化BVector3
     * <p>
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
     * 通过传入的yaw、pitch和向量的模初始化BVector3
     * <p>
     * Initialize B Vector 3 by the modulus of the incoming yaw, pitch and vector
     *
     * @param yaw    the yaw
     * @param pitch  the pitch
     * @param length 向量模
     */
    private BVector3(double yaw, double pitch, double length) {
        this.vector3 = getDirectionVector(yaw, pitch);
        this.yaw = getYawFromVector(this.vector3);
        this.pitch = getPitchFromVector(this.vector3);
        this.length = length;
    }

    /**
     * 通过传入的向量坐标初始化BVector3
     * <p>
     * Initialize B Vector 3 with the vector coordinates passed in
     *
     * @param vector3 向量坐标
     */
    private BVector3(Vector3 vector3) {
        this.yaw = getYawFromVector(vector3);
        this.pitch = getPitchFromVector(vector3);
        this.vector3 = getDirectionVector(yaw, pitch);
        this.length = vector3.length();
    }

    /**
     * 设置Yaw
     *
     * @param yaw the yaw
     * @return the yaw
     */
    public BVector3 setYaw(double yaw) {
        this.vector3 = getDirectionVector(yaw, this.pitch);
        //重新计算在范围内的等价yaw值
        this.yaw = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * 设置 pitch.
     *
     * @param pitch the pitch
     * @return the pitch
     */
    public BVector3 setPitch(double pitch) {
        this.vector3 = getDirectionVector(this.yaw, pitch);
        //重新计算在范围内的等价pitch值
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * 旋转Yaw
     * <p>
     * Rotate Yaw
     *
     * @param yaw the yaw
     * @return the b vector 3
     */
    public BVector3 rotateYaw(double yaw) {
        this.yaw += yaw;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        //重新计算在范围内的等价yaw值
        this.yaw = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * 旋转Pitch
     * <p>
     * Rotate Pitch
     *
     * @param pitch the pitch
     * @return the b vector 3
     */
    public BVector3 rotatePitch(double pitch) {
        this.pitch += pitch;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        //重新计算在范围内的等价pitch值
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * 旋转yaw和Pitch
     * <p>
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
        //重新计算在范围内的等价pitch值
        this.pitch = getPitchFromVector(this.vector3);
        this.pitch = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * 向量加法
     *
     * @return 结果向量
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
     * 向量加法
     *
     * @return 结果向量
     */
    public BVector3 add(Vector3 vector3) {
        return add(vector3.x, vector3.y, vector3.z);
    }

    /**
     * 添加指定模长的方向向量到Vector3(0, 0, 0)<br>
     * 其实就是返回此向量的坐标
     * <p>
     * Adding the direction vector of the specified modulus length to Vector3(0, 0, 0)<br> actually returns the coordinates of this vector
     *
     * @return the vector 3
     */
    public Vector3 addToPos() {
        return new Vector3(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
    }

    /**
     * 将此向量的坐标添加到pos上
     *
     * @param pos the pos
     * @return the vector 3
     */
    public Vector3 addToPos(Vector3 pos) {
        return pos.add(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
    }

    /**
     * 设置该向量的模
     *
     * @param length the length
     * @return the length
     */
    public BVector3 setLength(double length) {
        this.length = length;
        return this;
    }

    /**
     * 增加该向量的模<p/>
     * 当然你也可以传入负数，但请确保最终长度要大于0!
     *
     * @param length 增加/减少的模
     * @return 自身
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
     * 获取单位方向向量
     *
     * @return the direction vector
     */
    public Vector3 getDirectionVector() {
        return vector3.clone();
    }

    /**
     * 获取未克隆的单位方向向量
     *
     * @return the direction vector
     */
    @DoNotModify
    public Vector3 getUnclonedDirectionVector() {
        return vector3;
    }

    /**
     * 通过yaw与pitch计算出等价的Vector3方向向量
     *
     * @param yaw   yaw
     * @param pitch pitch
     * @return Vector3方向向量
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
     * 通过方向向量计算出yaw
     * <p>
     * Calculate yaw from the direction vector
     *
     * @param vector 方向向量
     * @return yaw
     */
    public static double getYawFromVector(Vector3 vector) {
        double length = vector.x * vector.x + vector.z * vector.z;
        // 避免NAN
        if (length == 0) {
            return 0;
        }
        double yaw = toDegrees(asin(-vector.x / sqrt(length)));
        return -vector.z > 0.0D ? 180.0D - yaw : StrictMath.abs(yaw) < 1E-10 ? 0 : yaw;
    }

    /**
     * 通过方向向量计算出pitch
     * <p>
     * Calculate the pitch by the direction vector
     *
     * @param vector 方向向量
     * @return pitch
     */
    public static double getPitchFromVector(Vector3 vector) {
        double length = vector.x * vector.x + vector.z * vector.z + vector.y * vector.y;
        // 避免NAN
        if (length == 0) {
            return 0;
        }
        var pitch = toDegrees(asin(-vector.y / sqrt(length)));
        return StrictMath.abs(pitch) < 1E-10 ? 0 : pitch;
    }
}
