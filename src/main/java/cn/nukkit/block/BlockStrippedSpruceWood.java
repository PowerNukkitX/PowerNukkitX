package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedSpruceWood extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_SPRUCE_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedSpruceWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.SPRUCE;
    }
}