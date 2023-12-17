package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author GoodLucky777
 */


public class BlockBricksDeepslateCracked extends BlockBricksDeepslate {


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
