package cn.nukkit.inventory;

import cn.nukkit.level.Dimension;
import cn.nukkit.math.Vector3;

public interface InventoryHolder {
    Inventory getInventory();

    Dimension getLevel();

    double getX();

    double getY();

    double getZ();

    Vector3 getVector3();
}
