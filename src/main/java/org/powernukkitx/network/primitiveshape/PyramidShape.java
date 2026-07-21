package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.PyramidDataPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.powernukkitx.math.Vector3;

public class PyramidShape extends PrimitiveShape<PyramidShape> {

    protected float width;
    protected Float depth;
    protected float height;

    public PyramidShape(Vector3 location, float width, float height) {
        super(location);
        this.width = width;
        this.height = height;
    }

    public static PyramidShape of(Vector3 location, float width, float height) {
        return new PyramidShape(location, width, height);
    }

    public PyramidShape width(float width) {
        this.width = width;
        return this;
    }

    public PyramidShape depth(float depth) {
        this.depth = depth;
        return this;
    }

    public PyramidShape height(float height) {
        this.height = height;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.PYRAMID;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        PyramidDataPayload payload = new PyramidDataPayload();
        payload.setWidth(width);
        payload.setDepth(depth);
        payload.setHeight(height);
        return payload;
    }
}
