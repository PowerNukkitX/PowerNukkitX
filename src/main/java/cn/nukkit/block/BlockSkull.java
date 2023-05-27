package cn.nukkit.block;

/**
 * @author Justin
 */

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
public class BlockSkull extends BlockTransparentMeta implements RedstoneComponent, BlockEntityHolder<BlockEntitySkull>, Faceable {
    @PowerNukkitOnly
    @Deprecated
    @Since("1.6.0.0-PNX")
    public static final BooleanBlockProperty NO_DROP = new BooleanBlockProperty("no_drop_bit", false);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION);

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKULL_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SKULL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntitySkull> getBlockEntityClass() {
        return BlockEntitySkull.class;
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
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public String getName() {
        int itemMeta = 0;

        if (this.level != null) {
            BlockEntitySkull blockEntity = getBlockEntity();
            if (blockEntity != null) {
                itemMeta = blockEntity.namedTag.getByte("SkullType");
            }
        }

        return ItemSkull.getItemSkullName(itemMeta);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case UP:
                this.setDamage(face.getIndex());
                break;
            case DOWN:
            default:
                return false;
        }
        CompoundTag nbt = new CompoundTag()
                .putByte("SkullType", item.getDamage())
                .putByte("Rot", (int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
        // TODO: 2016/2/3 SPAWN WITHER
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    public int onUpdate(int type) {
        if ((type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_NORMAL) || !level.getServer().isRedstoneEnabled()) {
            return 0;
        }

        BlockEntitySkull entity = getBlockEntity();
        if (entity == null || entity.namedTag.getByte("SkullType") != 5) {
            return 0;
        }

        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        entity.setMouthMoving(this.isGettingPower());
        return Level.BLOCK_UPDATE_REDSTONE;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntitySkull entitySkull = getBlockEntity();
        int dropMeta = 0;
        if (entitySkull != null) {
            dropMeta = entitySkull.namedTag.getByte("SkullType");
        }
        return new Item[]{
                new ItemSkull(dropMeta)
        };
    }

    @Override
    public Item toItem() {
        if (getLevel() != null) {
            BlockEntitySkull blockEntity = getBlockEntity();
            int itemMeta = 0;
            if (blockEntity != null) {
                itemMeta = blockEntity.namedTag.getByte("SkullType");
            }
            return new ItemSkull(itemMeta);
        } else return new ItemSkull();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    @PowerNukkitOnly
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    @PowerNukkitOnly
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x7);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(this.x + 0.25, this.y, this.z + 0.25, this.x + 1 - 0.25, this.y + 0.5, this.z + 1 - 0.25);
        switch (this.getBlockFace()) {
            case NORTH:
                return bb.offset(0, 0.25, 0.25);
            case SOUTH:
                return bb.offset(0, 0.25, -0.25);
            case WEST:
                return bb.offset(0.25, 0.25, 0);
            case EAST:
                return bb.offset(-0.25, 0.25, 0);
        }
        return bb;
    }
}
