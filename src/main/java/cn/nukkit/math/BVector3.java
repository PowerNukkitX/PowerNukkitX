package cn.nukkit.math;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Location;
import lombok.Getter;

@Deprecated
@DeprecationDetails(since = "1.19.50-r4",
        reason = "The implementation of this utility class is very crude and suspected to be buggy. Will be removed after a few versions",
        replaceWith = "AngleMath.java")
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class BVector3 {

    private double xzAxisAngle;
    private double yAxisAngle;//-90 -- 90
    private Vector3 pos;
    private double length;

    //only use the location's pitch and yaw
    public static BVector3 fromLocation(Location location) {
        return fromLocation(location, 1);
    }

    public static BVector3 fromLocation(Location location, double length) {
        return new BVector3(location.getYaw() - 270, -location.getPitch(), length);
    }

    public static BVector3 fromAngle(double xzAxisAngle, double yAxisAngle, double length) {
        return new BVector3(xzAxisAngle, yAxisAngle, length);
    }

    public static BVector3 fromPos(Vector3 pos) {
        return new BVector3(pos.clone());
    }

    public static BVector3 fromPos(double x, double y, double z) {
        return fromPos(new Vector3(x, y, z));
    }

    private BVector3(double xzAxisAngle, double yAxisAngle, double length) {
        convertAngle(xzAxisAngle, yAxisAngle);
        this.length = length;
        updatePos();
    }

    private BVector3(Vector3 pos) {
        this.pos = pos;
        updateAngle();
    }

    public BVector3 extend(double length) {
        this.length += length;
        updatePos();
        return this;
    }

    public BVector3 setLength(double length) {
        this.length = length;
        updatePos();
        return this;
    }

    public BVector3 setAngle(double xzAxisAngle, double yAxisAngle) {
        convertAngle(xzAxisAngle, yAxisAngle);
        updatePos();
        return this;
    }

    public BVector3 setYAngle(double yAngle) {
        this.yAxisAngle = yAngle;
        updatePos();
        return this;
    }

    public BVector3 setXZAngle(double xzAngle) {
        this.xzAxisAngle = xzAngle;
        updatePos();
        return this;
    }

    public BVector3 addAngle(double xzAxisAngle, double yAxisAngle) {
        convertAngle(this.xzAxisAngle + xzAxisAngle, this.yAxisAngle + yAxisAngle);
        updatePos();
        return this;
    }

    public BVector3 setPos(double x, double y, double z) {
        this.pos = this.pos.setComponents(x, y, z);
        updateAngle();
        return this;
    }

    public BVector3 addPos(double x, double y, double z) {
        this.pos = this.pos.add(x, y, z);
        updateAngle();
        return this;
    }

    public Vector3 addToPos(Vector3 pos) {
        return pos.add(this.pos.x, this.pos.y, this.pos.z);
    }

    public double getYaw() {
        double res = Double.isNaN(xzAxisAngle + 270) ? 0 : xzAxisAngle + 270;
        return pos.x < 0 ? res + 180 : res;
    }

    public double getHeadYaw() {
        return getYaw();
    }

    public double getPitch() {
        if (-yAxisAngle > 90) {
            return 90;
        } else if (-yAxisAngle < -90) {
            return -90;
        }
        return Double.isNaN(yAxisAngle) ? 0 : -yAxisAngle;
    }

    private void updatePos() {
        double y = sin(yAxisAngle) * length;
        double projectEdge = cos(yAxisAngle) * length;
        double x = cos(xzAxisAngle) * projectEdge;
        double z = sin(xzAxisAngle) * projectEdge;
        this.pos = new Vector3(x, y, z);
    }

    private void updateAngle() {
        this.xzAxisAngle = atan(pos.z / pos.x);
        double projectEdge = Math.sqrt(Math.pow(pos.x, 2) + Math.pow(pos.z, 2));
        this.yAxisAngle = atan(pos.y / projectEdge);
        this.length = Math.sqrt(Math.pow(projectEdge, 2) + Math.pow(pos.y, 2));
    }

    //convert the values of yAxisAngle and xzAxisAngle if it's not suitable;
    private void convertAngle(double xzAxisAngle, double yAxisAngle) {
        yAxisAngle = yAxisAngle % 360;
        if (Math.abs(yAxisAngle) <= 90) {
            this.xzAxisAngle = xzAxisAngle;
            this.yAxisAngle = yAxisAngle;
            return;
        }
        if (yAxisAngle < -90) {
            this.yAxisAngle = -(180 + yAxisAngle);
            this.xzAxisAngle = xzAxisAngle + 180;
            return;
        }
        if (yAxisAngle > 90) {
            this.yAxisAngle = 180 - yAxisAngle;
            this.xzAxisAngle = xzAxisAngle + 180;
            return;
        }
    }

    //Trigonometric functions which can use angle number
    public static double sin(double angle) {
        return Math.sin(Math.PI * (angle / 180));
    }

    public static double cos(double angle) {
        return Math.cos(Math.PI * (angle / 180));
    }

    public static double tan(double angle) {
        return Math.tan(Math.PI * (angle / 180));
    }

    public static double asin(double sin) {
        return 180 * Math.asin(sin) / Math.PI;
    }

    public static double acos(double cos) {
        return 180 * Math.acos(cos) / Math.PI;
    }

    public static double atan(double tan) {
        return 180 * Math.atan(tan) / Math.PI;
    }

    public static double minAbs(double a, double b) {
        return (Math.abs(a) < Math.abs(b)) ? a : b;
    }

    public static double maxAbs(double a, double b) {
        return (Math.abs(a) > Math.abs(b)) ? a : b;
    }
}
