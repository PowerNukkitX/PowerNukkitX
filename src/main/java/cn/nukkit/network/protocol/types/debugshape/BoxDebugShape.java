package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;

import java.awt.*;

public class BoxDebugShape extends DebugShape {
    public BoxDebugShape(Vector3f location, Color color, float scale, Vector3f boxBounds) {
        shapeType = ScriptDebugShapeType.BOX;
        this.location = location;
        this.color = color;
        this.scale = scale;
        this.boxBound = boxBounds;
    }
}
