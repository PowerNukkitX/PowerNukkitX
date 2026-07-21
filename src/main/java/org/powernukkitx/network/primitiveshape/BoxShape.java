package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.BoxDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class BoxShape extends PrimitiveShape<BoxShape> {

    protected Vector3 bound;

    public BoxShape(Vector3 origin, Vector3 bound) {
        super(origin);
        this.bound = bound;
    }

    public static BoxShape of(Vector3 origin, Vector3 bound) {
        return new BoxShape(origin, bound);
    }

    public BoxShape bound(Vector3 bound) {
        this.bound = bound;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.BOX;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        BoxDataPayload payload = new BoxDataPayload();
        payload.setBoxBound(bound.toNetwork());
        return payload;
    }
}
