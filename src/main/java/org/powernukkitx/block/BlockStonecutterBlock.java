package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.inventory.BlockInventoryHolder;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.StonecutterInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockStonecutterBlock extends BlockTransparent implements Faceable, BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(STONECUTTER_BLOCK, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(3.5)
            .resistance(17.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBeActivated(true)
            .canHarvestWithHand(false)
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonecutterBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonecutterBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex > -1) {
            this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                    CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(BlockFace.fromHorizontalIndex(horizontalIndex)));
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getHorizontalIndex()) : BlockFace.SOUTH);

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            player.addWindow(getInventory());
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockStonecutterBlock());
    }

    @Override
    public double getMaxY() {
        return y + 9 / 16.0;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new StonecutterInventory(this);
    }
}
