package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitRandom;

public class BlockAmethystCluster extends BlockAmethystBud {
    private static final NukkitRandom RANDOM = new NukkitRandom();

    @Override
    protected String getNamePrefix() {
        return "Cluster";
    }

    @Override
    public int getId() {
        return AMETHYST_CLUSTER;
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if(item.isPickaxe()){
            final int fortuneLvl = item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING);
            switch (fortuneLvl) {
                case 1:
                    if (RANDOM.nextBoundedInt(3) == 0) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 8)};
                    } else {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 4)};
                    }
                case 2:
                    final int bound = RANDOM.nextBoundedInt(4);
                    if (bound == 0) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 12)};
                    } else if (bound == 1) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 8)};
                    } else  {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 4)};
                    }
                case 3:
                    final int bound2 = RANDOM.nextBoundedInt(5);
                    if (bound2 == 0) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 16)};
                    } else if (bound2 == 1) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 12)};
                    } else if (bound2 == 2) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 8)};
                    }
                default:
                    return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 4)};
            }
        } else {
            return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 2)};
        }
    }
}
