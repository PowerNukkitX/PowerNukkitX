package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import org.jetbrains.annotations.NotNull;

public class BlockCobblestoneSlab extends BlockStoneBlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLESTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobblestoneSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockCobblestoneSlab(BlockState blockstate) {
        super(blockstate==null ? PROPERTIES.getDefaultState(): blockstate);
    }

    @Override
    public StoneSlabType getSlabType() {
        return StoneSlabType.COBBLESTONE;
    }
}