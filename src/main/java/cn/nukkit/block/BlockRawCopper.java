package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockRawCopper extends BlockRaw {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawCopper() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawCopper(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Block of Raw Copper";
    }

    @Override
    public int getId() {
        return RAW_COPPER_BLOCK;
    }
}
