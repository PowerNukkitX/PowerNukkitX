package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.item.MinecraftItemID;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockOreGold extends BlockOre {

    public BlockOreGold() {
        // Does nothing
    }

    @Override
    public int getId() {
        return GOLD_ORE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public String getName() {
        return "Gold Ore";
    }


    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.RAW_GOLD;
    }
}
