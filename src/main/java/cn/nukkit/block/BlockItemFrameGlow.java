package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockItemFrameGlow extends BlockItemFrame {
    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public BlockItemFrameGlow() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public BlockItemFrameGlow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public int getId() {
        return GLOW_FRAME;
    }
}
