package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public abstract class BlockFungus extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    public BlockFungus(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isValidSupport(down())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isValidSupport(down())) {
            level.useBreakOn(this);
            return type;
        }
        
        return 0;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isNull() || !item.isFertilizer()) {
            return false;
        }

        level.addParticle(new BoneMealParticle(this));

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        Block down = down();
        if (!isValidSupport(down)) {
            level.useBreakOn(this);
            return true;
        }
        
        if (!canGrowOn(down)) {
            return true;
        }

        grow(player);
        
        return true;
    }

    protected abstract boolean canGrowOn(Block support);

    protected boolean isValidSupport(@NotNull Block support) {
        return switch (support.getId()) {
            case GRASS_BLOCK, DIRT, PODZOL, FARMLAND, CRIMSON_NYLIUM, WARPED_NYLIUM, SOUL_SOIL, MYCELIUM -> true;
            default -> false;
        };
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public abstract boolean grow(@Nullable Player cause);
}
