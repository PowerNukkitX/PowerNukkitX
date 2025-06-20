package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;

import java.awt.*;

public class SphereDebugShape extends DebugShape {
    public SphereDebugShape(Vector3f location, Color color, float scale) {
        shapeType = ScriptDebugShapeType.SPHERE;
        this.location = location;
        this.color = color;
        this.scale = scale;
        this.numSegments = 20;
    }
}
