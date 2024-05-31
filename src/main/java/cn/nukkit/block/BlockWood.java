package cn.nukkit.block;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class BlockWood extends BlockLog {
    /**
     * @deprecated 
     */
    
    public BlockWood(BlockState blockstate) {
        super(blockstate);
    }

    public abstract WoodType getWoodType();

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getWoodType().name() + " Wood";
    }

    @Override
    public BlockState getStrippedState() {
        String $1 = switch (getWoodType()) {
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
