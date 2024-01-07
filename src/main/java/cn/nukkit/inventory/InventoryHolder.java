package cn.nukkit.inventory;

import cn.nukkit.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface InventoryHolder {

    Inventory getInventory();

    int getFloorX();

    int getFloorY();

    int getFloorZ();

    Level getLevel();
}
