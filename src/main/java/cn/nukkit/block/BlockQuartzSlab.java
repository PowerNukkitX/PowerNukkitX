package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import org.jetbrains.annotations.NotNull;

public class BlockQuartzSlab extends BlockStoneBlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(QUARTZ_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockQuartzSlab(BlockState blockstate) {
        super(blockstate==null ? PROPERTIES.getDefaultState(): blockstate);
    }

    @Override
    public StoneSlabType getSlabType() {
        return StoneSlabType.QUARTZ;
    }
}