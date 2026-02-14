package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;

import java.awt.*;

public class BoxDebugShape extends DebugShape {
    public BoxDebugShape(Vector3f location, Color color, float scale, Vector3f boxBounds, long attachedToEntityId) {
        shapeType = ScriptDebugShapeType.BOX;
        this.location = location;
        this.color = color;
        this.attachedToEntityId = attachedToEntityId;
        this.scale = scale;
        this.boxBound = boxBounds;
    }
}
