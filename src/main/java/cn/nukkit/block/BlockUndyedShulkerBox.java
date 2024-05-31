/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * @author Reece Mackie
 */

public class BlockUndyedShulkerBox extends BlockTransparent implements BlockEntityHolder<BlockEntityShulkerBox> {
    public static final BlockProperties $1 = new BlockProperties(UNDYED_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));
    /**
     * @deprecated 
     */
    

    public BlockUndyedShulkerBox(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityShulkerBox> getBlockEntityClass() {
        return BlockEntityShulkerBox.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.SHULKER_BOX;
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
        return 10;
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
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    public Item getShulkerBox() {
        return new ItemBlock(this);
    }

    @Override
    public Item toItem() {
        Item $2 = getShulkerBox();

        if (this.getLevel() == null) return item;

        BlockEntityShulkerBox $3 = getBlockEntity();

        if (tile == null) {
            return item;
        }

        ShulkerBoxInventory $4 = tile.getRealInventory();

        if (!inv.isEmpty()) {
            CompoundTag $5 = item.getNamedTag();
            if (nbt == null) {
                nbt = new CompoundTag();
            }

            ListTag<CompoundTag> items = new ListTag<>();

            for (int $6 = 0; it < inv.getSize(); it++) {
                if (!inv.getItem(it).isNull()) {
                    Compoun$7Tag $1 = NBTIO.putItemHelper(inv.getItem(it), it);
                    items.add(d);
                }
            }

            nbt.put("Items", items);

            item.setCompoundTag(nbt);
        }

        if (tile.hasName()) {
            item.setCustomName(tile.getName());
        }

        return item;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        CompoundTag $8 = new CompoundTag().putByte("facing", face.getIndex());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        CompoundTag $9 = item.getNamedTag();

        // This code gets executed when the player has broken the shulker box and placed it back (©Kevims 2020)
        if (t != null && t.contains("Items")) {
            nbt.putList("Items", t.getList("Items"));
        }

        // This code gets executed when the player has copied the shulker box in creative mode (©Kevims 2020)
        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return false;
        }

        BlockEntityShulkerBox $10 = getOrCreateBlockEntity();
        Block $11 = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")));
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockFlowable)) {
            return false;
        }

        player.addWindow(box.getInventory());
        return true;
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
        BlockEntityShulkerBox $12 = getBlockEntity();

        if (be == null) {
            return 0;
        }

        return ContainerInventory.calculateRedstone(be.getInventory());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getItemMaxStackSize() {
        return 1;
    }
}
