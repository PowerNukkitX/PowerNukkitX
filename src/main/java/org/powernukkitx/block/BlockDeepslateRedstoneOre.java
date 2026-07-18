package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateRedstoneOre extends BlockRedstoneOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_REDSTONE_ORE);
    public static final BlockDefinition DEFINITION = BlockRedstoneOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockDeepslateRedstoneOre(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    
    @Override
    public Block getLitBlock() {
        return new BlockLitDeepslateRedstoneOre();
    }

    @Override
    public Block getUnlitBlock() {
        return new BlockDeepslateRedstoneOre();
    }
}