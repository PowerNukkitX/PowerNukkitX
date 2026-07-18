package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public abstract class BlockWoodStripped extends BlockWood {
    public static final BlockDefinition DEFINITION = BlockLog.DEFINITION.toBuilder()
            .canBeActivated(false)
            .build();
    public BlockWoodStripped(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockWoodStripped(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    @Override
    public String getName() {
        return "Stripped " + super.getName();
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }
}
