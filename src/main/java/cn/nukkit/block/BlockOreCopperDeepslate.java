package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockOreCopperDeepslate extends BlockOreCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockOreCopperDeepslate() {

    }

    @Override
    public String getName() {
        return "Deepslate Copper Ore";
    }

    @Override
    public int getId() {
        return DEEPSLATE_COPPER_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}
