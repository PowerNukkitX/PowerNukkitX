package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;


public class BlockBarrel extends BlockSolid implements Faceable, BlockEntityHolder<BlockEntityBarrel> {
    public static final BlockProperties PROPERTIES = new BlockProperties(BARREL, FACING_DIRECTION, OPEN_BIT);

    public BlockBarrel() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBarrel(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Barrel";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.BARREL;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBarrel> getBlockEntityClass() {
        return BlockEntityBarrel.class;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.UP);
        } else {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    setBlockFace(BlockFace.UP);
                } else if (this.y - y > 0) {
                    setBlockFace(BlockFace.DOWN);
                } else {
                    setBlockFace(player.getHorizontalFacing().getOpposite());
                }
            } else {
                setBlockFace(player.getHorizontalFacing().getOpposite());
            }
        }

        CompoundTag nbt = new CompoundTag().putList("Items", new ListTag<>());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;

        BlockEntityBarrel barrel = getOrCreateBlockEntity();

        if (barrel.namedTag.contains("Lock") && barrel.namedTag.get("Lock") instanceof StringTag
                && !barrel.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(barrel.getInventory());
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBarrel());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    public boolean isOpen() {
        return getPropertyValue(OPEN_BIT);
    }

    public void setOpen(boolean open) {
        setPropertyValue(OPEN_BIT, open);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityBarrel blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }
}
