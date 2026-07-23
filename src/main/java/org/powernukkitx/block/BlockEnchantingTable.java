package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityEnchantTable;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */

public class BlockEnchantingTable extends BlockTransparent implements BlockEntityHolder<BlockEntityEnchantTable> {

    public static final BlockProperties PROPERTIES = new BlockProperties(ENCHANTING_TABLE);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(5)
            .resistance(6000)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .lightEmission(7)
            .canBeActivated(true)
            .canHarvestWithHand(false)
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEnchantingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEnchantingTable(BlockState blockstate) {
        super(blockstate, DEFINITION);
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
    public double getMaxY() {
        return getY() + 12 / 16.0;
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        CompoundTag nbt = new CompoundTag();

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return true;
        }
        Item itemInHand = player.getInventory().getItemInMainHand();
        if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
            return false;
        }

        BlockEntityEnchantTable enchantTable = getOrCreateBlockEntity();
        if (enchantTable.getNbt().contains("Lock") && enchantTable.getNbt().get("Lock") instanceof StringTag
                && !enchantTable.getNbt().getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(enchantTable.getInventory());

        return true;
    }

    
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }
}
