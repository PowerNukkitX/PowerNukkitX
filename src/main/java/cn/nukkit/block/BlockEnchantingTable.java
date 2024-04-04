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

    public static final BlockProperties PROPERTIES = new BlockProperties(ENCHANTING_TABLE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEnchantingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEnchantingTable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Enchanting Table";
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.ENCHANT_TABLE;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityEnchantTable> getBlockEntityClass() {
        return BlockEntityEnchantTable.class;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getMaxY() {
        return getY() + 12 / 16.0;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        CompoundTag nbt = new CompoundTag();

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
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return true;
        }
        Item itemInHand = player.getInventory().getItemInHand();
        if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
            return false;
        }

        BlockEntityEnchantTable enchantTable = getOrCreateBlockEntity();
        if (enchantTable.namedTag.contains("Lock") && enchantTable.namedTag.get("Lock") instanceof StringTag
                && !enchantTable.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(enchantTable.getInventory());

        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }
}
