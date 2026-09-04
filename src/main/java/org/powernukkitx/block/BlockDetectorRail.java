package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityMinecartAbstract;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.OptionalBoolean;
import org.powernukkitx.utils.Rail;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockDetectorRail extends BlockRail implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(DETECTOR_RAIL, RAIL_DATA_BIT, CommonBlockProperties.RAIL_DIRECTION_6);
    public static final BlockDefinition DEFINITION = BlockRail.DEFINITION.toBuilder()
            .hasComparatorInputOverride(true)
            .isPowerSource(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDetectorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDetectorRail(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }

    @Override
    public int getComparatorInputOverride() {
        return findMinecart() instanceof InventoryHolder inventoryHolder ? ContainerInventory.calculateRedstone(inventoryHolder.getInventory()) : 0;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return isActive() ? (side == BlockFace.UP ? 15 : 0) : 0;
    }

    public @Nullable EntityMinecartAbstract findMinecart() {
        for (Entity entity : level.getNearbyEntities(new SimpleAxisAlignedBB(
                getFloorX() + 0.2,
                getFloorY(),
                getFloorZ() + 0.2,
                getFloorX() + 0.8,
                getFloorY() + 0.8,
                getFloorZ() + 0.8))) {
            if (entity instanceof EntityMinecartAbstract minecart)
                return minecart;
        }
        return null;
    }

    public void updateState(boolean powered) {
        var wasPowered = isActive();
        if (powered != wasPowered) {
            this.setActive(powered);
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN));
        }
        if (powered) {
            //check once every 20gt
            level.scheduleUpdate(this, 20);
            //update comparator output
            level.updateComparatorOutputLevel(this);
        }
    }

    @Override
    public boolean isActive() {
        return getPropertyValue(RAIL_DATA_BIT);
    }

    @Override
    public OptionalBoolean isRailActive() {
        return OptionalBoolean.of(getPropertyValue(RAIL_DATA_BIT));
    }

    @Override
    public void setRailActive(boolean active) {
        setPropertyValue(RAIL_DATA_BIT, active);
    }

    /**
     * Changes the rail direction.
     *
     * @param orientation The new orientation
     */
    public void setRailDirection(Rail.Orientation orientation) {
        setPropertyValue(RAIL_DIRECTION_6, orientation.metadata());
    }

    public Rail.Orientation getOrientation() {
        return Rail.Orientation.byMetadata(getPropertyValue(RAIL_DIRECTION_6));
    }
}