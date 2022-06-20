package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemItemFrameGlow;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockItemFrameGlow extends BlockItemFrame {
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public BlockItemFrameGlow() {
        this(0);
    }

    @PowerNukkitXOnly
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

    @Override
    public Item toItem() {
        return new ItemItemFrameGlow();
    }
}
