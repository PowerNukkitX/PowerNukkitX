package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * Properties and behaviour definitions of the {@link BlockID#TWISTING_VINES} block.
 * @author joserobjr
 */


public class BlockVinesTwisting extends BlockVinesNether {
    /**
     * Increments for every block the twisting vine grows.
     */


    public static final IntBlockProperty TWISTING_VINES_AGE = new IntBlockProperty(
            "twisting_vines_age", false, 25);

    /**
     * Holds the {@code twisting_vines} block property definitions.
     */


    public static final BlockProperties PROPERTIES = new BlockProperties(TWISTING_VINES_AGE);

    /**
     * Creates a {@code twisting_vine} with age {@code 0}.
     */


    public BlockVinesTwisting() {
        // Does nothing
    }

    /**
     * Creates a {@code twisting_vine} from a meta compatible with {@link #getProperties()}.
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */


    public BlockVinesTwisting(int meta) throws InvalidBlockPropertyMetaException {
        super(meta);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getId() {
        return TWISTING_VINES;
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
        return getIntValue(TWISTING_VINES_AGE);
    }


    @Override
    public void setVineAge(int vineAge) throws InvalidBlockPropertyValueException {
        setIntValue(TWISTING_VINES_AGE, vineAge);
    }


    @Override
    public int getMaxVineAge() {
        return TWISTING_VINES_AGE.getMaxValue();
    }

}
