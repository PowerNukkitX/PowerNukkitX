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
    public static final BlockProperties PROPERTIES = new BlockProperties(GRINDSTONE, CommonBlockProperties.ATTACHMENT, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrindstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrindstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Grindstone";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockGrindstone());
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            return;
        }
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    public Attachment getAttachmentType() {
        return getPropertyValue(CommonBlockProperties.ATTACHMENT);
    }

    public void setAttachmentType(Attachment attachmentType) {
        setPropertyValue(CommonBlockProperties.ATTACHMENT, attachmentType);
    }

    private boolean isConnectedTo(BlockFace connectedFace, Attachment attachmentType, BlockFace blockFace) {
        BlockFace.Axis faceAxis = connectedFace.getAxis();
        switch (attachmentType) {
            case STANDING -> {
                if (faceAxis == BlockFace.Axis.Y) {
                    return connectedFace == BlockFace.DOWN;
                } else {
                    return false;
                }
            }
            case HANGING -> {
                return connectedFace == BlockFace.UP;
            }
            case SIDE, MULTIPLE -> {
                return connectedFace == blockFace.getOpposite();
            }
        }
        return false;
    }

    @Override
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
    private boolean checkSupport() {
        return switch (getAttachmentType()) {
            case STANDING -> checkSupport(down());
            case HANGING -> checkSupport(up());
            case SIDE -> checkSupport(getSide(getBlockFace().getOpposite()));
            default -> false;
        };
    }

    private boolean checkSupport(Block support) {
        String id = support.getId();
        return !id.equals(AIR) && !id.equals(BUBBLE_COLUMN) && !(support instanceof BlockLiquid);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        Attachment attachmentType = getAttachmentType();
        BlockFace blockFace = getBlockFace();
        boolean south = this.isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace);
        boolean north = this.isConnectedTo(BlockFace.NORTH, attachmentType, blockFace);
        boolean west = this.isConnectedTo(BlockFace.WEST, attachmentType, blockFace);
        boolean east = this.isConnectedTo(BlockFace.EAST, attachmentType, blockFace);
        boolean up = this.isConnectedTo(BlockFace.UP, attachmentType, blockFace);
        boolean down = this.isConnectedTo(BlockFace.DOWN, attachmentType, blockFace);

        double pixels = (2.0 / 16);

        double n = north ? 0 : pixels;
        double s = south ? 1 : 1 - pixels;
        double w = west ? 0 : pixels;
        double e = east ? 1 : 1 - pixels;
        double d = down ? 0 : pixels;
        double u = up ? 1 : 1 - pixels;

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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInHand();
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
