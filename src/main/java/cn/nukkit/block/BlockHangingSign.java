package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHangingSign;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Slf4j
public abstract class BlockHangingSign extends BlockSignBase implements BlockEntityHolder<BlockEntityHangingSign> {
    public BlockHangingSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityHangingSign> getBlockEntityClass() {
        return BlockEntityHangingSign.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.HANGING_SIGN;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;//01 23 45
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isHanging()) {
                if (up().isAir()) {
                    getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                if (checkGroundBlock() == null) {
                    getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    public boolean isHanging() {
        return getPropertyValue(CommonBlockProperties.HANGING);
    }

    public boolean isAttached() {
        return getPropertyValue(CommonBlockProperties.ATTACHED_BIT);
    }

    @Override
    public CompassRoseDirection getSignDirection() {
        if (isHanging() && isAttached()) {
            return CompassRoseDirection.from(getPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION));
        } else {
            return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION)).getCompassRoseDirection();
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null && !player.isSneaking() && target instanceof BlockSignBase) {
            return false;
        }
        if (face == BlockFace.UP) {
            BlockFace blockFace = checkGroundBlock();
            if (blockFace == null) {
                return false;
            }
            face = blockFace;
        }
        if (target instanceof BlockHangingSign && face != BlockFace.DOWN) {
            return false;
        }

        Block layer0 = level.getBlock(this, 0);
        Block layer1 = level.getBlock(this, 1);

        CompoundTag nbt = new CompoundTag();

        if (face == BlockFace.DOWN) {
            this.setPropertyValue(CommonBlockProperties.HANGING, true);
            CompassRoseDirection direction = CompassRoseDirection.from(
                    (int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f
            );
            if ((player != null && player.isSneaking()) || target instanceof BlockThin || target instanceof BlockIronChain || target instanceof BlockHangingSign) {
                this.setPropertyValue(CommonBlockProperties.ATTACHED_BIT, true);
                this.setPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION, direction.getIndex());
                getLevel().setBlock(block, this, true);
            } else {
                this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, direction.getClosestBlockFace().getIndex());
                getLevel().setBlock(block, this, true);
            }
        } else {
            this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.rotateY().getIndex());
            getLevel().setBlock(block, this, true);
        }
        if (item.hasCustomBlockData()) {
            for (var e : item.getCustomBlockData().getEntrySet()) {
                nbt.put(e.getKey(), e.getValue());
            }
        }

        try {
            createBlockEntity(nbt);
            if (player != null) {
                player.openSignEditor(this, true);
            }
            return true;
        } catch (Exception e) {
            log.warn("Failed to create block entity {} at {}", getBlockEntityType(), getLocation(), e);
            level.setBlock(layer0, 0, layer0, true);
            level.setBlock(layer1, 0, layer1, true);
            return false;
        }
    }

    private @Nullable BlockFace checkGroundBlock() {
        if (getSide(BlockFace.NORTH, 1).canBePlaced()) return BlockFace.NORTH;
        if (getSide(BlockFace.SOUTH, 1).canBePlaced()) return BlockFace.SOUTH;
        if (getSide(BlockFace.WEST, 1).canBePlaced()) return BlockFace.WEST;
        if (getSide(BlockFace.EAST, 1).canBePlaced()) return BlockFace.EAST;
        return null;
    }
}
