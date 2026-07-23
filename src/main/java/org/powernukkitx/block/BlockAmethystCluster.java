package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

public class BlockAmethystCluster extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(AMETHYST_CLUSTER, CommonBlockProperties.MINECRAFT_BLOCK_FACE);
    public static final BlockDefinition DEFINITION = BlockAmethystBud.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_WOODEN)
            .lightEmission(5)
            .build();

    public BlockAmethystCluster() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAmethystCluster(BlockState blockState) {
        super(blockState, DEFINITION);
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

    private static final int[][] FORTUNE_DROPS = {
            {4},
            {8, 4, 4},
            {12, 8, 4, 4},
            {16, 12, 8, 4, 4}
    };

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe()) {
            return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 2)};
        }

        int fortuneLevel = item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING);
        fortuneLevel = Math.max(0, Math.min(3, fortuneLevel));

        int[] choices = FORTUNE_DROPS[fortuneLevel];
        int amount = choices[RANDOM.nextBoundedInt(choices.length - 1)];

        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, amount)};
    }
}
