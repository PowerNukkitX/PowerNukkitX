package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

//todo complete
public class BlockPitcherPlant extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_PLANT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherPlant() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPitcherPlant(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Pitcher Plant";
    }
}