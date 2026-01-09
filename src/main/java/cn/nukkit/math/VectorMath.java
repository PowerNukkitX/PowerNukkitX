package cn.nukkit.math;

import com.google.common.base.Preconditions;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class VectorMath {

    public static Vector2 getDirection2D(double azimuth) {
        return new Vector2(Math.cos(azimuth), Math.sin(azimuth));
    }

    public static BlockFace.Axis calculateAxis(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        return vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
    }

    public static BlockFace calculateFace(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        BlockFace.Axis axis = vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
        double direction = vector.getAxis(axis);
        return BlockFace.fromAxis(direction < 0 ? BlockFace.AxisDirection.NEGATIVE : BlockFace.AxisDirection.POSITIVE, axis);
    }

    record FixedVector3(Vector3 from, Vector3 to) {
        @Override
        public String toString() {
            return from.x + " " + from.y + " " + from.z + " -> " + to.x + " " + to.y + " " + to.z;
        }
    }

    public static List<Vector3> getPassByVector3(Vector3 from, Vector3 to) {
        Preconditions.checkArgument(!Objects.equals(from, to), "from == to");

        LinkedList<FixedVector3> xCuts = new LinkedList<>();
        Vector3 lastXCut = from.x < to.x ? from : to;
        Vector3 targetXCut = from.x > to.x ? from : to;
        if (from.x != to.x) {
            for (int xCut = NukkitMath.ceilDouble(Math.min(from.x, to.x)); xCut < NukkitMath.floorDouble(Math.max(from.x, to.x)) + 1; xCut++) {
                double ratio = (xCut - from.x) / (to.x - from.x);
                Vector3 currentXCut = new Vector3(xCut, from.y + (to.y - from.y) * ratio, from.z + (to.z - from.z) * ratio);
                if (xCut != lastXCut.x) {
                    xCuts.add(new FixedVector3(lastXCut, currentXCut));
                }
                lastXCut = currentXCut;
                if (xCut + 1 > NukkitMath.floorDouble(Math.max(from.x, to.x))) {
                    xCuts.add(new FixedVector3(lastXCut, targetXCut));
                }
            }
        }

        if (xCuts.isEmpty()) xCuts.add(new FixedVector3(from, to));

        var zCuts = new LinkedList<FixedVector3>();
        if (from.z != to.z) {
            for (FixedVector3 xCut : xCuts) {
                Vector3 lastZCut = xCut.from.z < xCut.to.z ? xCut.from : xCut.to;
                Vector3 targetZCut = xCut.from.z > xCut.to.z ? xCut.from : xCut.to;
                int oldSize = zCuts.size();
                for (int zCut = NukkitMath.ceilDouble(Math.min(xCut.from.z, xCut.to.z)); zCut < NukkitMath.floorDouble(Math.max(xCut.from.z, xCut.to.z)) + 1; zCut++) {
                    double ratio = (zCut - xCut.from.z) / (xCut.to.z - xCut.from.z);
                    Vector3 currentZCut = new Vector3(xCut.from.x + (xCut.to.x - xCut.from.x) * ratio, xCut.from.y + (xCut.to.y - xCut.from.y) * ratio, zCut);
                    if (zCut != lastZCut.z) {
                        zCuts.add(new FixedVector3(lastZCut, currentZCut));
                    }
                    lastZCut = currentZCut;
                    if (zCut + 1 > NukkitMath.floorDouble(Math.max(xCut.from.z, xCut.to.z))) {
                        zCuts.add(new FixedVector3(lastZCut, targetZCut));
                    }
                }
                if (oldSize == zCuts.size()) zCuts.add(xCut);
            }
        }

        LinkedList<FixedVector3> yCuts = new LinkedList<>();
        if (from.y != to.y) {
            for (FixedVector3 zCut : zCuts) {
                Vector3 lastYCut = zCut.from.y < zCut.to.y ? zCut.from : zCut.to;
                Vector3 targetYCut = zCut.from.y > zCut.to.y ? zCut.from : zCut.to;
                int oldSize = yCuts.size();
                for (int yCut = NukkitMath.ceilDouble(Math.min(zCut.from.y, zCut.to.y)); yCut < NukkitMath.floorDouble(Math.max(zCut.from.y, zCut.to.y)) + 1; yCut++) {
                    double ratio = (yCut - zCut.from.y) / (zCut.to.y - zCut.from.y);
                    Vector3 currentYCut = new Vector3(zCut.from.x + (zCut.to.x - zCut.from.x) * ratio, yCut, zCut.from.z + (zCut.to.z - zCut.from.z) * ratio);
                    if (yCut != lastYCut.y) {
                        yCuts.add(new FixedVector3(lastYCut, currentYCut));
                    }
                    lastYCut = currentYCut;
                    if (yCut + 1 > NukkitMath.floorDouble(Math.max(zCut.from.y, zCut.to.y))) {
                        yCuts.add(new FixedVector3(lastYCut, targetYCut));
                    }
                }
                if (oldSize == yCuts.size()) yCuts.add(zCut);
            }
        } else {
            yCuts = zCuts;
        }

        return yCuts
                .stream()
                .map(yCut ->
                        new Vector3(
                                (yCut.from.x + yCut.to.x) * 0.5,
                                (yCut.from.y + yCut.to.y) * 0.5,
                                (yCut.from.z + yCut.to.z) * 0.5
                        ).floor()//这里取中点是为了防止浮点数精度丢失影响结果
                )
                .collect(Collectors.toList());
    }
}
