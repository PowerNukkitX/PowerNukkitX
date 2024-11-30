package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockMangroveWood extends BlockWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Wood";
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }
}