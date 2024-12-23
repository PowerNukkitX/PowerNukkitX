package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockCherryWood extends BlockWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Wood";
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.CHERRY;
    }
}