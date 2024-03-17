package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public abstract class BlockWoodStripped extends BlockWood {
    public BlockWoodStripped(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    @Override
    public String getName() {
        return "Stripped " + super.getName();
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
