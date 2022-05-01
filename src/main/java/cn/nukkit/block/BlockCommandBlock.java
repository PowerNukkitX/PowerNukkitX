package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static cn.nukkit.blockentity.ICommandBlock.MODE_REPEATING;
import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

//special thanks to wode
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockCommandBlock  extends BlockSolidMeta implements Faceable, BlockEntityHolder<BlockEntityCommandBlock>{

    public static final BooleanBlockProperty CONDITIONAL_BIT = new BooleanBlockProperty("conditional_bit", false);
    public static final BlockProperties PROPERTIES = new BlockProperties(CONDITIONAL_BIT, CommonBlockProperties.FACING_DIRECTION);

    public BlockCommandBlock() {
        this(0);
    }

    public BlockCommandBlock(int meta) {
        super(meta);
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getId() {
        return BlockID.COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Impulse Command Block";
    }

    @Override
    public double getResistance() {
        return 6000000;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(get(AIR));
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) < 2 && Math.abs(player.getFloorZ() - this.z) < 2) {
                double y = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    this.setPropertyValue(FACING_DIRECTION, BlockFace.UP);
                } else if (this.y - y > 0) {
                    this.setPropertyValue(FACING_DIRECTION, BlockFace.DOWN);
                } else {
                    this.setPropertyValue(FACING_DIRECTION, player.getHorizontalFacing().getOpposite());
                }
            } else {
                this.setPropertyValue(FACING_DIRECTION, player.getHorizontalFacing().getOpposite());
            }
        } else {
            this.setPropertyValue(FACING_DIRECTION, BlockFace.DOWN);
        }
        this.getLevel().setBlock(block, this, true);
        this.createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntityCommandBlock tile = this.getBlockEntity();
            tile.spawnTo(player);
            player.addWindow(tile.getInventory());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            BlockEntityCommandBlock tile = this.getBlockEntity();
            if (this.isGettingPower()) {
                if (!tile.isPowered()) {
                    tile.setPowered();
                    tile.trigger();
                }
            } else {
                tile.setPowered(false);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return Math.min(this.getBlockEntity().getSuccessCount(), 0xf);
    }

    public BlockEntityCommandBlock getBlockEntity() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityCommandBlock) {
            return (BlockEntityCommandBlock) blockEntity;
        }
        return this.createBlockEntity(null);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityCommandBlock> getBlockEntityClass() {
        return BlockEntityCommandBlock.class;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.COMMAND_BLOCK;
    }

    protected BlockEntityCommandBlock createBlockEntity(Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.COMMAND_BLOCK);
        if (item != null) {
            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }
            if (item.hasCustomBlockData()) {
                Map<String, Tag> customData = item.getCustomBlockData().getTags();
                for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                    nbt.put(tag.getKey(), tag.getValue());
                }
            }
        }
        return new BlockEntityCommandBlock(this.getChunk(), this.createCompoundTag(nbt));
    }

    protected CompoundTag createCompoundTag(CompoundTag nbt) {
        return nbt;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public Block cloneTo(Position pos) {
        BlockCommandBlock clone = (BlockCommandBlock) super.cloneTo(pos);
        BlockEntity source = this.level.getBlockEntity(this);
        if (source != null) {
            BlockEntityCommandBlock cb = (BlockEntityCommandBlock) source;
            clone.getOrCreateBlockEntity();
            BlockEntityCommandBlock cloneEntity = (BlockEntityCommandBlock) clone.getBlockEntity();
            cloneEntity.setConditional(cb.isConditional());
            cloneEntity.setAuto(cb.isAuto());
            cloneEntity.setCommand(cb.getCommand());
            cloneEntity.setLastExecution(cb.getLastExecution());
            cloneEntity.setTrackOutput(cb.isTrackOutput());
            cloneEntity.setLastOutput(cb.getLastOutput());
            cloneEntity.setLastOutputParams(cb.getLastOutputParams());
            cloneEntity.setLastOutputCommandMode(cb.getLastOutputCommandMode());
            cloneEntity.setLastOutputCondionalMode(cb.isLastOutputCondionalMode());
            cloneEntity.setLastOutputRedstoneMode(cb.isLastOutputRedstoneMode());
            cloneEntity.setSuccessCount(cb.getSuccessCount());
            cloneEntity.setConditional(cb.isConditional());
            cloneEntity.setTickDelay(cb.getTickDelay());
            cloneEntity.setExecutingOnFirstTick(cb.isExecutingOnFirstTick());

            if (cloneEntity.getMode() == MODE_REPEATING) {
                cloneEntity.scheduleUpdate();
            }
        }
        return clone;
    }
}
