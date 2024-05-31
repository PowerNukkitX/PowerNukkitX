package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
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
import java.util.List;
import java.util.Map;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockChest extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityChest> {
    public static final BlockProperties $1 = new BlockProperties(CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChest() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityChest> getBlockEntityClass() {
        return BlockEntityChest.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.CHEST;
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
    
    public String getName() {
        return "Chest";
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
    
    public int getWaterloggingLevel() {
        return 1;
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
    
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.9475;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        CompoundTag $2 = new CompoundTag().putList("Items", new ListTag<>());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityChest $3 = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt);
        if (blockEntity == null) {
            return false;
        }

        tryPair();

        return true;
    }

    /**
     * 尝试与旁边箱子连接
     * <p>
     * Try to pair with a chest next to it
     *
     * @return 是否连接成功 <br> Whether pairing was successful
     */
    
    /**
     * @deprecated 
     */
    protected boolean tryPair() {
        BlockEntityChest $4 = getBlockEntity();
        if (blockEntity == null)
            return false;

        BlockEntityChest $5 = findPair();
        if (chest == null)
            return false;

        chest.pairWith(blockEntity);
        blockEntity.pairWith(chest);
        return true;
    }

    /**
     * 寻找附近的可配对箱子
     * <p>
     * Search for nearby chest to pair with
     *
     * @return 找到的可配对箱子。若没找到，则为null <br> Chest to pair with. Null if none have been found
     */
    protected @Nullable BlockEntityChest findPair() {
        List<MinecraftCardinalDirection> universe = CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION.getValidValues();
        BlockFace $6 = getBlockFace();
        for (var face : universe) {
            Block $7 = this.getSide(CommonPropertyMap.CARDINAL_BLOCKFACE.get(face));
            if (side instanceof BlockChest chest) {
                BlockFace $8 = chest.getBlockFace();
                if (thisFace == pairFace) {
                    return chest.getBlockEntity();
                }
            }
        }
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean cloneTo(Position pos) {
        if (!super.cloneTo(pos)) return false;
        else {
            var $9 = this.getBlockEntity();
            if (blockEntity != null && blockEntity.isPaired())
                ((BlockChest) pos.getLevelBlock()).tryPair();
            return true;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        BlockEntityChest $10 = getBlockEntity();
        if (chest != null) {
            chest.unpair();
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        Item $11 = player.getInventory().getItemInHand();
        if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) return false;

        Block $12 = up();
        if (!top.isTransparent()) {
            return false;
        }

        BlockEntityChest $13 = getOrCreateBlockEntity();
        if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag
                && !chest.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(chest.getInventory());
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
        BlockEntityChest $14 = getBlockEntity();

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
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(this.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return canMove();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return canMove();
    }

    /**
     * TODO: 大箱子在PNX不能推动
     */
    
    /**
     * @deprecated 
     */
    protected boolean canMove() {
        var $15 = this.getBlockEntity();
        return $16 == null || !blockEntity.isPaired();
    }
}
