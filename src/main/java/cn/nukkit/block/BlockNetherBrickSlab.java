package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickSlab extends BlockStoneBlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickSlab(BlockState blockstate) {
        super(blockstate==null ? PROPERTIES.getDefaultState(): blockstate);
    }

    @Override
    public StoneSlabType getSlabType() {
        return StoneSlabType.NETHER_BRICK;
    }
}