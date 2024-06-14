package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockRoseBush extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(ROSE_BUSH, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRoseBush() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockRoseBush(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.ROSE;
    }
}