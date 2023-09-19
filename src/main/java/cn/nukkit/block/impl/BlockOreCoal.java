package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockOre;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.MinecraftItemID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "FUTURE", info = "Extends BlockOre instead of BlockSolid only in PowerNukkit")
public class BlockOreCoal extends BlockOre {

    public BlockOreCoal() {
        // Does nothing
    }

    @Override
    public int getId() {
        return COAL_ORE;
    }

    @Override
    public String getName() {
        return "Coal Ore";
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nullable @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.COAL;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3);
    }
}
