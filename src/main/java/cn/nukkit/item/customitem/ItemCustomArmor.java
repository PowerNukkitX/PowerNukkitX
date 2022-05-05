package cn.nukkit.item.customitem;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lt_name
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomArmor extends ItemCustom implements ItemDurable {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_CHAIN = 3;
    public static final int TIER_GOLD = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;
    public static final int TIER_OTHER = 7;

    public ItemCustomArmor(@Nonnull String id, @Nullable String name) {
        super(id, name);
    }

    public ItemCustomArmor(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
        super(id, name, textureName);
    }

    @Override
    public int getCreativeCategory() {
        return super.getCreativeCategory();
    }

    @Override
    public CompoundTag getComponentsData() {
        CompoundTag data = super.getComponentsData();

        data.getCompound("components")
                .putCompound("minecraft:armor", new CompoundTag()
                        .putInt("protection", this.getArmorPoints()))
                .putCompound("minecraft:durability", new CompoundTag()
                        .putInt("max_durability", this.getMaxDurability()));

        if (this.isHelmet()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.head");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putInt("slot", 2));
        } else if (this.isChestplate()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.chest");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putInt("slot", 3));
        } else if (this.isLeggings()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.legs");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putInt("slot", 4));
        } else if (this.isBoots()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.feet");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putInt("slot", 5));
        }

        return data;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean isHelmet() {
        return false;
    }

    @Override
    public boolean isChestplate() {
        return false;
    }

    @Override
    public boolean isLeggings() {
        return false;
    }

    @Override
    public boolean isBoots() {
        return false;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        boolean equip = false;
        Item oldSlotItem = Item.get(AIR);
        if (this.isHelmet()) {
            oldSlotItem = player.getInventory().getHelmet();
            if (player.getInventory().setHelmet(this)) {
                equip = true;
            }
        } else if (this.isChestplate()) {
            oldSlotItem = player.getInventory().getChestplate();
            if (player.getInventory().setChestplate(this)) {
                equip = true;
            }
        } else if (this.isLeggings()) {
            oldSlotItem = player.getInventory().getLeggings();
            if (player.getInventory().setLeggings(this)) {
                equip = true;
            }
        } else if (this.isBoots()) {
            oldSlotItem = player.getInventory().getBoots();
            if (player.getInventory().setBoots(this)) {
                equip = true;
            }
        }
        if (equip) {
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), oldSlotItem);
            final int tier = this.getTier();
            switch (tier) {
                case TIER_CHAIN:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_CHAIN);
                    break;
                case TIER_DIAMOND:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_DIAMOND);
                    break;
                case TIER_GOLD:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GOLD);
                    break;
                case TIER_IRON:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_IRON);
                    break;
                case TIER_LEATHER:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_LEATHER);
                    break;
                case TIER_NETHERITE:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_NETHERITE);
                    break;
                default:
                    player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GENERIC);
            }
        }

        return this.getCount() == 0;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case TIER_CHAIN:
                return 12;
            case TIER_LEATHER:
            case TIER_NETHERITE:
                return 15;
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 25;
            case TIER_IRON:
                return 9;
        }

        return 0;
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

}
