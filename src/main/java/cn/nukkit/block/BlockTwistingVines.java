package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.TWISTING_VINES_AGE;

public class BlockTwistingVines extends BlockVinesNether {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:twisting_vines", TWISTING_VINES_AGE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTwistingVines() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTwistingVines(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Twisting Vines";
    }

    @NotNull


    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.UP;
    }


    @Override
    public int getVineAge() {
        return getPropertyValue(TWISTING_VINES_AGE);
    }


    @Override
    public void setVineAge(int vineAge) {
        setPropertyValue(TWISTING_VINES_AGE, vineAge);
    }


    @Override
    public int getMaxVineAge() {
        return TWISTING_VINES_AGE.getMax();
    }
}