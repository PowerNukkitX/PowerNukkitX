package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

public interface EntityInventoryHolder extends InventoryHolder {

    EntityArmorInventory getArmorInventory();

    EntityEquipmentInventory getEquipmentInventory();

    default boolean canEquipByDispenser() {
        return false;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default Item getHelmet() {
        return getArmorInventory().getHelmet();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean setHelmet(Item item){
        return getArmorInventory().setHelmet(item);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default Item getChestplate() {
        return getArmorInventory().getChestplate();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean setChestplate(Item item){
        return getArmorInventory().setChestplate(item);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default Item getLeggings() {
        return getArmorInventory().getLeggings();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean setLeggings(Item item){
        return getArmorInventory().setLeggings(item);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default Item getBoots() {
        return getArmorInventory().getBoots();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean setBoots(Item item){
        return getArmorInventory().setBoots(item);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default Item getItemInHand(){
        return getEquipmentInventory().getItemInHand();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean setItemInHand(Item item){
        return getEquipmentInventory().setItemInHand(item);
    }
}
