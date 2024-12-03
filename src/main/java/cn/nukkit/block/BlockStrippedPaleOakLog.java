package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedPaleOakLog extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_PALE_OAK_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedPaleOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedPaleOakLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.PALE_OAK;
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedPaleOakLog.PROPERTIES.getDefaultState();
    }
}