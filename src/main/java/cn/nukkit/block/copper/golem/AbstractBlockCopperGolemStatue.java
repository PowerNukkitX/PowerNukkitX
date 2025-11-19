package cn.nukkit.block.copper.golem;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.blockentity.BlockEntityCopperGolemStatue;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.mob.EntityCopperGolem;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.blockentity.BlockEntityCopperGolemStatue.CopperPose;

/**
 * @author keksdev
 * @since 1.21.110
 */
public abstract class AbstractBlockCopperGolemStatue extends BlockTransparent implements Oxidizable, Waxable, Faceable, BlockEntityHolder<BlockEntityCopperGolemStatue> {
    public AbstractBlockCopperGolemStatue(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(item.isAxe() && !isWaxed()) {
            OxidizationLevel oxidizationLevel = getOxidizationLevel();
            if (OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
                CompoundTag nbt = Entity.getDefaultNBT(this.add(0.5, 0, 0.5f));
                EntityCopperGolem copperGolem = (EntityCopperGolem) Entity.createEntity(EntityID.COPPER_GOLEM, this.level.getChunk(this.getChunkX(), this.getChunkZ()), nbt);
                copperGolem.spawnToAll();
                this.level.setBlock(this, BlockAir.STATE.toBlock());
                return true;
            }
        }
        if(player != null && player.getInventory().getItemInHand().isNull()) {
            BlockEntityCopperGolemStatue blockEntity = this.getOrCreateBlockEntity();
            CopperPose[] poses = CopperPose.values();
            blockEntity.setPose(poses[(blockEntity.getPose().ordinal()+1)%poses.length]);
            blockEntity.spawnToAll();
            return true;
        } else return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock();
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER_GOLEM_STATUE : COPPER_GOLEM_STATUE;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_GOLEM_STATUE : EXPOSED_COPPER_GOLEM_STATUE;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_GOLEM_STATUE : WEATHERED_COPPER_GOLEM_STATUE;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_GOLEM_STATUE : OXIDIZED_COPPER_GOLEM_STATUE;
        };
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public @NotNull Class<? extends BlockEntityCopperGolemStatue> getBlockEntityClass() {
        return BlockEntityCopperGolemStatue.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.COPPER_GOLEM_STATUE;
    }
}
