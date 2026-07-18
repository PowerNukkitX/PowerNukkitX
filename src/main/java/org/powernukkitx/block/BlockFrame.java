package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityItemFrame;
import org.powernukkitx.event.block.ItemFrameUseEvent;
import org.powernukkitx.event.player.PlayerInteractEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Faceable;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.ITEM_FRAME_MAP_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.ITEM_FRAME_PHOTO_BIT;
import static org.powernukkitx.math.BlockFace.AxisDirection.POSITIVE;

public class BlockFrame extends BlockTransparent implements BlockEntityHolder<BlockEntityItemFrame>, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(FRAME, FACING_DIRECTION, ITEM_FRAME_MAP_BIT, ITEM_FRAME_PHOTO_BIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.25)
            .canPassThrough(true)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .canBeActivated(true)
            .hasComparatorInputOverride(true)
            .waterloggingLevel(1)
            .build();

    /**
     * Per-position lock to prevent concurrent interactions (left-click + right-click, or multiple players)
     * from racing on the same item frame. Entries are removed after each interaction completes.
     */
    private static final ConcurrentHashMap<Long, Object> frameInteractionLocks = new ConcurrentHashMap<>();

    private static long positionKey(int x, int y, int z) {
        return ((long) x & 0x3FFFFFF) | (((long) z & 0x3FFFFFF) << 26) | (((long) y & 0xFFF) << 52);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFrame() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFrame(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockFrame(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
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
            if (!support.isSolid() && !(support instanceof BlockWallBase)) {
                this.level.useBreakOn(this);
                return type;
            }
        }

        return 0;
    }

    @Override
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @org.jetbrains.annotations.Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        onUpdate(Level.BLOCK_UPDATE_TOUCH);
        if (player != null && action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            long key = positionKey(getFloorX(), getFloorY(), getFloorZ());
            Object lock = frameInteractionLocks.computeIfAbsent(key, k -> new Object());
            synchronized (lock) {
                try {
                    BlockEntityItemFrame blockEntity = getBlockEntity();
                    if (blockEntity == null) return;
                    if (player.isCreative()) {
                        Item before = blockEntity.getItem();
                        if (before.isNull()) return;

                        ItemFrameUseEvent event = new ItemFrameUseEvent(player, this, blockEntity, before, ItemFrameUseEvent.Action.REMOVE);
                        this.getLevel().getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            blockEntity.spawnTo(player);
                            return;
                        }

                        blockEntity.setItem(Item.AIR);
                        blockEntity.setItemRotation(0);

                        if (isStoringMap()) {
                            setStoringMap(false);
                            this.getLevel().setBlock(this, this, true);
                        }

                        this.getLevel().addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_ITEM_REMOVE);
                    } else {
                        blockEntity.dropItem(player);
                    }
                } finally {
                    frameInteractionLocks.remove(key);
                }
            }
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null || player.isSneaking()) {
            return false;
        }

        long key = positionKey(getFloorX(), getFloorY(), getFloorZ());
        Object lock = frameInteractionLocks.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            try {
                BlockEntityItemFrame itemFrame = getBlockEntity();
                if (itemFrame == null) return false;

                if (itemFrame.getItem().isNull()) {
                    Item itemOnFrame = item.clone();
                    ItemFrameUseEvent event = new ItemFrameUseEvent(player, this, itemFrame, itemOnFrame, ItemFrameUseEvent.Action.PUT);
                    this.getLevel().getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) return false;

                    if (!player.isCreative()) {
                        itemOnFrame.setCount(itemOnFrame.getCount() - 1);
                        player.getInventory().setItemInMainHand(itemOnFrame);
                    }

                    itemOnFrame.setCount(1);
                    itemFrame.setItem(itemOnFrame);

                    if (Objects.equals(itemOnFrame.getId(), ItemID.FILLED_MAP)) {
                        setStoringMap(true);
                        this.getLevel().setBlock(this, this, true);
                    }

                this.getLevel().addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_ITEM_ADD);
            } else {
                ItemFrameUseEvent event = new ItemFrameUseEvent(player, this, itemFrame, null, ItemFrameUseEvent.Action.ROTATION);
                this.getLevel().getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) return false;

                    itemFrame.setItemRotation((itemFrame.getItemRotation() + 1) % 8);

                    if (isStoringMap()) {
                        setStoringMap(false);
                        this.getLevel().setBlock(this, this, true);
                    }

                this.getLevel().addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_ITEM_ROTATE);
            }

                return true;
            } finally {
                frameInteractionLocks.remove(key);
            }
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if ((!(target.isSolid() || target instanceof BlockWallBase) && !target.equals(block) || target instanceof BlockFrame || (block.isSolid() && !block.canBeReplaced()))) {
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
                .putByte("ItemRotation", (byte) 0)
                .putFloat("ItemDropChance", 1.0f);
        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }
        level.setBlock(block, this, false, true);
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
        this.getLevel().addLevelEvent(this, LevelEvent.SOUND_ITEMFRAME_BREAK);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntityItemFrame itemFrame = getBlockEntity();
        if (itemFrame != null && !itemFrame.getItem().isNull() && ThreadLocalRandom.current().nextFloat() <= itemFrame.getItemDropChance()) {
            return new Item[]{toItem(), itemFrame.getItem().clone()};
        }
        return new Item[]{toItem()};
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
