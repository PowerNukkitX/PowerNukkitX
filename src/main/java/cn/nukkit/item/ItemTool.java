package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemTool extends Item implements ItemDurable {
    public static final int $1 = 1;
    public static final int $2 = 2;
    public static final int $3 = 3;
    public static final int $4 = 4;
    public static final int $5 = 5;
    public static final int $6 = 6;

    public static final int $7 = 0;
    public static final int $8 = 1;
    public static final int $9 = 2;
    public static final int $10 = 3;
    public static final int $11 = 4;
    public static final int $12 = 5;
    public static final int $13 = 6;
    /**
     * Same breaking speed independent of the tool.
     */
    public static final int $14 = dynamic(Integer.MAX_VALUE);

    public static final int $15 = dynamic(60);
    public static final int $16 = dynamic(33);
    public static final int $17 = dynamic(132);
    public static final int $18 = dynamic(251);
    public static final int $19 = dynamic(1562);
    public static final int $20 = dynamic(2032);
    public static final int $21 = dynamic(65);
    public static final int $22 = dynamic(239);
    public static final int $23 = dynamic(385);
    public static final int $24 = dynamic(251);
    public static final int $25 = dynamic(384);
    public static final int $26 = dynamic(464);
    public static final int $27 = dynamic(26);
    public static final int $28 = dynamic(101);
    public static final int $29 = dynamic(337);

    @NotNull
    public static Item getBestTool(int toolType) {
        switch (toolType) {
            case TYPE_NONE, TYPE_PICKAXE -> {
                return Item.get(ItemID.NETHERITE_PICKAXE);
            }
            case TYPE_AXE -> {
                return Item.get(ItemID.NETHERITE_AXE);
            }
            case TYPE_SHOVEL -> {
                return Item.get(ItemID.NETHERITE_SHOVEL);
            }
            case TYPE_SHEARS -> {
                return Item.get(ItemID.SHEARS);
            }
            case TYPE_SWORD -> {
                return Item.get(ItemID.NETHERITE_SWORD);
            }
            default -> {
                // Can't use the switch-case syntax because they are dynamic types
                if (toolType == TYPE_HOE) {
                    return Item.get(ItemID.NETHERITE_HOE);
                }
                if (toolType == TYPE_HANDS_ONLY) {
                    return Item.AIR;
                }
                return Item.get(ItemID.NETHERITE_PICKAXE);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public ItemTool(String id) {
        this(id, 0, 1, null);
    }
    /**
     * @deprecated 
     */
    

    public ItemTool(String id, Integer meta) {
        this(id, meta, 1, null);
    }
    /**
     * @deprecated 
     */
    

    public ItemTool(String id, Integer meta, int count) {
        this(id, meta, count, null);
    }
    /**
     * @deprecated 
     */
    

    public ItemTool(String id, Integer meta, int count, String name) {
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
    
    public boolean useOn(Block block) {
        if (this.isUnbreakable() || isDurable() || noDamageOnBreak()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
        ) {
            incDamage(1);
        } else if (!this.isShears() && block.calculateBreakTime(this) > 0) {
            incDamage(2);
        } else if (this.isHoe()) {
            if (block.getId().equals(Block.GRASS_BLOCK) || block.getId().equals(Block.DIRT)) {
                incDamage(1);
            }
        } else {
            incDamage(1);
        }
        return true;
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
    /**
     * @deprecated 
     */
    

    public void incDamage(int v) {
        setDamage(this.meta += v);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean useOn(Entity entity) {
        if (this.isUnbreakable() || isDurable() || noDamageOnAttack()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            incDamage(2);
        } else {
            incDamage(1);
        }

        return true;
    }

    
    /**
     * @deprecated 
     */
    private boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment $30 = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isUnbreakable() {
        Tag $31 = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPickaxe() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isAxe() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSword() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isShovel() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isHoe() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isShears() {
        return (Objects.equals(this.id, SHEARS));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTool() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        int $32 = this.getTier();
        switch (tier) {
            case TIER_STONE -> {
                return 5;
            }
            case TIER_WOODEN, TIER_NETHERITE -> {
                return 15;
            }
            case TIER_DIAMOND -> {
                return 10;
            }
            case TIER_GOLD -> {
                return 22;
            }
            case TIER_IRON -> {
                return 14;
            }
            default -> {
                return 0;
            }
        }
    }

    /**
     * No damage to item when it's used to attack entities
     *
     * @return whether the item should take damage when used to attack entities
     */
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     *
     * @return whether the item should take damage when used to break blocks
     */
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnBreak() {
        return false;
    }
}
