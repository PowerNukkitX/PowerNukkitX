package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.SphereDataPayload;
import org.powernukkitx.math.Vector3;

public class CircleShape extends PrimitiveShape<CircleShape> {

    public static final int DEFAULT_SEGMENTS = 20;

    protected int segments = DEFAULT_SEGMENTS;

    public CircleShape(Vector3 center, float radius) {
        super(center);
        scale(radius);
    }

    public static CircleShape of(Vector3 center, float radius) {
        return new CircleShape(center, radius);
    }

    public CircleShape radius(float radius) {
        return scale(radius);
    }

    public CircleShape segments(int segments) {
        this.segments = segments;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.CIRCLE;
    }

    @Override
    protected ExtraShapeDataPayload buildExtra() {
        SphereDataPayload payload = new SphereDataPayload();
        payload.setNumSegments(segments);
        return payload;
    }
}
