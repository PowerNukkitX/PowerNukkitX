package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BlockDamagedAnvil extends BlockAnvil {
    public static final BlockProperties PROPERTIES = new BlockProperties(DAMAGED_ANVIL, MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDamagedAnvil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDamagedAnvil(BlockState blockstate) {
        super(blockstate);
    }

}
