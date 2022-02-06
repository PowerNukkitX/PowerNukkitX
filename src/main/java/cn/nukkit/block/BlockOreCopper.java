package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.MinecraftItemID;

import javax.annotation.Nullable;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockOreCopper extends BlockOre {
    @PowerNukkitOnly
    @Since("FUTURE")
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

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.RAW_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    protected float getDropMultiplier() {
        return 3;
    }
}
