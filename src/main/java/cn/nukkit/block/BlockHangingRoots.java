package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockHangingRoots extends BlockHanging {
    public static final BlockProperties PROPERTIES = new BlockProperties(HANGING_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHangingRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHangingRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    protected boolean isSupportValid() {
        return this.up().isSolid();
    }
}