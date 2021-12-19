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
        final int type = getDamage();
        if (type <= 0) {
            name = "Arrow";
            return;
        }

        final int potionId = type - 1;
        switch (potionId) {
            case Potion.WATER:
                name = "Arrow of Splashing";
                return;
            case Potion.MUNDANE:
            case Potion.MUNDANE_II:
            case Potion.THICK:
            case Potion.AWKWARD:
                name = "Tipped Arrow";
                return;
            default:
                name = ItemPotion.buildName(potionId, "Arrow", false);
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
