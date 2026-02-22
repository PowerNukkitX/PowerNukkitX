package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class SphereDebugShape extends DebugShape {
    public SphereDebugShape(Vector3f location, Color color, float scale, @Nullable Long attachedToEntityId) {
        shapeType = ScriptDebugShapeType.SPHERE;
        this.location = location;
        this.color = color;
        this.attachedToEntityId = attachedToEntityId;
        this.scale = scale;
        this.numSegments = 20;
    }
}
