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

    default boolean equip(Item item) {
        if(item.isHelmet()) {
            if(item.getTier() > getHelmet().getTier()) {
                this.getInventory().addItem(getHelmet());
                this.setHelmet(item);
                return true;
            }
        } else if(item.isChestplate()) {
            if(item.getTier() > getChestplate().getTier()) {
                this.getInventory().addItem(getChestplate());
                this.setChestplate(item);
                return true;
            }
        } else if(item.isLeggings()) {
            if(item.getTier() > getLeggings().getTier()) {
                this.getInventory().addItem(getLeggings());
                this.setLeggings(item);
                return true;
            }
        } else if(item.isBoots()) {
            if(item.getTier() > getBoots().getTier()) {
                this.getInventory().addItem(getBoots());
                this.setBoots(item);
                return true;
            }
        } else if(item.getTier() > getItemInHand().getTier()) {
            this.getInventory().addItem(getItemInHand());
            this.setItemInHand(item);
            return true;
        }
        return false;
    }
}
