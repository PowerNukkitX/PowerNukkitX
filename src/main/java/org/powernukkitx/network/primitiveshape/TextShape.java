package org.powernukkitx.network.primitiveshape;

import org.cloudburstmc.protocol.bedrock.data.payload.shape.DebugShapePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.ScriptPrimitiveShapeType;
import org.cloudburstmc.protocol.bedrock.data.payload.shape.TextDataPayload;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockColor;

public class TextShape extends PrimitiveShape<TextShape> {

    protected String text;
    protected boolean useRotation = false;
    protected Integer backgroundColor;
    protected boolean depthTest = true;
    protected boolean showBackface = true;
    protected boolean showTextBackface = true;

    public TextShape(Vector3 location, String text) {
        super(location);
        this.text = text;
    }

    public static TextShape of(Vector3 location, String text) {
        return new TextShape(location, text);
    }

    public TextShape text(String text) {
        this.text = text;
        return this;
    }

    public TextShape useRotation(boolean useRotation) {
        this.useRotation = useRotation;
        return this;
    }

    public TextShape backgroundColor(int argb) {
        this.backgroundColor = argb;
        return this;
    }

    public TextShape backgroundColor(BlockColor color) {
        this.backgroundColor = ((color.getAlpha() & 0xff) << 24) | ((color.getRed() & 0xff) << 16)
                | ((color.getGreen() & 0xff) << 8) | (color.getBlue() & 0xff);
        return this;
    }

    public TextShape depthTest(boolean depthTest) {
        this.depthTest = depthTest;
        return this;
    }

    public TextShape showBackface(boolean showBackface) {
        this.showBackface = showBackface;
        return this;
    }

    public TextShape showTextBackface(boolean showTextBackface) {
        this.showTextBackface = showTextBackface;
        return this;
    }

    @Override
    protected ScriptPrimitiveShapeType shapeType() {
        return ScriptPrimitiveShapeType.TEXT;
    }

    @Override
    protected DebugShapePayload buildExtra() {
        TextDataPayload payload = new TextDataPayload();
        payload.setText(text);
        payload.setUseRotation(useRotation);
        payload.setBackgroundColor(backgroundColor);
        payload.setDepthTest(depthTest);
        payload.setShowBackface(showBackface);
        payload.setShowTextBackface(showTextBackface);
        return payload;
    }
}
