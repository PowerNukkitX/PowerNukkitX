package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockGlassTinted extends BlockGlass {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockGlassTinted() {

    }

    @Override
    public String getName() {
        return "Tinted Glass";
    }

    @Override
    public int getId() {
        return TINTED_GLASS;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { toItem() };
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}
