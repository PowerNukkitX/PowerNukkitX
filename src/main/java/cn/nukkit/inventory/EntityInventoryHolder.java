package cn.nukkit.inventory;

public interface EntityInventoryHolder extends InventoryHolder {

    EntityArmorInventory getArmorInventory();

    EntityEquipmentInventory getEquipmentInventory();
}
