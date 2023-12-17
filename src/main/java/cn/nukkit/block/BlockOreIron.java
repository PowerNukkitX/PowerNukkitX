package cn.nukkit.block;

import cn.nukkit.item.MinecraftItemID;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */

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


    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.RAW_IRON;
    }
}
