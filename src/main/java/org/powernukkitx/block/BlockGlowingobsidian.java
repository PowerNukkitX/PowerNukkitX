package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockGlowingobsidian extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLOWINGOBSIDIAN);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(50)
            .resistance(6000)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .lightEmission(12)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowingobsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGlowingobsidian(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Glowing Obsidian";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.OBSIDIAN));
    }

    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() > ItemTool.TIER_DIAMOND) {
            return new Item[] { toItem() };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    }