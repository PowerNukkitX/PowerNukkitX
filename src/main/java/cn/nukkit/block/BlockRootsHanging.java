package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockRootsHanging extends BlockRoots {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRootsHanging() {
    }

    @Override
    public String getName() {
        return "Hanging Roots";
    }

    @Override
    public int getId() {
        return HANGING_ROOTS;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        return isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    protected boolean isSupportValid() {
        if (this.up().isSolid()) {
            return true;
        }
        return false;
    }
}
