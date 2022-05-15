package cn.nukkit.inventory;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface InventoryHolder {

    Inventory getInventory();

    public default boolean canEquipByDispenser() {
        return false;
    }
}
