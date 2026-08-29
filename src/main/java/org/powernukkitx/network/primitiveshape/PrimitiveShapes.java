package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.ExtraShapeDataType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.PrimitiveShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.packet.PrimitiveShapesPacket;
import org.powernukkitx.Player;
import org.powernukkitx.math.Vector3;

public final class PrimitiveShapes {

    public static final ExtraShapeDataPayload REMOVAL_EXTRA = () -> ExtraShapeDataType.NONE;

    private PrimitiveShapes() {
    }

    public static LineShape line(Vector3 start, Vector3 end) {
        return new LineShape(start, end);
    }

    public static BoxShape box(Vector3 origin, Vector3 bound) {
        return new BoxShape(origin, bound);
    }

    public static SphereShape sphere(Vector3 center, float radius) {
        return new SphereShape(center, radius);
    }

    public static CircleShape circle(Vector3 center, float radius) {
        return new CircleShape(center, radius);
    }

    public static ArrowShape arrow(Vector3 start, Vector3 end) {
        return new ArrowShape(start, end);
    }

    public static TextShape text(Vector3 location, String text) {
        return new TextShape(location, text);
    }

    public static CylinderShape cylinder(Vector3 center, float radius, float height) {
        return new CylinderShape(center, radius, height);
    }

    public static PyramidShape pyramid(Vector3 location, float width, float height) {
        return new PyramidShape(location, width, height);
    }

    public static EllipsoidShape ellipsoid(Vector3 center, Vector3 radii) {
        return new EllipsoidShape(center, radii);
    }

    public static ConeShape cone(Vector3 base, float radius, float height) {
        return new ConeShape(base, radius, height);
    }

    public static PrimitiveShapeDataPayload removalPayload(long networkId) {
        PrimitiveShapeDataPayload payload = new PrimitiveShapeDataPayload();
        payload.setNetworkId(networkId);
        payload.setExtraShapeData(REMOVAL_EXTRA);
        return payload;
    }

    public static void remove(Player player, int... networkIds) {
        if (networkIds.length == 0) {
            return;
        }
        PrimitiveShapesPacket packet = new PrimitiveShapesPacket();
        for (int networkId : networkIds) {
            packet.getShapes().add(removalPayload(networkId));
        }
        player.sendPacket(packet);
    }

    public static void clear(Player player) {
        player.clearPrimitiveShapes();
    }
}
