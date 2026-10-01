package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

public abstract class BlockHanging extends BlockFlowable {
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .burnChance(5)
            .build();

    public BlockHanging(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockHanging(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid()) {
            level.useBreakOn(this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }

    protected boolean isSupportValid() {
        return down().hasTag(BlockTags.DIRT) || switch (down().getId()) {
            case WARPED_NYLIUM, CRIMSON_NYLIUM, SOUL_SOIL -> true;
            default -> false;
        };
    }

    }
