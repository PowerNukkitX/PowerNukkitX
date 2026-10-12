package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitDeepslateRedstoneOre extends BlockDeepslateRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_DEEPSLATE_REDSTONE_ORE);
    public static final BlockDefinition DEFINITION = BlockDeepslateRedstoneOre.DEFINITION.toBuilder()
            .lightEmission(9)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Glowing Deepslate Redstone Ore";
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