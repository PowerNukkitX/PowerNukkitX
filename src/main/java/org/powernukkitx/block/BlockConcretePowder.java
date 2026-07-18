package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */

public abstract class BlockConcretePowder extends BlockFallable {
    public static final BlockDefinition DEFINITION = FALLABLE.toBuilder()
            .hardness(0.5)
            .resistance(2.5)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();
    public BlockConcretePowder(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockConcretePowder(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    public abstract BlockConcrete getConcrete();

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            super.onUpdate(Level.BLOCK_UPDATE_NORMAL);

            for (int side = 1; side <= 5; side++) {
                Block block = this.getSide(BlockFace.fromIndex(side));
                if (block.getId().equals(Block.FLOWING_WATER) || block.getId().equals(Block.WATER)) {
                    this.level.setBlock(this, getConcrete(), true, true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block b, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean concrete = false;

        for (int side = 1; side <= 5; side++) {
            Block block = this.getSide(BlockFace.fromIndex(side));
            if (block.getId().equals(Block.FLOWING_WATER) || block.getId().equals(Block.WATER)) {
                concrete = true;
                break;
            }
        }

        if (concrete) {
            this.level.setBlock(this, getConcrete(), true, true);
        } else {
            this.level.setBlock(this, this, true, true);
        }

        return true;
    }
}
