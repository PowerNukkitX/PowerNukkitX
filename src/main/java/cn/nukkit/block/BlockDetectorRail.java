package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockDetectorRail extends BlockRail implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(DETECTOR_RAIL, RAIL_DATA_BIT, CommonBlockProperties.RAIL_DIRECTION_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDetectorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDetectorRail(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
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

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_NORMAL) {
            checkMinecart();
            return type;
        }
        return super.onUpdate(type);
    }

    public void checkMinecart() {
        updateState(findMinecart() != null);
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
            //每20gt检查一遍
            level.scheduleUpdate(this, 20);
            //更新比较器输出
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