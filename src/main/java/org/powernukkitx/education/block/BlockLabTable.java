package org.powernukkitx.education.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.DIRECTION;

public class BlockLabTable extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(LAB_TABLE, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLabTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLabTable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection() : BlockFace.SOUTH);
        return this.level.setBlock(block, this, true, true);
    }

    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face.getHorizontalIndex());
    }
}