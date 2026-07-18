package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityMovingBlock;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockMovingBlock extends BlockTransparent implements BlockEntityHolder<BlockEntityMovingBlock> {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOVING_BLOCK);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .canPassThrough(true)
            .canBePushed(false)
            .canBePulled(false)
            .isSolid(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMovingBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMovingBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "MovingBlock";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.MOVING_BLOCK;
    }

    @Override
    @NotNull public Class<? extends BlockEntityMovingBlock> getBlockEntityClass() {
        return BlockEntityMovingBlock.class;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    
    }