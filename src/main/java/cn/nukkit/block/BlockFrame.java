package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.block.ItemFrameUseEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.*;
import static cn.nukkit.math.BlockFace.AxisDirection.POSITIVE;

public class BlockFrame extends BlockTransparent implements BlockEntityHolder<BlockEntityItemFrame>, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(FRAME, FACING_DIRECTION, ITEM_FRAME_MAP_BIT, ITEM_FRAME_PHOTO_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFrame() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFrame(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(@NotNull BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    public boolean isStoringMap() {
        return getPropertyValue(ITEM_FRAME_MAP_BIT);
    }

    public void setStoringMap(boolean map) {
        setPropertyValue(ITEM_FRAME_MAP_BIT, map);
    }

    public boolean isStoringPhoto() {
        return getPropertyValue(ITEM_FRAME_PHOTO_BIT);
    }

    public void setStoringPhoto(boolean hasPhoto) {
        setPropertyValue(ITEM_FRAME_PHOTO_BIT, hasPhoto);
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.ITEM_FRAME;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityItemFrame> getBlockEntityClass() {
        return BlockEntityItemFrame.class;
    }

    @Override
    public String getName() {
        return "Item Frame";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block support = this.getSideAtLayer(0, getFacing().getOpposite());
            if (!support.isSolid() && !support.getId().equals(COBBLESTONE_WALL)) {
                this.level.useBreakOn(this);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @org.jetbrains.annotations.Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        onUpdate(Level.BLOCK_UPDATE_TOUCH);
        if (player != null && action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            BlockEntityItemFrame blockEntity = getOrCreateBlockEntity();
            if (player.isCreative()) {
                blockEntity.setItem(Item.AIR);
            } else {
                blockEntity.dropItem(player);
            }
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking()) return false;
        BlockEntityItemFrame itemFrame = getOrCreateBlockEntity();
        if (itemFrame.getItem().isNull()) {
            Item itemOnFrame = item.clone();
            ItemFrameUseEvent event = new ItemFrameUseEvent(player, this, itemFrame, itemOnFrame, ItemFrameUseEvent.Action.PUT);
            this.getLevel().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            if (player != null && !player.isCreative()) {
                itemOnFrame.setCount(itemOnFrame.getCount() - 1);
                player.getInventory().setItemInHand(itemOnFrame);
            }
            itemOnFrame.setCount(1);
            itemFrame.setItem(itemOnFrame);
            if (Objects.equals(itemOnFrame.getId(), ItemID.FILLED_MAP)) {
                setStoringMap(true);
                this.getLevel().setBlock(this, this, true);
            }
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEMFRAME_ITEM_ADD);
        } else {
            ItemFrameUseEvent event = new ItemFrameUseEvent(player, this, itemFrame, null, ItemFrameUseEvent.Action.ROTATION);
            this.getLevel().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            itemFrame.setItemRotation((itemFrame.getItemRotation() + 1) % 8);
            if (isStoringMap()) {
                setStoringMap(false);
                this.getLevel().setBlock(this, this, true);
            }
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEMFRAME_ITEM_ROTATE);
        }
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if ((!(target.isSolid() || target instanceof BlockWallBase) && !target.equals(block) || target instanceof BlockFrame ||  (block.isSolid() && !block.canBeReplaced()))) {
            return false;
        }

        if (target.equals(block) && block.canBeReplaced()) {
            face = BlockFace.UP;
            target = block.down();
            if (!target.isSolid() && !(target instanceof BlockWallBase)) {
                return false;
            }
        }

        setBlockFace(face);
        setStoringMap(Objects.equals(item.getId(), ItemID.FILLED_MAP));
        CompoundTag nbt = new CompoundTag()
                .putByte("ItemRotation", 0)
                .putFloat("ItemDropChance", 1.0f);
        if (item.hasCustomBlockData()) {
            for (var e : item.getCustomBlockData().getEntrySet()) {
                nbt.put(e.getKey(), e.getValue());
            }
        }
        level.setBlock(block, this, true, true);
        BlockFrame levelBlock = (BlockFrame) block.getLevelBlock();
        BlockEntityItemFrame frame = levelBlock.getBlockEntity();
        if (frame == null) {
            frame = levelBlock.createBlockEntity(nbt);
        }

        this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_PLACE);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true);
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEMFRAME_BREAK);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntityItemFrame itemFrame = getBlockEntity();
        if (itemFrame != null && ThreadLocalRandom.current().nextFloat() <= itemFrame.getItemDropChance()) {
            return new Item[]{
                    toItem(), itemFrame.getItem().clone()
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityItemFrame blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return blockEntity.getAnalogOutput();
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return getBlockFace();
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double[][] aabb = {
                {2.0 / 16, 14.0 / 16},
                {2.0 / 16, 14.0 / 16},
                {2.0 / 16, 14.0 / 16}
        };

        BlockFace facing = getFacing();
        if (facing.getAxisDirection() == POSITIVE) {
            int axis = facing.getAxis().ordinal();
            aabb[axis][0] = 0;
            aabb[axis][1] = 1.0 / 16;
        }

        return new SimpleAxisAlignedBB(
                aabb[0][0] + x, aabb[1][0] + y, aabb[2][0] + z,
                aabb[0][1] + x, aabb[1][1] + y, aabb[2][1] + z
        );
    }
}