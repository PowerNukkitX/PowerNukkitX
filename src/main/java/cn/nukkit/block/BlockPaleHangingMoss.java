package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockPaleHangingMoss extends BlockHanging {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_HANGING_MOSS, CommonBlockProperties.TIP);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleHangingMoss() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleHangingMoss(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    protected boolean isSupportValid() {
        return this.up().isSolid() || this.up() instanceof BlockPaleHangingMoss;
    }
}