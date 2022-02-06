package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockTilesDeepslateCracked extends BlockTilesDeepslate {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockTilesDeepslateCracked() {
        // Does nothing
    }
    
    @Override
    public int getId() {
        return CRACKED_DEEPSLATE_TILES;
    }
    
    @Override
    public String getName() {
        return "Cracked Deepslate Tiles";
    }
}
