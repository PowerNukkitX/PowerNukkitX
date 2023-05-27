package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockChest extends BlockTransparentMeta implements Faceable, BlockEntityHolder<BlockEntityChest> {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public BlockChest() {
        this(0);
    }

    public BlockChest(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityChest> getBlockEntityClass() {
        return BlockEntityChest.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.CHEST;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return CHEST;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
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
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9475;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }


    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        CompoundTag nbt = new CompoundTag().putList(new ListTag<>("Items"));

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityChest blockEntity = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt);
        if (blockEntity == null) {
            return false;
        }

        tryPair();

        return true;
    }

    /**
     * 尝试与旁边箱子连接
     * @return 是否连接成功
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    protected boolean tryPair() {
        BlockEntityChest blockEntity = getBlockEntity();
        if (blockEntity == null)
            return false;

        BlockEntityChest chest = findPair();
        if (chest == null)
            return false;

        chest.pairWith(blockEntity);
        blockEntity.pairWith(chest);
        return true;
    }

    /**
     * 寻找附近的可配对箱子
     * @return 找到的可配对箱子。若没找到，则为null
     */
    @Nullable
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    protected BlockEntityChest findPair() {
        for (int side = 2; side <= 5; ++side) {
            if ((this.getDamage() == 4 || this.getDamage() == 5) && (side == 4 || side == 5)) {
                continue;
            } else if ((this.getDamage() == 3 || this.getDamage() == 2) && (side == 2 || side == 3)) {
                continue;
            }
            Block c = this.getSide(BlockFace.fromIndex(side));
            if (c instanceof BlockChest && c.getDamage() == this.getDamage()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
                if (blockEntity instanceof BlockEntityChest blockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    return blockEntityChest;
                }
            }
        }
        return null;
    }

    @Since("1.19.60-r1")
    @Override
    public boolean cloneTo(Position pos) {
        if (!super.cloneTo(pos)) return false;
        else {
            var blockEntity = this.getBlockEntity();
            if (blockEntity != null && blockEntity.isPaired())
                ((BlockChest) pos.getLevelBlock()).tryPair();
            return true;
        }
    }

    @Override
    public boolean onBreak(Item item) {
        BlockEntityChest chest = getBlockEntity();
        if (chest != null) {
            chest.unpair();
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (player == null) {
            return false;
        }

        Block top = up();
        if (!top.isTransparent()) {
            return false;
        }

        BlockEntityChest chest = getOrCreateBlockEntity();
        if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag 
                && !chest.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(chest.getInventory());
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityChest blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean canBePushed() {
        return canMove();
    }

    @PowerNukkitOnly
    @Override
    public boolean canBePulled() {
        return canMove();
    }

    /**
     * TODO: 大箱子在PNX不能推动
     */
    protected boolean canMove() {
        var blockEntity = this.getBlockEntity();
        return blockEntity == null || !blockEntity.isPaired();
    }
}
