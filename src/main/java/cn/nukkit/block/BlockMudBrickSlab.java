package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickSlab extends BlockSlab{

    public static final BlockProperties PROPERTIES = new BlockProperties(MUD_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBrickSlab(BlockState blockState) {
        super(blockState, MUD_BRICK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Mud Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }
}
