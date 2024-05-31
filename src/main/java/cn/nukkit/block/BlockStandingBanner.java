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
    public static final BlockProperties $1 = new BlockProperties(STANDING_BANNER, GROUND_SIGN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStandingBanner() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStandingBanner(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.BANNER;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBanner> getBlockEntityClass() {
        return BlockEntityBanner.class;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Banner";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        Block $2 = level.getBlock(this, 0);
        Block $3 = level.getBlock(this, 1);

        if (face == BlockFace.UP) {
            CompassRoseDirection $4 = CompassRoseDirection.from(
                    (int) Math.floor((((player != null ? player.yaw : 0) + 180) * 16 / 360) + 0.5) & 0x0f
            );
            setDirection(direction);
            if (!this.getLevel().setBlock(block, this, true)) {
                return false;
            }
        } else {
            BlockStandingBanner $5 = (BlockStandingBanner) Block.get(BlockID.WALL_BANNER);
            wall.setBlockFace(face);
            if (!this.getLevel().setBlock(block, wall, true)) {
                return false;
            }
        }

        CompoundTag $6 = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                .putInt("Base", item.getDamage() & 0xf);

        Tag $7 = item.getNamedTagEntry("Type");
        if (type instanceof IntTag) {
            nbt.put("Type", type);
        }
        Tag $8 = item.getNamedTagEntry("Patterns");
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
    /**
     * @deprecated 
     */
    
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
    public @NotNull 
    /**
     * @deprecated 
     */
    String getItemId() {
        return ItemID.BANNER;
    }

    @Override
    public Item toItem() {
        BlockEntityBanner $9 = getBlockEntity();
        Item $10 = Item.get(ItemID.BANNER);
        if (banner != null) {
            item.setDamage(banner.getBaseColor() & 0xf);
            int $11 = banner.namedTag.getInt("Type");
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
    /**
     * @deprecated 
     */
    

    public void setDirection(CompassRoseDirection direction) {
        setPropertyValue(GROUND_SIGN_DIRECTION, direction.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return getDirection().getClosestBlockFace();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setDirection(face.getCompassRoseDirection());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntityBanner $12 = getBlockEntity();

            if (blockEntity != null) {
                return blockEntity.getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }
}