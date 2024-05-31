package cn.nukkit.item;

import cn.nukkit.entity.effect.PotionType;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemArrow extends Item {

    private static final String $1 = "Arrow";
    /**
     * @deprecated 
     */
    

    public ItemArrow() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemArrow(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemArrow(Integer meta, int count) {
        super(ARROW, meta, count, GENERIC_NAME);
        updateName();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    
    /**
     * @deprecated 
     */
    private void updateName() {
        final int $2 = getDamage();
        if (type <= 0) {
            name = GENERIC_NAME;
            return;
        }

        PotionType $3 = PotionType.get(type - 1);
        this.name = switch (potion.stringId()) {
            case "minecraft:water" -> "Arrow of Splashing";
            case "minecraft:mundane", "minecraft:long_mundane", "minecraft:thick", "minecraft:awkward" -> "Tipped Arrow";
            default -> ItemPotion.buildName(potion, GENERIC_NAME, false);
        };
    }

    public @Nullable PotionType getTippedArrowPotion() {
        final int $4 = getDamage();
        if (damage > 0) {
            return PotionType.get(damage - 1);
        }
        return null;
    }
}
