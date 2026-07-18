package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityBanner;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.CompassRoseDirection;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.Faceable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;

@Slf4j
public class BlockStandingBanner extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityBanner> {
    public static final BlockProperties PROPERTIES = new BlockProperties(STANDING_BANNER, GROUND_SIGN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStandingBanner() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStandingBanner(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.BANNER;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBanner> getBlockEntityClass() {
        return BlockEntityBanner.class;
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
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        Block layer0 = level.getBlock(this, 0);
        Block layer1 = level.getBlock(this, 1);

        if (face == BlockFace.UP) {
            CompassRoseDirection direction = CompassRoseDirection.from(
                    (int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f
            );
            setDirection(direction);
            if (!this.getLevel().setBlock(block, this, false)) {
                return false;
            }
        } else {
            BlockStandingBanner wall = (BlockStandingBanner) Block.get(BlockID.WALL_BANNER);
            wall.setBlockFace(face);
            if (!this.getLevel().setBlock(block, wall, false)) {
                return false;
            }
        }

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                .putInt("Base", item.getDamage() & 0xf);

        Object type = item.getNbtEntry("Type");
        if (type instanceof IntTag tag) {
            nbt.put("Type", tag.copy());
        }
        Object patterns = item.getNbtEntry("Patterns");
        if (patterns instanceof ListTag<?> tag) {
            nbt.put("Patterns", tag.copy());
        }

        try {
            createBlockEntity(nbt);
            return true;
        } catch (Exception e) {
            log.error("Failed to create the block entity {} at {}", getBlockEntityType(), getLocation(), e);
            level.setBlock(layer0, 0, layer0, true);
            level.setBlock(layer0, 1, layer1, true);
            return false;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == BlockID.AIR) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public @NotNull String getItemId() {
        return ItemID.BANNER;
    }

    @Override
    public Item toItem() {
        BlockEntityBanner banner = getBlockEntity();
        Item item = Item.get(ItemID.BANNER);
        if (banner != null) {
            item.setDamage(banner.getBaseColor() & 0xf);
            int type = banner.getNbt().getInt("Type");
            if (type > 0) {
                item.setNbt((item.hasNbt() ? item.getNbt().copy() : new CompoundTag())
                        .putInt("Type", type));
            }
            ListTag<?> patterns = banner.getNbt().getList("Patterns");
            if (patterns.size() > 0) {
                item.setNbt((item.hasNbt() ? item.getNbt().copy() : new CompoundTag())
                        .putList("Patterns", (ListTag<?>) patterns.copy()));
            }
        }
        return item;
    }

    public CompassRoseDirection getDirection() {
        return CompassRoseDirection.from(getPropertyValue(GROUND_SIGN_DIRECTION));
    }

    public void setDirection(CompassRoseDirection direction) {
        setPropertyValue(GROUND_SIGN_DIRECTION, direction.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return getDirection().getClosestBlockFace();
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setDirection(face.getCompassRoseDirection());
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntityBanner blockEntity = getBlockEntity();

            if (blockEntity != null) {
                return blockEntity.getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
