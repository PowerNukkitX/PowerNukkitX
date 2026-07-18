package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemMelonSlice;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockMelonBlock extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(MELON_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1)
            .resistance(5)
            .toolType(ItemTool.TYPE_AXE)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .canSilkTouch(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMelonBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMelonBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Melon Block";
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

    }