package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockHead extends BlockTransparent implements RedstoneComponent, BlockEntityHolder<BlockEntitySkull>, Faceable {

    public BlockHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.SKULL;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntitySkull> getBlockEntityClass() {
        return BlockEntitySkull.class;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        if (player == null)
            return false;

        setBlockFace(face);
        CompoundTag nbt = new CompoundTag()
                .putByte("SkullType", item.getDamage())
                .putByte("Rot", (int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (var e : item.getCustomBlockData().getEntrySet()) {
                nbt.put(e.getKey(), e.getValue());
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
        // TODO: 2016/2/3 SPAWN WITHER
    }

    @Override
    public int onUpdate(int type) {
        if ((type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_NORMAL) || !level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return 0;
        }

        BlockEntitySkull entity = getBlockEntity();
        if (entity == null || entity.namedTag.getByte("SkullType") != 5) {
            return 0;
        }

        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        entity.setMouthMoving(this.isGettingPower());
        return Level.BLOCK_UPDATE_REDSTONE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(this.x + 0.25, this.y, this.z + 0.25, this.x + 1 - 0.25, this.y + 0.5, this.z + 1 - 0.25);
        return switch (this.getBlockFace()) {
            case NORTH -> bb.offset(0, 0.25, 0.25);
            case SOUTH -> bb.offset(0, 0.25, -0.25);
            case WEST -> bb.offset(0.25, 0.25, 0);
            case EAST -> bb.offset(-0.25, 0.25, 0);
            default -> bb;
        };
    }


}
