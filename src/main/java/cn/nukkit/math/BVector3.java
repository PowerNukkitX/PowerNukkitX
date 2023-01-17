package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Location;

import static java.lang.StrictMath.*;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@PowerNukkitXDifference(info = "update Angle algorithm", since = "1.19.50-r4")
public final class BVector3 {
    private Vector3 vector3;//标准化的方向向量,模长为1
    private double yaw;
    private double pitch;
    private double length;

    /**
     * From location to BVector 3.
     *
     * @param location the location
     * @return the b vector 3
     */
    public static BVector3 fromLocation(Location location) {
        return new BVector3(location.getYaw(), location.getPitch());
    }

    /**
     * From angle to BVector 3.
     *
     * @param yaw   the yaw
     * @param pitch the pitch
     * @return the b vector 3
     */
    public static BVector3 fromAngle(double yaw, double pitch) {
        return new BVector3(yaw, pitch);
    }

    /**
     * From pos to BVector 3.
     *
     * @param pos the pos
     * @return the b vector 3
     */
    public static BVector3 fromPos(Vector3 pos) {
        return new BVector3(pos);
    }

    /**
     * From pos to BVector 3.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the b vector 3
     */
    public static BVector3 fromPos(double x, double y, double z) {
        return fromPos(new Vector3(x, y, z));
    }

    private BVector3(double yaw, double pitch) {
        this.vector3 = getDirectionVector(yaw, pitch);
        this.yaw = getYawFromVector(this.vector3);
        this.pitch = getPitchFromVector(this.vector3);
        this.length = 1;
    }

    private BVector3(Vector3 vector3) {
        this.yaw = getYawFromVector(vector3);
        this.pitch = getPitchFromVector(vector3);
        this.vector3 = getDirectionVector(yaw, pitch);
        this.length = 1;
    }

    /**
     * 设置Yaw
     *
     * @param yaw the yaw
     * @return the yaw
     */
    public BVector3 setYaw(double yaw) {
        this.vector3 = getDirectionVector(yaw, this.pitch);
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
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * 旋转Yaw
     *
     * @param yaw the yaw
     * @return the b vector 3
     */
    public BVector3 rotateYaw(double yaw) {
        this.yaw += yaw;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        this.yaw = getYawFromVector(this.vector3);
        return this;
    }

    /**
     * 旋转Pitch
     *
     * @param pitch the pitch
     * @return the b vector 3
     */
    public BVector3 rotatePitch(double pitch) {
        this.pitch += pitch;
        this.vector3 = getDirectionVector(this.yaw, this.pitch);
        this.pitch = getPitchFromVector(this.vector3);
        return this;
    }

    /**
     * 添加指定模长的方向向量到Vector3(0, 0, 0)
     *
     * @return the vector 3
     */
    public Vector3 addToPos() {
        return addToPos(new Vector3(0, 0, 0));
    }

    /**
     * 添加指定模长的方向向量到指定坐标
     *
     * @param pos the pos
     * @return the vector 3
     */
    public Vector3 addToPos(Vector3 pos) {
        return pos.add(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
    }

    /**
     * 设置该方向向量的模
     *
     * @param length the length
     * @return the length
     */
    public BVector3 setLength(double length) {
        this.length = length;
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
     *
     * @param vector 方向向量
     * @return yaw
     */
    public static double getYawFromVector(Vector3 vector) {
        double yaw = toDegrees(asin(-vector.x / sqrt(vector.x * vector.x + vector.z * vector.z)));
        return -vector.z > 0.0D ? 180.0D - yaw : StrictMath.abs(yaw) < 1E-10 ? 0 : yaw;
    }

    /**
     * 通过方向向量计算出pitch
     *
     * @param vector 方向向量
     * @return pitch
     */
    public static double getPitchFromVector(Vector3 vector) {
        var pitch = toDegrees(asin(-vector.y / sqrt(vector.x * vector.x + vector.z * vector.z + vector.y * vector.y)));
        return StrictMath.abs(pitch) < 1E-10 ? 0 : pitch;
    }
}
