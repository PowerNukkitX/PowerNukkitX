package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockRaw;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockRawIron extends BlockRaw {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawIron() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawIron(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Block of Raw Iron";
    }

    @Override
    public int getId() {
        return RAW_IRON_BLOCK;
    }
}
