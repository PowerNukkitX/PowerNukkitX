package org.powernukkitx.inventory;

import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;

public interface InventoryHolder {
    Inventory getInventory();

    Level getLevel();

    double getX();

    double getY();

    double getZ();

    Vector3 getVector3();
}
