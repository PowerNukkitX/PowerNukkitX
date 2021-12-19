package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.ServerException;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemArrow extends Item {

    public ItemArrow() {
        this(0, 1);
    }

    public ItemArrow(Integer meta) {
        this(meta, 1);
    }

    public ItemArrow(Integer meta, int count) {
        super(ARROW, meta, count, "Arrow");
        updateName();
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        final int damage = getDamage();
        switch (damage) {
            case 0:
                name = "Arrow";
                return;
            case 1:
                name = "Arrow of Splashing";
                return;
            case 2:
            case 3:
            case 4:
            case 5:
                name = "Tipped Arrow";
                return;
            default:
                name = ItemPotion.buildName(damage - 1, "Arrow", false);
        }
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nullable
    public Potion getTippedArrowPotion() {
        final int damage = getDamage();
        if (damage > 0) {
            try {
                return Potion.getPotion(damage - 1);
            } catch (ServerException ignored) {
                // Not found
                return null;
            }
        }
        return null;
    }
}
