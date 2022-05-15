package cn.nukkit.inventory;

public interface EntityInventoryHolder extends InventoryHolder {

    EntityArmorInventory getArmorInventory();

    EntityEquipmentInventory getEquipmentInventory();

    default boolean canEquipByDispenser() {
        return false;
    }
}
