package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

public class BlockAmethystCluster extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(AMETHYST_CLUSTER, CommonBlockProperties.MINECRAFT_BLOCK_FACE);

    public BlockAmethystCluster() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAmethystCluster(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    private static final NukkitRandom RANDOM = new NukkitRandom();

    @Override
    protected String getNamePrefix() {
        return "Cluster";
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

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
                    if (RANDOM.nextInt(3) == 0) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 8)};
                    } else {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 4)};
                    }
                case 2:
                    final int bound = RANDOM.nextInt(4);
                    if (bound == 0) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 12)};
                    } else if (bound == 1) {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 8)};
                    } else  {
                        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 4)};
                    }
                case 3:
                    final int bound2 = RANDOM.nextInt(5);
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
