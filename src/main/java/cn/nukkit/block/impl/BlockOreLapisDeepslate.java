package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockOreLapisDeepslate extends BlockOreLapis {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockOreLapisDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_LAPIS_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Lazuli Ore";
    }
}
