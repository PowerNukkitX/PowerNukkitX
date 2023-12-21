package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public abstract class BlockStemStripped extends BlockStem {


    public BlockStemStripped(int meta) {
        super(meta);
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
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }
}
