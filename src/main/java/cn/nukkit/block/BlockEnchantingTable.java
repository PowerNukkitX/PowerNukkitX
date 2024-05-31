package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */

public class BlockEnchantingTable extends BlockTransparent implements BlockEntityHolder<BlockEntityEnchantTable> {

    public static final BlockProperties $1 = new BlockProperties(ENCHANTING_TABLE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEnchantingTable() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEnchantingTable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Enchanting Table";
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.ENCHANT_TABLE;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityEnchantTable> getBlockEntityClass() {
        return BlockEntityEnchantTable.class;
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
    
    public double getHardness() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6000;
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
    
    public int getLightLevel() {
        return 7;
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
    
    public double getMaxY() {
        return getY() + 12 / 16.0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        CompoundTag $2 = new CompoundTag();

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
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return true;
        }
        Item $3 = player.getInventory().getItemInHand();
        if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
            return false;
        }

        BlockEntityEnchantTable $4 = getOrCreateBlockEntity();
        if (enchantTable.namedTag.contains("Lock") && enchantTable.namedTag.get("Lock") instanceof StringTag
                && !enchantTable.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(enchantTable.getInventory());

        return true;
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
    
    public boolean isSolid(BlockFace side) {
        return false;
    }
}
