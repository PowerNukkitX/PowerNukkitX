package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockMangroveWood extends BlockWood {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_WOOD, CommonBlockProperties.PILLAR_AXIS, CommonBlockProperties.STRIPPED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveWood() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return (isStripped() ? "Stripped " : "") + "Mangrove Wood";
    }
    /**
     * @deprecated 
     */
    

    public boolean isStripped() {
        return getPropertyValue(CommonBlockProperties.STRIPPED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setStripped(boolean stripped) {
        setPropertyValue(CommonBlockProperties.STRIPPED_BIT, stripped);
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockState getStrippedState() {
        return Registries.BLOCK.getBlockProperties(STRIPPED_MANGROVE_WOOD).getBlockState(PILLAR_AXIS, getPillarAxis());
    }
}