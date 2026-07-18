package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.inventory.BlockInventoryHolder;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.LoomInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @implNote Faceable since FUTURE
 */

public class BlockLoom extends BlockSolid implements Faceable, BlockInventoryHolder {
    public static final BlockProperties PROPERTIES = new BlockProperties(LOOM, CommonBlockProperties.DIRECTION);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.5)
            .resistance(12.5)
            .toolType(ItemTool.TYPE_AXE)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLoom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLoom(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLoom());
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getInventory());
        }
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.getHorizontals()[getPropertyValue(CommonBlockProperties.DIRECTION)];
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new LoomInventory(this);
    }
}
