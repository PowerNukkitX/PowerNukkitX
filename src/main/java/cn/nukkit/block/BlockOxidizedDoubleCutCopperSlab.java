package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedDoubleCutCopperSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }
}