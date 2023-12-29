package cn.nukkit.block;

/**
 * @author CreeperFace
 */

public class BlockPiston extends BlockPistonBase {

    public BlockPiston() {
        this(0);
    }

    public BlockPiston(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return PISTON;
    }

    @Override
    public String getName() {
        return "Piston";
    }


    @Override
    public int getPistonHeadBlockId() {
        return PISTON_ARM_COLLISION;
    }
}
