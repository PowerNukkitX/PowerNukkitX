package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCutCopperSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }
}