package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author GoodLucky777
 */


public class BlockDeepslateChiseled extends BlockDeepslateCobbled {


    public BlockDeepslateChiseled() {
        // Does nothing
    }
    
    @Override
    public int getId() {
        return CHISELED_DEEPSLATE;
    }
    
    @Override
    public String getName() {
        return "Chiseled Deepslate";
    }
}
