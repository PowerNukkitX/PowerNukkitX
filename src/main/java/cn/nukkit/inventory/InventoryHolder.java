package cn.nukkit.inventory;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public interface InventoryHolder {
    Inventory getInventory();

    Level getLevel();

    double getX();

    double getY();

    double getZ();

    Vector3 getVector3();
}
