package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;

import java.awt.*;

public class TextDebugShape extends DebugShape {
    public TextDebugShape(Vector3f location, Color color, String text, long attachedToEntityId) {
        shapeType = ScriptDebugShapeType.TEXT;
        this.location = location;
        this.color = color;
        this.attachedToEntityId = attachedToEntityId;
        this.text = text;
    }
}
