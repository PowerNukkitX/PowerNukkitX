package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

//todo complete
public class BlockPitcherPlant extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_PLANT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherPlant() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPitcherPlant(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.PITCHER_PLANT;
    }

    public String getName() {
        return "Pitcher Plant";
    }
}