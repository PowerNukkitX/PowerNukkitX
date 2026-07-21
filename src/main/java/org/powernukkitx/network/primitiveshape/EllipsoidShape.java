package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.EllipsoidDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class EllipsoidShape extends PrimitiveShape<EllipsoidShape> {

    public static final int DEFAULT_SEGMENTS = 20;

    protected Vector3f radii;
    protected int segmentsPerAxis = DEFAULT_SEGMENTS;

    public EllipsoidShape(Vector3 center, Vector3 radii) {
        super(center);
        this.radii = radii.toNetwork();
    }

    public static EllipsoidShape of(Vector3 center, Vector3 radii) {
        return new EllipsoidShape(center, radii);
    }

    public EllipsoidShape radii(Vector3 radii) {
        this.radii = radii.toNetwork();
        return this;
    }

    public EllipsoidShape segmentsPerAxis(int segmentsPerAxis) {
        this.segmentsPerAxis = segmentsPerAxis;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.ELLIPSOID;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        EllipsoidDataPayload payload = new EllipsoidDataPayload();
        payload.setRadii(radii);
        payload.setSegmentsPerAxis(segmentsPerAxis);
        return payload;
    }
}
