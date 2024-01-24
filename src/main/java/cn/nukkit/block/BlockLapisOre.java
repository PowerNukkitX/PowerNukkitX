package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockLapisOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(LAPIS_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLapisOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLapisOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Lapis Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int count = 4 + random.nextInt(5);
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                int i = random.nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count *= (i + 1);
            }
            Item itemRaw = Item.get(ItemID.LAPIS_LAZULI);
            itemRaw.setCount(count);
            return new Item[]{itemRaw};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.LAPIS_LAZULI;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(2, 6);
    }
}