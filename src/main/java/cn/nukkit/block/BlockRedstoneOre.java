package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockRedstoneOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:redstone_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = new Random().nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += new Random().nextInt(fortune.getLevel() + 1);
            }
            Item itemRaw = Item.get(ItemID.REDSTONE);
            itemRaw.setCount(count);
            return new Item[]{itemRaw};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Nullable
    @Override
    protected String getRawMaterial() {
        return ItemID.REDSTONE;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }
}