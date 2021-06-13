package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

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
}
