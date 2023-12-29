package cn.nukkit.block;

import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.IBlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockWood extends BlockLog {


    public static final BlockProperties PROPERTIES = new BlockProperties(WoodType.PROPERTY, PILLAR_AXIS);
    
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;


    public BlockWood() {
        this(0);
    }

    public BlockWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return LOG;
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
        return getPropertyValue(WoodType.PROPERTY);
    }


    public void setWoodType(WoodType woodType) {
        setPropertyValue(WoodType.PROPERTY, woodType);
    }
    
    @Override
    public String getName() {
        return getWoodType().getEnglishName() + " Log";
    }


    @NotNull
    @Override
    public Block forState(@NotNull IBlockState state) throws InvalidBlockStateException {
        int id = getId();
        if (id != LOG && id != LOG2) {
            return super.forState(state);
        }

        id = state.getBlockId();
        if (id != LOG && id != LOG2 || state.getBitSize() != 4) {
            return super.forState(state);
        }

        int exactInt = state.getExactIntStorage();
        if ((exactInt & 0b1100) == 0b1100) {
            int increment = state.getBlockId() == BlockID.LOG? 0b000 : 0b100;
            return BlockState.of(BlockID.WOOD_BARK, (exactInt & 0b11) + increment).getBlock(this, layer);
        }

        return super.forState(state);
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
    @
    public BlockState getStrippedState() {
        int strippedId = switch (getWoodType()) {
            case OAK -> STRIPPED_OAK_LOG;
            case SPRUCE -> STRIPPED_SPRUCE_LOG;
            case BIRCH -> STRIPPED_BIRCH_LOG;
            case JUNGLE -> STRIPPED_JUNGLE_LOG;
            case ACACIA -> STRIPPED_ACACIA_LOG;
            case DARK_OAK -> STRIPPED_DARK_OAK_LOG;
        };
        return BlockState.of(strippedId).withProperty(PILLAR_AXIS, getPillarAxis());
    }

}
