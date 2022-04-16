package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "FUTURE", info = "Extends BlockOre instead of BlockSolid only in PowerNukkit")
public class BlockOreDiamond extends BlockOre {


    public BlockOreDiamond() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DIAMOND_ORE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public String getName() {
        return "Diamond Ore";
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.DIAMOND;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = 1;
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                int i = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = i + 1;
            }

            return new Item[]{
                    new ItemDiamond(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3, 8);
    }
}
