package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;

public class ArrowDebugShape extends DebugShape {
    public ArrowDebugShape(Vector3f location, Color color, Vector3f lineEndLocation, float arrowHeadLength, float arrowHeadRadius) {
        shapeType = ScriptDebugShapeType.ARROW;
        this.location = location;
        this.color = color;
        this.lineEndLocation = lineEndLocation;
        this.arrowHeadLength = arrowHeadLength;
        this.arrowHeadRadius = arrowHeadRadius;
        this.numSegments = 4;
    }
}
