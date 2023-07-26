package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

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
