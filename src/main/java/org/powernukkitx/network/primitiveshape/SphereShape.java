package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.SphereDataPayload;
import org.powernukkitx.math.Vector3;

public class SphereShape extends PrimitiveShape<SphereShape> {

    public static final int DEFAULT_SEGMENTS = 20;

    protected int segments = DEFAULT_SEGMENTS;

    public SphereShape(Vector3 center, float radius) {
        super(center);
        scale(radius);
    }

    public static SphereShape of(Vector3 center, float radius) {
        return new SphereShape(center, radius);
    }

    public SphereShape radius(float radius) {
        return scale(radius);
    }

    public SphereShape segments(int segments) {
        this.segments = segments;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.SPHERE;
    }

    @Override
    protected ExtraShapeDataPayload buildExtra() {
        SphereDataPayload payload = new SphereDataPayload();
        payload.setNumSegments(segments);
        return payload;
    }
}
