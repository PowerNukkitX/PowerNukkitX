package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockLilac extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(LILAC, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLilac() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockLilac(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.SYRINGA;
    }
}