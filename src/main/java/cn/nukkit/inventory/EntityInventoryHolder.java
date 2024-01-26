package cn.nukkit.inventory;



import cn.nukkit.item.Item;

public interface EntityInventoryHolder extends InventoryHolder {

    EntityArmorInventory getArmorInventory();

    EntityEquipmentInventory getEquipmentInventory();

    default boolean canEquipByDispenser() {
        return false;
    }

    default Item getHelmet() {
        return getArmorInventory().getHelmet();
    }

    default boolean setHelmet(Item item) {
        return getArmorInventory().setHelmet(item);
    }

    default Item getChestplate() {
        return getArmorInventory().getChestplate();
    }

    default boolean setChestplate(Item item) {
        return getArmorInventory().setChestplate(item);
    }

    default Item getLeggings() {
        return getArmorInventory().getLeggings();
    }

    default boolean setLeggings(Item item) {
        return getArmorInventory().setLeggings(item);
    }

    default Item getBoots() {
        return getArmorInventory().getBoots();
    }

    default boolean setBoots(Item item) {
        return getArmorInventory().setBoots(item);
    }

    default Item getItemInHand() {
        return getEquipmentInventory().getItemInHand();
    }

    default Item getItemInOffhand() {
        return this.getEquipmentInventory().getItemInOffhand();
    }

    default boolean setItemInHand(Item item) {
        return getEquipmentInventory().setItemInHand(item);
    }

    default boolean setItemInHand(Item item, boolean send) {
        return getEquipmentInventory().setItemInHand(item, send);
    }

    default boolean setItemInOffhand(Item item) {
        return this.getEquipmentInventory().setItemInOffhand(item, true);
    }

    default boolean setItemInOffhand(Item item, boolean send) {
        return this.getEquipmentInventory().setItemInOffhand(item, send);
    }
}
