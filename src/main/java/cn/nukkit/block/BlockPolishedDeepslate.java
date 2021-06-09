package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockPolishedDeepslate extends BlockCobbledDeepslate {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockPolishedDeepslate() {
    }

    @Override
    public String getName() {
        return "Polished Deepslate";
    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE;
    }
}
