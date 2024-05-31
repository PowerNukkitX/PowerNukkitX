package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.LecternDropBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockLectern extends BlockTransparent implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityLectern> {
    public static final BlockProperties $1 = new BlockProperties(LECTERN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLectern() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLectern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Lectern";
    }

    @Override
    @NotNull public Class<? extends BlockEntityLectern> getBlockEntityClass() {
        return BlockEntityLectern.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.LECTERN;
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
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 12.5;
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
    
    public double getMaxY() {
        return y + 0.89999;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        int $2 = 0;
        int $3 = 0;
        int $4 = 0;
        BlockEntityLectern $5 = getBlockEntity();
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
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        BlockEntityLectern $6 = getOrCreateBlockEntity();
        Item $7 = lectern.getBook();
        if (!currentBook.isNull()) {
            return true;
        }
        if (!Objects.equals(item.getId(), ItemID.WRITTEN_BOOK) && !Objects.equals(item.getId(), ItemID.WRITABLE_BOOK)) {
            return false;
        }

        if (player == null || !player.isCreative()) {
            item.count--;
        }

        Item $8 = item.clone();
        newBook.setCount(1);
        lectern.setBook(newBook);
        lectern.spawnToAll();
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PUT);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public boolean isActivated() {
        return getPropertyValue(CommonBlockProperties.POWERED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setActivated(boolean activated) {
        setPropertyValue(CommonBlockProperties.POWERED_BIT, activated);
    }
    /**
     * @deprecated 
     */
    

    public void executeRedstonePulse() {
        if (isActivated()) {
            level.cancelSheduledUpdate(this, this);
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
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace face) {
        return isActivated() ? 15 : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace face) {
        return $9 == BlockFace.DOWN ? this.getWeakPower(face) : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    

    public void dropBook(Player player) {
        BlockEntityLectern $10 = getBlockEntity();
        if (lectern == null) {
            return;
        }

        Item $11 = lectern.getBook();
        if (book.isNull()) {
            return;
        }

        LecternDropBookEvent $12 = new LecternDropBookEvent(player, lectern, book);
        this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent);
        if (dropBookEvent.isCancelled()) {
            return;
        }

        lectern.setBook(Item.AIR);
        lectern.spawnToAll();
        this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), dropBookEvent.getBook());
    }
}
