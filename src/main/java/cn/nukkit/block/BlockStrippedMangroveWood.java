package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedMangroveWood extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_MANGROVE_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedMangroveWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedMangroveWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Wood";
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.OAK;
    }
}