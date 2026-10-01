package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySkull;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockHead extends BlockTransparent implements RedstoneComponent, BlockEntityHolder<BlockEntitySkull>, Faceable {
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(1)
            .resistance(5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .isSolid(false)
            .canBeFlowedInto(true)
            .waterloggingLevel(1)
            .build();

    public BlockHead(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockHead(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
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
    public boolean isSolid(BlockFace side) {
        return false;
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
                .putByte("SkullType", (byte) item.getDamage())
                .putByte("Rot", (byte) ((int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f));
        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public int onUpdate(int type) {
        if ((type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_NORMAL) || !level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return 0;
        }

        BlockEntitySkull entity = getBlockEntity();
        if (entity == null || entity.getNbt().getByte("SkullType") != 5) {
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
