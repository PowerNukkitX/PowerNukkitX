package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import org.jetbrains.annotations.NotNull;

public class BlockPetrifiedOakSlab extends BlockStoneBlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(PETRIFIED_OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPetrifiedOakSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPetrifiedOakSlab(BlockState blockstate) {
        super(blockstate==null ? PROPERTIES.getDefaultState(): blockstate);
    }

    @Override
    public StoneSlabType getSlabType() {
        return StoneSlabType.WOOD;
    }
}