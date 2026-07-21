package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.CylinderDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class CylinderShape extends PrimitiveShape<CylinderShape> {

    public static final int DEFAULT_SEGMENTS = 20;

    protected Vector2f radiusX;
    protected Vector2f radiusZ;
    protected float height;
    protected int segments = DEFAULT_SEGMENTS;

    public CylinderShape(Vector3 center, float radius, float height) {
        super(center);
        this.radiusX = Vector2f.from(radius, radius);
        this.radiusZ = Vector2f.from(radius, radius);
        this.height = height;
    }

    public static CylinderShape of(Vector3 center, float radius, float height) {
        return new CylinderShape(center, radius, height);
    }

    public CylinderShape radiusX(Vector2f radiusX) {
        this.radiusX = radiusX;
        return this;
    }

    public CylinderShape radiusZ(Vector2f radiusZ) {
        this.radiusZ = radiusZ;
        return this;
    }

    public CylinderShape height(float height) {
        this.height = height;
        return this;
    }

    public CylinderShape segments(int segments) {
        this.segments = segments;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.CYLINDER;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        CylinderDataPayload payload = new CylinderDataPayload();
        payload.setRadiusX(radiusX);
        payload.setRadiusZ(radiusZ);
        payload.setHeight(height);
        payload.setNumSegments(segments);
        return payload;
    }
}
