package cn.nukkit.block;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.registry.Registries;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class BlockWood extends BlockLog {
    public BlockWood(BlockState blockstate) {
        super(blockstate);
    }

    public abstract WoodType getWoodType();

    @Override
    public String getName() {
        return getWoodType().getName() + " Wood";
    }

    @Override
    public BlockState getStrippedState() {
        String strippedId = switch (getWoodType()) {
            case OAK -> STRIPPED_OAK_WOOD;
            case SPRUCE -> STRIPPED_SPRUCE_WOOD;
            case BIRCH -> STRIPPED_BIRCH_WOOD;
            case JUNGLE -> STRIPPED_JUNGLE_WOOD;
            case ACACIA -> STRIPPED_ACACIA_WOOD;
            case DARK_OAK -> STRIPPED_DARK_OAK_WOOD;
            case PALE_OAK -> STRIPPED_PALE_OAK_WOOD;
            case CHERRY -> STRIPPED_CHERRY_WOOD;
        };
        return Registries.BLOCK.getBlockProperties(strippedId).getBlockState(PILLAR_AXIS, getPillarAxis());
    }
}
