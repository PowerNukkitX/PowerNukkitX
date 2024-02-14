package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull public BlockProperties getProperties() {
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
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
