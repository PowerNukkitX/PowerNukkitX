package cn.nukkit.network.protocol.types;

public enum ScriptDebugShapeType {
    LINE(4),
    BOX(3),
    SPHERE(5),
    CIRCLE(5),
    TEXT(2),
    ARROW(1);

    public int payloadType;

    ScriptDebugShapeType(int payloadType) {
        this.payloadType = payloadType;
    }
}
