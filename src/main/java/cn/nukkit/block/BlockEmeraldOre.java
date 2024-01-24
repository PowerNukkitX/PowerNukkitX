package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEmerald;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockEmeraldOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(EMERALD_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEmeraldOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Emerald Ore";
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.EMERALD;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
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
                    new ItemEmerald(0, count)
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