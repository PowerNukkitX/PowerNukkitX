package cn.nukkit.block;


public class BlockMudBrickSlab extends BlockSlab{
    public BlockMudBrickSlab() {
        super(MUD_BRICK_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return MUD_BRICK_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Mud Brick Slab";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }
}
