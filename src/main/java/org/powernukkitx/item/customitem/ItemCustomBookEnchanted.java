package org.powernukkitx.item.customitem;

import org.powernukkitx.item.ItemEnchantedBook;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.enchantment.Enchantment;

public abstract class ItemCustomBookEnchanted extends ItemEnchantedBook implements CustomItem {

    public ItemCustomBookEnchanted(String id) {
        super(id);
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.customBuilder(this)
                .name(name)
                .texture("book_enchanted")
                .allowOffHand(false)
                .creativeCategory(CreativeCategory.ITEMS)
                .creativeGroup("itemGroup.name.enchantedBook")
                .glint(true)
                .customBuild(nbt -> nbt.getCompound("components")
                        .getCompound("item_properties")
                        .putString("enchantable_slot", "all")
                        .putInt("enchantable_value", 20)
                        .putBoolean("hand_equipped", false)
                        .putFloat("mining_speed", 1f)
                        .putBoolean("mirrored_art", false)
                        .putInt("use_animation", 0)
                        .putInt("use_duration", 0)
                        .putBoolean("animates_in_toolbar", false)
                );
    }

    public Enchantment getEnchantment() {
        return Enchantment.getEnchantment(this.getId());
    }
}
