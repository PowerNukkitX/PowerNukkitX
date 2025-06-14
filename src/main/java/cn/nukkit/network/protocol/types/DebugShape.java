package cn.nukkit.network.protocol.types;

import cn.nukkit.math.Vector3f;

import java.awt.*;

public class DebugShape {
    public long networkId;
    public ScriptDebugShapeType shapeType;
    public Vector3f location;
    public Float scale;
    public Vector3f rotation;
    public Float totalTimeLeft;
    public Color color;
    public String text;
    public Vector3f boxBound;
    public Vector3f lineEndLocation;
    public Float arrowHeadLength;
    public Float arrowHeadRadius;
    public Integer numSegments;
}
