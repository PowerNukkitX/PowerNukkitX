package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BlockBlastFurnace extends BlockLitBlastFurnace {

    public static final BlockProperties PROPERTIES = new BlockProperties(BLAST_FURNACE, MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
