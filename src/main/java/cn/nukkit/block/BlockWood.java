package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;
import static cn.nukkit.block.property.CommonBlockProperties.WOOD_TYPE;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockWood extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOOD, PILLAR_AXIS, CommonBlockProperties.STRIPPED_BIT, WOOD_TYPE);
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;


    public BlockWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWood(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }


    public WoodType getWoodType() {
        return getPropertyValue(WOOD_TYPE);
    }


    public void setWoodType(WoodType woodType) {
        setPropertyValue(WOOD_TYPE, woodType);
    }

    @Override
    public String getName() {
        return getWoodType().name() + " Wood";
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }


    @Override
    public BlockState getStrippedState() {
        String strippedId = switch (getWoodType()) {
            case OAK -> STRIPPED_OAK_LOG;
            case SPRUCE -> STRIPPED_SPRUCE_LOG;
            case BIRCH -> STRIPPED_BIRCH_LOG;
            case JUNGLE -> STRIPPED_JUNGLE_LOG;
            case ACACIA -> STRIPPED_ACACIA_LOG;
            case DARK_OAK -> STRIPPED_DARK_OAK_LOG;
        };
        return Registries.BLOCK.getBlockProperties(strippedId).getBlockState(PILLAR_AXIS, getPillarAxis());
    }
}
