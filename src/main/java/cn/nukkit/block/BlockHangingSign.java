package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHangingSign;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockHangingSign;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Log4j2
@PowerNukkitXOnly
@Since("1.20.0-r2")
public abstract class BlockHangingSign extends BlockSignBase implements BlockEntityHolder<BlockEntityHangingSign> {
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION,
            CommonBlockProperties.ATTACHED, CommonBlockProperties.HANGING);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHangingSign() {
        this(0);
    }

    public BlockHangingSign(int meta) {
        super(meta);
    }

    @NotNull
    @Override
    public Class<? extends BlockEntityHangingSign> getBlockEntityClass() {
        return BlockEntityHangingSign.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
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
                if (up().getId() == Block.AIR) {
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
        return getBooleanValue(CommonBlockProperties.HANGING);
    }

    public boolean isAttached() {
        return getBooleanValue(CommonBlockProperties.ATTACHED);
    }

    @Override
    public Item toItem() {
        return new ItemBlockHangingSign(this);
    }

    @Override
    public CompassRoseDirection getSignDirection() {
        if (isHanging() && isAttached()) {
            return getPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION);
        } else {
            return getPropertyValue(CommonBlockProperties.FACING_DIRECTION).getCompassRoseDirection();
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
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
            CompassRoseDirection direction = CommonBlockProperties.GROUND_SIGN_DIRECTION.getValueForMeta(
                    (int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f
            );
            if ((player != null && player.isSneaking()) || target instanceof BlockThin || target instanceof BlockChain || target instanceof BlockHangingSign) {
                this.setPropertyValue(CommonBlockProperties.ATTACHED, true);
                this.setPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION, direction);
                getLevel().setBlock(block, this, true);
            } else {
                this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, direction.getClosestBlockFace());
                getLevel().setBlock(block, this, true);
            }
        } else {
            this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.rotateY());
            getLevel().setBlock(block, this, true);
        }
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
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

    @Nullable
    private BlockFace checkGroundBlock() {
        if (getSide(BlockFace.NORTH, 1).canBePlaced()) return BlockFace.NORTH;
        if (getSide(BlockFace.SOUTH, 1).canBePlaced()) return BlockFace.SOUTH;
        if (getSide(BlockFace.WEST, 1).canBePlaced()) return BlockFace.WEST;
        if (getSide(BlockFace.EAST, 1).canBePlaced()) return BlockFace.EAST;
        return null;
    }
}
