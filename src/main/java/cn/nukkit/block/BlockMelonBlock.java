package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMelonSlice;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockMelonBlock extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(MELON_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMelonBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMelonBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Melon Block";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = new Random();
        int count = 3 + random.nextInt(5);

        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                new ItemMelonSlice(0, Math.min(9, count))
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public  boolean sticksToPiston() {
        return false;
    }
}