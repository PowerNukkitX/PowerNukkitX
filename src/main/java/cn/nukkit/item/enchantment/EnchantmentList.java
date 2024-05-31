package cn.nukkit.item.enchantment;

/**
 * @author Nukkit Project Team
 */
public class EnchantmentList {

    private final EnchantmentEntry[] enchantments;
    /**
     * @deprecated 
     */
    

    public EnchantmentList(int size) {
        this.enchantments = new EnchantmentEntry[size];
    }

    /**
     * @param slot  The index of enchantment.
     * @param entry The given enchantment entry.
     * @return {@link EnchantmentList}
     */
    public EnchantmentList setSlot(int slot, EnchantmentEntry entry) {
        enchantments[slot] = entry;
        return this;
    }

    public EnchantmentEntry getSlot(int slot) {
        return enchantments[slot];
    }
    /**
     * @deprecated 
     */
    

    public int getSize() {
        return enchantments.length;
    }

}
