package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockCherryWood extends BlockWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_WOOD, CommonBlockProperties.PILLAR_AXIS, CommonBlockProperties.STRIPPED_BIT);

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
        return (isStripped() ? "Stripped " : "") + "Cherry Wood";
    }

    public boolean isStripped() {
        return getPropertyValue(CommonBlockProperties.STRIPPED_BIT);
    }

    public void setStripped(boolean stripped) {
        setPropertyValue(CommonBlockProperties.STRIPPED_BIT, stripped);
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockState getStrippedState() {
        return Registries.BLOCK.getBlockProperties(STRIPPED_CHERRY_WOOD).getBlockState(PILLAR_AXIS, getPillarAxis());
    }
}