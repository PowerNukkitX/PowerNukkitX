package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ConeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class ConeShape extends PrimitiveShape<ConeShape> {

    public static final int DEFAULT_SEGMENTS = 20;

    protected Vector2f radii;
    protected float height;
    protected int segments = DEFAULT_SEGMENTS;

    public ConeShape(Vector3 base, float radius, float height) {
        super(base);
        this.radii = Vector2f.from(radius, radius);
        this.height = height;
    }

    public static ConeShape of(Vector3 base, float radius, float height) {
        return new ConeShape(base, radius, height);
    }

    public ConeShape radii(Vector2f radii) {
        this.radii = radii;
        return this;
    }

    public ConeShape height(float height) {
        this.height = height;
        return this;
    }

    public ConeShape segments(int segments) {
        this.segments = segments;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.CONE;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        ConeDataPayload payload = new ConeDataPayload();
        payload.setRadii(radii);
        payload.setHeight(height);
        payload.setNumSegments(segments);
        return payload;
    }
}
