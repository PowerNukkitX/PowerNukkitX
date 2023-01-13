package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import static java.lang.StrictMath.*;

/**
 * 与角度有关的计算<p/>
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class AngleMath {

    /**
     * 通过yaw与pitch计算出等价的Vector3方向向量
     * @param yaw yaw
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
     * @param vector 方向向量
     * @return yaw
     */
    public static double getYawFromVector(Vector3 vector) {
        double yaw = toDegrees(asin(-vector.x / sqrt(vector.x * vector.x + vector.z * vector.z)));
        return -vector.z > 0.0D ? 180.0D - yaw : yaw;
    }

    /**
     * 通过方向向量计算出pitch
     * @param vector 方向向量
     * @return pitch
     */
    public static double getPitchFromVector(Vector3 vector) {
        return toDegrees(asin(-vector.y / sqrt(vector.x * vector.x + vector.z * vector.z + vector.y * vector.y)));
    }
}
