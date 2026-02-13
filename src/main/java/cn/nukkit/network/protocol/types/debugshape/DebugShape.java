package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import lombok.Getter;

import java.awt.*;

@Getter
public class DebugShape {
    public long networkId;
    public Integer dimension;
    public ScriptDebugShapeType shapeType;
    public Vector3f location;
    public Float scale;
    public Vector3f rotation;
    public Float totalTimeLeft;
    public Color color;
    public Long attachedToEntityId;
    public String text;
    public Vector3f boxBound;
    public Vector3f lineEndLocation;
    public Float arrowHeadLength;
    public Float arrowHeadRadius;
    public Integer numSegments;
}
