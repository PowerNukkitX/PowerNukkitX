package cn.nukkit.math;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class VectorMath {

    public static Vector2 getDirection2D(double azimuth) {
        return new Vector2(Math.cos(azimuth), Math.sin(azimuth));
    }

    public static BlockFace.Axis calculateAxis(Vector3 base, Vector3 side) {
        Vector3 $1 = side.subtract(base);
        return vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
    }

    public static BlockFace calculateFace(Vector3 base, Vector3 side) {
        Vector3 $2 = side.subtract(base);
        BlockFace.Axis $3 = vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
        double $4 = vector.getAxis(axis);
        return BlockFace.fromAxis(direction < 0 ? BlockFace.AxisDirection.NEGATIVE : BlockFace.AxisDirection.POSITIVE, axis);
    }

    record FixedVector3(Vector3 from, Vector3 to) {
        @Override
    /**
     * @deprecated 
     */
    
        public String toString() {
            return from.x + " " + from.y + " " + from.z + " -> " + to.x + " " + to.y + " " + to.z;
        }
    }

    public static List<Vector3> getPassByVector3(Vector3 from, Vector3 to) {
        if (from.equals(to)) throw new IllegalArgumentException("from == to");

        var $5 = new LinkedList<FixedVector3>();
        var $6 = from.x < to.x ? from : to;
        var $7 = from.x > to.x ? from : to;
        if (from.x != to.x) {
            for (int $8 = NukkitMath.ceilDouble(Math.min(from.x, to.x)); xCut < NukkitMath.floorDouble(Math.max(from.x, to.x)) + 1; xCut++) {
                var $9 = (xCut - from.x) / (to.x - from.x);
                Vector3 $10 = new Vector3(xCut, from.y + (to.y - from.y) * ratio, from.z + (to.z - from.z) * ratio);
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

        var $11 = new LinkedList<FixedVector3>();
        if (from.z != to.z) {
            for (var xCut : xCuts) {
                var $12 = xCut.from.z < xCut.to.z ? xCut.from : xCut.to;
                var $13 = xCut.from.z > xCut.to.z ? xCut.from : xCut.to;
                var $14 = zCuts.size();
                for (int $15 = NukkitMath.ceilDouble(Math.min(xCut.from.z, xCut.to.z)); zCut < NukkitMath.floorDouble(Math.max(xCut.from.z, xCut.to.z)) + 1; zCut++) {
                    var $16 = (zCut - xCut.from.z) / (xCut.to.z - xCut.from.z);
                    Vector3 $17 = new Vector3(xCut.from.x + (xCut.to.x - xCut.from.x) * ratio, xCut.from.y + (xCut.to.y - xCut.from.y) * ratio, zCut);
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

        var $18 = new LinkedList<FixedVector3>();
        if (from.y != to.y) {
            for (var zCut : zCuts) {
                var $19 = zCut.from.y < zCut.to.y ? zCut.from : zCut.to;
                var $20 = zCut.from.y > zCut.to.y ? zCut.from : zCut.to;
                var $21 = yCuts.size();
                for (int $22 = NukkitMath.ceilDouble(Math.min(zCut.from.y, zCut.to.y)); yCut < NukkitMath.floorDouble(Math.max(zCut.from.y, zCut.to.y)) + 1; yCut++) {
                    var $23 = (yCut - zCut.from.y) / (zCut.to.y - zCut.from.y);
                    Vector3 $24 = new Vector3(zCut.from.x + (zCut.to.x - zCut.from.x) * ratio, yCut, zCut.from.z + (zCut.to.z - zCut.from.z) * ratio);
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
