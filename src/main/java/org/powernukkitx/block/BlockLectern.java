package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityLectern;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.event.block.LecternDropBookEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockLectern extends BlockTransparent implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityLectern> {
    public static final BlockProperties PROPERTIES = new BlockProperties(LECTERN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_BIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(2.5)
            .resistance(12.5)
            .toolType(ItemTool.TYPE_AXE)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLectern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLectern(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Lectern";
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityLectern> getBlockEntityClass() {
        return BlockEntityLectern.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.LECTERN;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getMaxY() {
        return y + 0.89999;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        int power = 0;
        int page = 0;
        int maxPage = 0;
        BlockEntityLectern lectern = getBlockEntity();
        if (lectern != null && lectern.hasBook()) {
            maxPage = lectern.getTotalPages();
            page = lectern.getLeftPage() + 1;
            power = (int) (((float) page / maxPage) * 16);
        }
        return power;
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;
        BlockEntityLectern lectern = getOrCreateBlockEntity();
        Item currentBook = lectern.getBook();
        if (!currentBook.isNull()) {
            this.openBook(player);
            return true;
        }
        if (!Objects.equals(item.getId(), ItemID.WRITTEN_BOOK) && !Objects.equals(item.getId(), ItemID.WRITABLE_BOOK)) {
            return false;
        }

        if (!player.isCreative()) {
            item.count--;
        }

        Item newBook = item.clone();
        newBook.setCount(1);
        lectern.setBook(newBook);
        lectern.spawnToAll();
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PUT);
        return true;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return getPropertyValue(CommonBlockProperties.POWERED_BIT);
    }

    public void setActivated(boolean activated) {
        setPropertyValue(CommonBlockProperties.POWERED_BIT, activated);
    }

    public void executeRedstonePulse() {
        if (isActivated()) {
            level.cancelScheduledUpdate(this, this);
        } else {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        }

        level.scheduleUpdate(this, this, 4);
        setActivated(true);
        level.setBlock(this, this, true, false);
        level.addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PAGE_TURN);

        updateAroundRedstone();
        RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP);
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isActivated() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace face) {
        return face == BlockFace.DOWN ? this.getWeakPower(face) : 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (isActivated()) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                setActivated(false);
                level.setBlock(this, this, true, false);
                updateAroundRedstone();
                RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP);
            }

            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public void dropBook(Player player) {
        BlockEntityLectern lectern = getBlockEntity();
        if (lectern == null) {
            return;
        }

        Item book = lectern.getBook();
        if (book.isNull()) {
            return;
        }

        LecternDropBookEvent dropBookEvent = new LecternDropBookEvent(player, lectern, book);
        this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent);
        if (dropBookEvent.isCancelled()) {
            return;
        }

        lectern.setBook(Item.AIR);
        lectern.spawnToAll();
        this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), dropBookEvent.getBook());
    }

    public void openBook(Player player) {
        final ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.setContainerID((byte) -1);
        containerOpenPacket.setContainerType(ContainerType.LECTERN);
        containerOpenPacket.setPosition(Vector3i.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
        player.sendPacket(containerOpenPacket);
    }
}
