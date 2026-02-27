package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class LineDebugShape extends DebugShape {
    public LineDebugShape(Vector3f location, Color color, Vector3f lineEndLocation, @Nullable Long attachedToEntityId) {
        shapeType = ScriptDebugShapeType.LINE;
        this.location = location;
        this.color = color;
        this.attachedToEntityId = attachedToEntityId;
        this.lineEndLocation = lineEndLocation;
    }
}
