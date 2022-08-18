package cn.nukkit.level.generator.populator.impl.structure.village.block.state;

//\\ VanillaStates::TorchFacingDirection
public final class TorchFacingDirection {

    public static final int UNKNOWN = 0b000;
    public static final int EAST = 0b001; //attached to a block to its WEST
    public static final int WEST = 0b010; //attached to a block to its EAST
    public static final int SOUTH = 0b011; //attached to a block to its NORTH
    public static final int NORTH = 0b100; //attached to a block to its SOUTH
    public static final int TOP = 0b101;

    private TorchFacingDirection() {

    }
}
