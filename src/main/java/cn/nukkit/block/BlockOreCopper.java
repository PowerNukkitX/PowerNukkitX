package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.MinecraftItemID;

import javax.annotation.Nullable;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */


public class BlockOreCopper extends BlockOre {


    public BlockOreCopper() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Copper Ore";
    }

    @Override
    public int getId() {
        return COPPER_ORE;
    }


    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.RAW_COPPER;
    }


    @Override
    protected float getDropMultiplier() {
        return 3;
    }
}
