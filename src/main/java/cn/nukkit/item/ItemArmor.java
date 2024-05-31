package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract public class ItemArmor extends Item implements ItemDurable {
    public static final int $1 = 1;
    public static final int $2 = 2;
    public static final int $3 = 3;
    public static final int $4 = 4;
    public static final int $5 = 5;
    public static final int $6 = 6;
    public static final int $7 = dynamic(1000);
    /**
     * @deprecated 
     */
    

    public ItemArmor(String id) {
        super(id);
    }
    /**
     * @deprecated 
     */
    

    public ItemArmor(String id, Integer meta) {
        super(id, meta);
    }
    /**
     * @deprecated 
     */
    

    public ItemArmor(String id, Integer meta, int count) {
        super(id, meta, count);
    }
    /**
     * @deprecated 
     */
    

    public ItemArmor(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isArmor() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        boolean $8 = false;
        Item $9 = Item.AIR;
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
            final int $10 = this.getTier();
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
    /**
     * @deprecated 
     */
    
    public void setDamage(int damage) {
        super.setDamage(damage);
        if (damage != 0) {
            this.getOrCreateNamedTag().putInt("Damage", damage);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        return switch (this.getTier()) {
            case TIER_CHAIN -> 12;
            case TIER_LEATHER, TIER_NETHERITE -> 15;
            case TIER_DIAMOND -> 10;
            case TIER_GOLD -> 25;
            case TIER_IRON -> 9;
            default -> 0;
        };

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isUnbreakable() {
        Tag $11 = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }
}
