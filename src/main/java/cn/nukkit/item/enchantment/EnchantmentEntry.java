package cn.nukkit.item.enchantment;

/**
 * @author Nukkit Project Team
 */
public class EnchantmentEntry {

    private final Enchantment[] enchantments;
    private final int cost;
    private final String randomName;
    /**
     * @deprecated 
     */
    

    public EnchantmentEntry(Enchantment[] enchantments, int cost, String randomName) {
        this.enchantments = enchantments;
        this.cost = cost;
        this.randomName = randomName;
    }

    public Enchantment[] getEnchantments() {
        return enchantments;
    }
    /**
     * @deprecated 
     */
    

    public int getCost() {
        return cost;
    }
    /**
     * @deprecated 
     */
    

    public String getRandomName() {
        return randomName;
    }

}
