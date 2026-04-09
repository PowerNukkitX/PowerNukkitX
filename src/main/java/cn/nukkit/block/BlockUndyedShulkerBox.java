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
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author Reece Mackie
 */

public class BlockUndyedShulkerBox extends BlockTransparent implements BlockEntityHolder<BlockEntityShulkerBox> {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNDYED_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

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
    public String getBlockEntityType() {
        return BlockEntity.SHULKER_BOX;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    public Item getShulkerBox() {
        return new ItemBlock(this);
    }

    @Override
    public Item toItem() {
        Item item = getShulkerBox();

        if (this.getLevel() == null) return item;

        BlockEntityShulkerBox tile = getBlockEntity();

        if (tile == null) {
            return item;
        }

        ShulkerBoxInventory inv = tile.getRealInventory();

        if (!inv.isEmpty()) {
            NbtMap nbt = item.getNamedTag();
            if (nbt == null) {
                nbt = NbtMap.EMPTY;
            }

            List<NbtMap> items = new ObjectArrayList<>();

            for (int it = 0; it < inv.getSize(); it++) {
                if (!inv.getItem(it).isNull()) {
                    NbtMap d = ItemHelper.write(inv.getItem(it), it);
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        NbtMapBuilder nbt = NbtMap.builder();

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        NbtMap t = item.getNamedTag();

        // This code gets executed when the player has broken the shulker box and placed it back (©Kevims 2020)
        if (t != null && t.containsKey("Items")) {
            nbt.putList("Items", NbtType.COMPOUND, t.getList("Items", NbtType.COMPOUND));
        }

        // This code gets executed when the player has copied the shulker box in creative mode (©Kevims 2020)
        if (item.hasCustomBlockData()) {
            nbt.putAll(item.getCustomBlockData());
        }
        nbt.putByte("facing", (byte) face.getIndex());
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt.build()) != null;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        BlockEntityShulkerBox box = getOrCreateBlockEntity();
        Block block = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")));
        if (!player.getDataFlag(ActorFlags.SILENT) && !this.canBeOpened(block)) {
            return false;
        }

        player.addWindow(box.getInventory());
        return true;
    }

    protected boolean canBeOpened(Block block) {
        return block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockFlowable;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityShulkerBox be = getBlockEntity();

        if (be == null) {
            return 0;
        }

        return ContainerInventory.calculateRedstone(be.getInventory());
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public int getItemMaxStackSize() {
        return 1;
    }
}
