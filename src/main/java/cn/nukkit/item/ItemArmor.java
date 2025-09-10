package cn.nukkit.item;

import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;


/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract public class ItemArmor extends Item {

    public ItemArmor(String id) {
        super(id);
    }

    public ItemArmor(String id, Integer meta) {
        super(id, meta);
    }

    public ItemArmor(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemArmor(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public int getEnchantAbility() {
        return switch (this.getTier()) {
            case WEARABLE_TIER_CHAIN -> 12;
            case WEARABLE_TIER_LEATHER, WEARABLE_TIER_NETHERITE -> 15;
            case WEARABLE_TIER_DIAMOND -> 10;
            case WEARABLE_TIER_GOLD -> 25;
            case WEARABLE_TIER_IRON -> 9;
            default -> 0;
        };
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }
}
