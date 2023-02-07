package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public abstract class ItemCustomBookEnchanted extends ItemBookEnchanted implements CustomItem {
    private final String id;

    public ItemCustomBookEnchanted(String id, String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, name);
        this.id = id;
    }

    @Override
    public String getTextureName() {
        return "book_enchanted";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public String getNamespaceId() {
        return id;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.customBuilder(this, ItemCreativeCategory.ITEMS)
                .allowOffHand(false)
                .creativeGroup("itemGroup.name.enchantedBook")
                .foil(true)
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

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
