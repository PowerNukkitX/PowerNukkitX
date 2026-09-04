package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public abstract class BlockStemStripped extends BlockStem {
    public static final BlockDefinition DEFINITION = BlockStem.DEFINITION.toBuilder()
            .canBeActivated(false)
            .build();

    public BlockStemStripped(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockStemStripped(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }
}
