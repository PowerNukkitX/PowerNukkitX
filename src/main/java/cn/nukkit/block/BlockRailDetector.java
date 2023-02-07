package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.RedstoneComponent;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author CreeperFace (Nukkit Project), larryTheCoder (Minecart and Riding Project)
 * @since 2015/11/22 
 */
public class BlockRailDetector extends BlockRail implements RedstoneComponent {

    protected int comparatorInput = 0;

    public BlockRailDetector() {
        this(0);
        canBePowered = true;
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return ACTIVABLE_PROPERTIES;
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
        if (findMinecart() != null) updateState(true);
        else updateState(false);
    }

    @Nullable
    public EntityMinecartAbstract findMinecart() {
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
        return getBooleanValue(ACTIVE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public OptionalBoolean isRailActive() {
        return OptionalBoolean.of(getBooleanValue(ACTIVE));
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setRailActive(boolean active) {
        setBooleanValue(ACTIVE, active);
    }
}
