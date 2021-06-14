package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockOreIronDeepslate extends BlockOreIron {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockOreIronDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_IRON_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Iron Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
