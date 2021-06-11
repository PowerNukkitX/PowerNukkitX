package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockRawGold extends BlockRaw {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawGold() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockRawGold(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Block of Raw Gold";
    }

    @Override
    public int getId() {
        return RAW_GOLD_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}
