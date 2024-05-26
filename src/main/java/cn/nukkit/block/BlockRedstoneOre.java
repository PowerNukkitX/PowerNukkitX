package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemRedstone;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockRedstoneOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = new Random().nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += new Random().nextInt(fortune.getLevel() + 1);
            }

            return new Item[]{
                    new ItemRedstone(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, getLitBlock(), true, true);
            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.REDSTONE;
    }

    public Block getLitBlock() {
        return new BlockLitRedstoneOre();
    }

    public Block getUnlitBlock() {
        return new BlockRedstoneOre();
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }
}