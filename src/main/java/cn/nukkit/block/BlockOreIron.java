package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.MinecraftItemID;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "FUTURE", info = "Extends BlockOre instead of BlockSolid only in PowerNukkit")
public class BlockOreIron extends BlockOre {

    public BlockOreIron() {
        // Does nothing
    }

    @Override
    public int getId() {
        return IRON_ORE;
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.RAW_IRON;
    }
}
