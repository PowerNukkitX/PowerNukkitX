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
    public static final int TIER_WOODEN = 1;
    public static final int TIER_GOLD = 2;
    public static final int TIER_STONE = 3;
    public static final int TIER_IRON = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SWORD = 1;
    public static final int TYPE_SHOVEL = 2;
    public static final int TYPE_PICKAXE = 3;
    public static final int TYPE_AXE = 4;
    public static final int TYPE_SHEARS = 5;
    public static final int TYPE_HOE = 6;

    /**
     * Same breaking speed independent of the tool.
     */

    public static final int TYPE_HANDS_ONLY = dynamic(Integer.MAX_VALUE);

    public static final int DURABILITY_WOODEN = dynamic(60);
    public static final int DURABILITY_GOLD = dynamic(33);
    public static final int DURABILITY_STONE = dynamic(132);
    public static final int DURABILITY_IRON = dynamic(251);
    public static final int DURABILITY_DIAMOND = dynamic(1562);
    public static final int DURABILITY_NETHERITE = dynamic(2032);
    public static final int DURABILITY_FLINT_STEEL = dynamic(65);
    public static final int DURABILITY_SHEARS = dynamic(239);
    public static final int DURABILITY_BOW = dynamic(385);
    public static final int DURABILITY_TRIDENT = dynamic(251);
    public static final int DURABILITY_FISHING_ROD = dynamic(384);
    public static final int DURABILITY_CROSSBOW = dynamic(464);
    public static final int DURABILITY_CARROT_ON_A_STICK = dynamic(26);
    public static final int DURABILITY_WARPED_FUNGUS_ON_A_STICK = dynamic(101);
    public static final int DURABILITY_SHIELD = dynamic(337);

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

    public ItemTool(String id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemTool(String id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemTool(String id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemTool(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
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
            if (block.getId().equals(Block.GRASS) || block.getId().equals(Block.DIRT)) {
                incDamage(1);
            }
        } else {
            incDamage(1);
        }
        return true;
    }

    public void incDamage(int v) {
        this.meta += v;
        this.getOrCreateNamedTag().putInt("Damage", meta);
    }

    @Override
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

    private boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100);
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    public boolean isPickaxe() {
        return false;
    }

    @Override
    public boolean isAxe() {
        return false;
    }

    @Override
    public boolean isSword() {
        return false;
    }

    @Override
    public boolean isShovel() {
        return false;
    }

    @Override
    public boolean isHoe() {
        return false;
    }

    @Override
    public boolean isShears() {
        return (Objects.equals(this.id, SHEARS));
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getEnchantAbility() {
        int tier = this.getTier();
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
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     *
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }
}
