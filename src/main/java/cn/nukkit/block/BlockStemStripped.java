package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public abstract class BlockStemStripped extends BlockStem {

    public BlockStemStripped(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }
}
