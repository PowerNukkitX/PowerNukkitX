package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSign;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.math.CompassRoseDirection.*;

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
public class BlockSignPost extends BlockSignBase implements BlockEntityHolder<BlockEntitySign> {
    @Deprecated(since = "1.20.0-r2", forRemoval = true)
    @DeprecationDetails(since = "1.20.0-r2", reason = "replace to CommonBlockProperties")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<CompassRoseDirection> GROUND_SIGN_DIRECTION = new ArrayBlockProperty<>("ground_sign_direction", false, new CompassRoseDirection[]{
            SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST,
            WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST,
            NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST,
            EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST
    }).ordinal(true);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.GROUND_SIGN_DIRECTION);

    public BlockSignPost() {
        this(0);
    }

    public BlockSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SIGN_POST;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntitySign> getBlockEntityClass() {
        return BlockEntitySign.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SIGN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Sign Post";
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @PowerNukkitOnly
    protected int getPostId() {
        return getId();
    }

    @PowerNukkitOnly
    public int getWallId() {
        return WALL_SIGN;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == Block.AIR) {
                getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemSign();
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
            CompassRoseDirection direction = CommonBlockProperties.GROUND_SIGN_DIRECTION.getValueForMeta(
                    (int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f
            );

            BlockState post = BlockState.of(getPostId()).withProperty(CommonBlockProperties.GROUND_SIGN_DIRECTION, direction);
            getLevel().setBlock(block, post.getBlock(block), true);
        } else {
            BlockState wall = BlockState.of(getWallId()).withProperty(FACING_DIRECTION, face);
            getLevel().setBlock(block, wall.getBlock(block), true);
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
}
