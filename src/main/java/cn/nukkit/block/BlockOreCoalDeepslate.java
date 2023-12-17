package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public class BlockOreCoalDeepslate extends BlockOreCoal {


    public BlockOreCoalDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_COAL_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Coal Ore";
    }

}
