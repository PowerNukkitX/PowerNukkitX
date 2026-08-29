package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.ArrowDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class ArrowShape extends PrimitiveShape<ArrowShape> {

    public static final int DEFAULT_SEGMENTS = 4;

    protected Vector3 end;
    protected Float headLength;
    protected Float headRadius;
    protected int segments = DEFAULT_SEGMENTS;

    public ArrowShape(Vector3 start, Vector3 end) {
        super(start);
        this.end = end;
    }

    public static ArrowShape between(Vector3 start, Vector3 end) {
        return new ArrowShape(start, end);
    }

    public ArrowShape end(Vector3 end) {
        this.end = end;
        return this;
    }

    public ArrowShape headLength(float headLength) {
        this.headLength = headLength;
        return this;
    }

    public ArrowShape headRadius(float headRadius) {
        this.headRadius = headRadius;
        return this;
    }

    public ArrowShape segments(int segments) {
        this.segments = segments;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.ARROW;
    }

    @Override
    protected ExtraShapeDataPayload buildExtra() {
        ArrowDataPayload payload = new ArrowDataPayload();
        payload.setArrowEndLocation(end.toNetwork());
        payload.setArrowHeadLength(headLength);
        payload.setArrowHeadRadius(headRadius);
        payload.setNumSegments(segments);
        return payload;
    }
}
