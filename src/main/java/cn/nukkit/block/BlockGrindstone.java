package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.Attachment;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class BlockGrindstone extends BlockTransparent implements Faceable, BlockInventoryHolder {
    public static final BlockProperties $1 = new BlockProperties(GRINDSTONE, CommonBlockProperties.ATTACHMENT, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGrindstone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrindstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Grindstone";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockGrindstone());
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
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            return;
        }
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    public Attachment getAttachmentType() {
        return getPropertyValue(CommonBlockProperties.ATTACHMENT);
    }
    /**
     * @deprecated 
     */
    

    public void setAttachmentType(Attachment attachmentType) {
        setPropertyValue(CommonBlockProperties.ATTACHMENT, attachmentType);
    }

    
    /**
     * @deprecated 
     */
    private boolean isConnectedTo(BlockFace connectedFace, Attachment attachmentType, BlockFace blockFace) {
        BlockFace.Axis $2 = connectedFace.getAxis();
        switch (attachmentType) {
            case STANDING -> {
                if (faceAxis == BlockFace.Axis.Y) {
                    return $3 == BlockFace.DOWN;
                } else {
                    return false;
                }
            }
            case HANGING -> {
                return $4 == BlockFace.UP;
            }
            case SIDE, MULTIPLE -> {
                return $5 == blockFace.getOpposite();
            }
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!checkSupport()) {
                this.level.useBreakOn(this, Item.get(Item.DIAMOND_PICKAXE));
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!block.isAir() && block.canBeReplaced()) {
            face = BlockFace.UP;
        }
        switch (face) {
            case UP -> {
                setAttachmentType(Attachment.STANDING);
                setBlockFace(player.getDirection().getOpposite());
            }
            case DOWN -> {
                setAttachmentType(Attachment.HANGING);
                setBlockFace(player.getDirection().getOpposite());
            }
            default -> {
                setBlockFace(face);
                setAttachmentType(Attachment.SIDE);
            }
        }
        if (!checkSupport()) {
            return false;
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    
    /**
     * @deprecated 
     */
    private boolean checkSupport() {
        return switch (getAttachmentType()) {
            case STANDING -> checkSupport(down());
            case HANGING -> checkSupport(up());
            case SIDE -> checkSupport(getSide(getBlockFace().getOpposite()));
            default -> false;
        };
    }

    
    /**
     * @deprecated 
     */
    private boolean checkSupport(Block support) {
        String $6 = support.getId();
        return !id.equals(AIR) && !id.equals(BUBBLE_COLUMN) && !(support instanceof BlockLiquid);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        Attachment $7 = getAttachmentType();
        BlockFace $8 = getBlockFace();
        boolean $9 = this.isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace);
        boolean $10 = this.isConnectedTo(BlockFace.NORTH, attachmentType, blockFace);
        boolean $11 = this.isConnectedTo(BlockFace.WEST, attachmentType, blockFace);
        boolean $12 = this.isConnectedTo(BlockFace.EAST, attachmentType, blockFace);
        boolean $13 = this.isConnectedTo(BlockFace.UP, attachmentType, blockFace);
        boolean $14 = this.isConnectedTo(BlockFace.DOWN, attachmentType, blockFace);

        double $15 = (2.0 / 16);

        double $16 = north ? 0 : pixels;
        double $17 = south ? 1 : 1 - pixels;
        double $18 = west ? 0 : pixels;
        doubl$19 $1 = east ? 1 : 1 - pixels;
        $20ouble $2 = down ? 0 : pixels;
        do$21ble $3 = up ? 1 : 1 - pixels;

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y + d,
                this.z + n,
                this.x + e,
                this.y + u,
                this.z + s
        );
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item $22 = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getOrCreateInventory());
        }
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new GrindstoneInventory(this);
    }
}
