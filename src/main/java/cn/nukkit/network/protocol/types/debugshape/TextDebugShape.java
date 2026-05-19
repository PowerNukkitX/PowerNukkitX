package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class TextDebugShape extends DebugShape {
    public TextDebugShape(Vector3f location, Color color, String text, @Nullable Long attachedToEntityId,
                          boolean useRotation, Integer backgroundColor, boolean depthTest, boolean showBackface) {
        shapeType = ScriptDebugShapeType.TEXT;
        this.location = location;
        this.color = color;
        this.attachedToEntityId = attachedToEntityId;
        this.text = text;
        this.useRotation = useRotation;
        this.backgroundColor = backgroundColor;
        this.depthTest = depthTest;
        this.showBackface = showBackface;
    }
}
