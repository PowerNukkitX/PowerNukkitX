package cn.nukkit.education.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;

public class BlockCompoundCreator extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COMPOUND_CREATOR, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCompoundCreator() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCompoundCreator(BlockState blockstate) {
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