package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.impl.BlockBricksDeepslate;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockBricksDeepslateCracked extends BlockBricksDeepslate {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockBricksDeepslateCracked() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRACKED_DEEPSLATE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Bricks";
    }
}
