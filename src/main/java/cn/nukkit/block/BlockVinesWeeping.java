package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * Properties and behaviour definitions of the {@link BlockID#WEEPING_VINES} block.
 * @author joserobjr
 */


public class BlockVinesWeeping extends BlockVinesNether {
    /**
     * Increments for every block the weeping vine grows.
     */


    public static final IntBlockProperty WEEPING_VINES_AGE = new IntBlockProperty(
            "weeping_vines_age", false, 25);

    /**
     * Holds the {@code weeping_vines} block property definitions.
     */


    public static final BlockProperties PROPERTIES = new BlockProperties(WEEPING_VINES_AGE);

    /**
     * Creates a {@code weeping_vine} with age {@code 0}.
     */


    public BlockVinesWeeping() {
    }

    /**
     * Creates a {@code weeping_vine} from a meta compatible with {@link #getProperties()}.
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */


    public BlockVinesWeeping(int meta) throws InvalidBlockPropertyMetaException {
        super(meta);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getId() {
        return WEEPING_VINES;
    }

    @Override
    public String getName() {
        return "Weeping Vines";
    }

    @NotNull


    @Override
    public BlockFace getGrowthDirection() {
        return BlockFace.DOWN;
    }


    @Override
    public int getVineAge() {
        return getIntValue(WEEPING_VINES_AGE);
    }


    @Override
    public void setVineAge(int vineAge) {
        setIntValue(WEEPING_VINES_AGE, vineAge);
    }


    @Override
    public int getMaxVineAge() {
        return WEEPING_VINES_AGE.getMaxValue();
    }

}
