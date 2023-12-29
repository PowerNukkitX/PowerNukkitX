package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;

/**
 * @author Pub4Game
 * @since 03.01.2016
 */

public class BlockBarrier extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BARRIER);

    public BlockBarrier() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBarrier(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Barrier";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
