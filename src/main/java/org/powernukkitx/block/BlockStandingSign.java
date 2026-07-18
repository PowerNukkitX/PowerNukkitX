package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySign;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemOakSign;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.CompassRoseDirection;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;

/**
 * The alias is post sign
 */
@Slf4j
public class BlockStandingSign extends BlockSignBase implements BlockEntityHolder<BlockEntitySign> {
    public static final BlockProperties PROPERTIES = new BlockProperties(STANDING_SIGN, GROUND_SIGN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    protected String getStandingSignId() {
        return getId();
    }

    public String getWallSignId() {
        return WALL_SIGN;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntitySign> getBlockEntityClass() {
        return BlockEntitySign.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.SIGN;
    }

    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().isAir()) {
                getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemOakSign();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }
        if (player != null && !player.isSneaking() && target instanceof BlockSignBase) {
            return false;
        }

        Block layer0 = level.getBlock(this, 0);
        Block layer1 = level.getBlock(this, 1);

        CompoundTag nbt = new CompoundTag();

        if (face == BlockFace.UP) {
            CompassRoseDirection direction = CompassRoseDirection.from((int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f);
            Block post = Block.get(getStandingSignId());
            post.setPropertyValue(GROUND_SIGN_DIRECTION, direction.getIndex());
            getLevel().setBlock(block, post);
        } else {
            Block wall = Block.get(getWallSignId());
            wall.setPropertyValue(FACING_DIRECTION, face.getIndex());
            getLevel().setBlock(block, wall);
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
}