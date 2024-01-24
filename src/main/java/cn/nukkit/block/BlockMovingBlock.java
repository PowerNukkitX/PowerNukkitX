package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockMovingBlock extends BlockTransparent implements BlockEntityHolder<BlockEntityMovingBlock> {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOVING_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMovingBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMovingBlock(BlockState blockstate) {
        super(blockstate);
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
    public boolean canBePushed() {
        return false;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}