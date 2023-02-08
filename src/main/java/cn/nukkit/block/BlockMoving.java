package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@PowerNukkitOnly
public class BlockMoving extends BlockTransparent implements BlockEntityHolder<BlockEntityMovingBlock> {

    @PowerNukkitOnly
    public BlockMoving() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockMoving(int meta) {
        super();
    }

    @Override
    public String getName() {
        return "MovingBlock";
    }

    @Override
    public int getId() {
        return BlockID.MOVING_BLOCK;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.MOVING_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityMovingBlock> getBlockEntityClass() {
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
    @PowerNukkitOnly
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean isBreakable(Item item) {
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
