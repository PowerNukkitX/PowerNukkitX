package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;

import java.awt.*;

public class CircleDebugShape extends DebugShape {
    public CircleDebugShape(Vector3f location, Color color, float scale) {
        shapeType = ScriptDebugShapeType.CIRCLE;
        this.location = location;
        this.color = color;
        this.scale = scale;
        this.numSegments = 20;
    }
}
