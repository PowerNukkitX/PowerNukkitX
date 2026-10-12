package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneOre extends BlockRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_REDSTONE_ORE);
    public static final BlockDefinition DEFINITION = BlockRedstoneOre.DEFINITION.toBuilder()
            .lightEmission(9)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitRedstoneOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Glowing Redstone Ore";
    }

    
    @Override
    public Item toItem() {
        return IBlockOreRedstoneGlowing.super.toItem();
    }

    @Override
    public int onUpdate(int type) {
        return IBlockOreRedstoneGlowing.super.onUpdate(this, type);
    }
}