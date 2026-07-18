package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BlockChippedAnvil extends BlockAnvil {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHIPPED_ANVIL, MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChippedAnvil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChippedAnvil(BlockState blockstate) {
        super(blockstate);
    }

}
