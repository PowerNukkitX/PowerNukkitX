package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;

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
            if (!this.getLevel().setBlock(block, this, true)) {
                return false;
            }
        } else {
            BlockStandingBanner wall = (BlockStandingBanner) Block.get(BlockID.WALL_BANNER);
            wall.setBlockFace(face);
            if (!this.getLevel().setBlock(block, wall, true)) {
                return false;
            }
        }

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                .putInt("Base", item.getDamage() & 0xf);

        Tag type = item.getNamedTagEntry("Type");
        if (type instanceof IntTag) {
            nbt.put("Type", type);
        }
        Tag patterns = item.getNamedTagEntry("Patterns");
        if (patterns instanceof ListTag) {
            nbt.put("Patterns", patterns);
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
            int type = banner.namedTag.getInt("Type");
            if (type > 0) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putInt("Type", type));
            }
            ListTag<CompoundTag> patterns = banner.namedTag.getList("Patterns", CompoundTag.class);
            if (patterns.size() > 0) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putList("Patterns", patterns));
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