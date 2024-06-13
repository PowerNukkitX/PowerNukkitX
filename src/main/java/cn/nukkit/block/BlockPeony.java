package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockPeony extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(PEONY, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPeony() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPeony(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.PAEONIA;
    }
}