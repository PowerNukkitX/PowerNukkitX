package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class VectorMath {

    public static Vector2 getDirection2D(double azimuth) {
        return new Vector2(Math.cos(azimuth), Math.sin(azimuth));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static  BlockFace.Axis calculateAxis(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        return vector.x != 0? BlockFace.Axis.X : vector.z != 0? BlockFace.Axis.Z : BlockFace.Axis.Y;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static  BlockFace calculateFace(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        BlockFace.Axis axis = vector.x != 0? BlockFace.Axis.X : vector.z != 0? BlockFace.Axis.Z : BlockFace.Axis.Y;
        double direction = vector.getAxis(axis);
        return BlockFace.fromAxis(direction < 0? BlockFace.AxisDirection.NEGATIVE : BlockFace.AxisDirection.POSITIVE, axis);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    record FixedVector3(Vector3 from, Vector3 to){
        @Override
        public String toString() {
            return from.x + " " + from.y + " " + from.z + " -> " + to.x + " " + to.y + " " + to.z;
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public static List<Vector3> getPassByVector3(Vector3 from, Vector3 to){
        if (from.equals(to)) throw new IllegalArgumentException();

        var xCuts = new ArrayList<FixedVector3>();
        var lastXCut = from;
        for (double xCut = Math.ceil(Math.min(from.x, to.x)); xCut <= Math.floor(Math.max(from.x, to.x)); xCut++){
            var ratio = (xCut - from.x) / (to.x - from.x);
            Vector3 currentXCut = new Vector3(xCut, from.y + (to.y - from.y) * ratio, from.z + (to.z - from.z) * ratio);
            if (xCut != lastXCut.x){
                xCuts.add(new FixedVector3(lastXCut, currentXCut));
            }
            lastXCut = currentXCut;
        }

        var zCuts = new ArrayList<FixedVector3>();
        for (var xCut : xCuts){
            var lastZCut = xCut.from;
            var oldSize = zCuts.size();
            for (double zCut = Math.ceil(Math.min(xCut.from.z, xCut.to.z)); zCut <= Math.floor(Math.max(xCut.from.z, xCut.to.z)); zCut++){
                var ratio = (zCut - xCut.from.z) / (xCut.to.z - xCut.from.z);
                Vector3 currentZCut = new Vector3(xCut.from.x + (xCut.to.x - xCut.from.x) * ratio, xCut.from.y + (xCut.to.y - xCut.from.y) * ratio, zCut);
                if (zCut != lastZCut.z){
                    zCuts.add(new FixedVector3(lastZCut, currentZCut));
                }
                lastZCut = currentZCut;
            }
            if (oldSize == zCuts.size()) zCuts.add(xCut);
        }

        var yCuts = new ArrayList<FixedVector3>();
        for (var zCut : zCuts){
            var lastYCut = zCut.from;
            var oldSize = yCuts.size();
            for (double yCut = Math.ceil(Math.min(zCut.from.y, zCut.to.y)); yCut <= Math.floor(Math.max(zCut.from.y, zCut.to.y)); yCut++){
                var ratio = (yCut - zCut.from.y) / (zCut.to.y - zCut.from.y);
                Vector3 currentYCut = new Vector3(zCut.from.x + (zCut.to.x - zCut.from.x) * ratio, yCut, zCut.from.z + (zCut.to.z - zCut.from.z) * ratio);
                if (yCut != lastYCut.y){
                    yCuts.add(new FixedVector3(lastYCut, currentYCut));
                }
                lastYCut = currentYCut;
            }
            if (oldSize == yCuts.size()) yCuts.add(zCut);
        }

        var passBy = yCuts.stream().map(yCut -> yCut.from.floor()).collect(Collectors.toList());
        passBy.add(to.floor());//Add the missing end

        return passBy;
    }
}
