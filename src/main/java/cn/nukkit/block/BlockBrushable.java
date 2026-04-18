package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntityBrushable;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Buddelbubi
 * @since 2026/03/31
 */
public abstract class BlockBrushable extends BlockFallable implements BlockEntityHolder<BlockEntityBrushable> {

    public BlockBrushable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        int progress = getPropertyValue(CommonBlockProperties.BRUSHED_PROGRESS);
        if (progress < 3) {
            setPropertyValue(CommonBlockProperties.BRUSHED_PROGRESS, progress + 1);
            getLevel().addSound(this, getHitSound());
            getLevel().setBlock(this, this, false, true);
        } else {
            BlockEntityBrushable brushable = getOrCreateBlockEntity();
            getLevel().dropItem(this.add(HALF), brushable.getItem());
            getLevel().setBlock(this, getFinalState());
            getLevel().addSound(this, getBreakSound());
        }
        return super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    protected EntityFallingBlock createFallingEntity(NbtMap customNbt) {
        customNbt = customNbt.toBuilder().putBoolean("BreakOnGround", true).build();
        return super.createFallingEntity(customNbt);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.AIR};
    }

    public abstract Block getFinalState();

    protected abstract Sound getHitSound();

    protected abstract Sound getBreakSound();

    @Override
    public @NotNull Class<? extends BlockEntityBrushable> getBlockEntityClass() {
        return BlockEntityBrushable.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.BRUSHABLE_BLOCK;
    }
}
