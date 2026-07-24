package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.ExtraShapeDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.LineDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class LineShape extends PrimitiveShape<LineShape> {

    protected Vector3 end;

    public LineShape(Vector3 start, Vector3 end) {
        super(start);
        this.end = end;
    }

    public static LineShape between(Vector3 start, Vector3 end) {
        return new LineShape(start, end);
    }

    public LineShape end(Vector3 end) {
        this.end = end;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.LINE;
    }

    @Override
    protected ExtraShapeDataPayload buildExtra() {
        LineDataPayload payload = new LineDataPayload();
        payload.setLineEndLocation(end.toNetwork());
        return payload;
    }
}
