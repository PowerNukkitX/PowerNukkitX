package cn.nukkit.item;

import cn.nukkit.entity.effect.PotionType;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemArrow extends Item {

    private static final String GENERIC_NAME = "Arrow";

    public ItemArrow() {
        this(0, 1);
    }

    public ItemArrow(Integer meta) {
        this(meta, 1);
    }

    public ItemArrow(Integer meta, int count) {
        super(ARROW, meta, count, GENERIC_NAME);
        updateName();
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        final int type = getDamage();
        if (type <= 0) {
            name = GENERIC_NAME;
            return;
        }

        PotionType potion = PotionType.get(type - 1);
        this.name = switch (potion.stringId()) {
            case "minecraft:water" -> "Arrow of Splashing";
            case "minecraft:mundane", "minecraft:long_mundane", "minecraft:thick", "minecraft:awkward" -> "Tipped Arrow";
            default -> ItemPotion.buildName(potion, GENERIC_NAME, false);
        };
    }

    public @Nullable PotionType getTippedArrowPotion() {
        final int damage = getDamage();
        if (damage > 0) {
            return PotionType.get(damage - 1);
        }
        return null;
    }
}
