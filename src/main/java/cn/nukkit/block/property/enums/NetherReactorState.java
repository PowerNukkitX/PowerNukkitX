package cn.nukkit.block.property.enums;

public enum NetherReactorState {
    READY,

    INITIALIZED,

    FINISHED;
    
    private static final NetherReactorState[] values = values();
    
    public static NetherReactorState getFromData(int data) {
        return values[data];
    }
}
