package cn.nukkit.item;

import cn.nukkit.potion.Potion;
import cn.nukkit.utils.ServerException;

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
    public void setMeta(Integer meta) {
        super.setMeta(meta);
        updateName();
    }

    private void updateName() {
        final int type = getMeta();
        if (type <= 0) {
            name = GENERIC_NAME;
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
                name = ItemPotion.buildName(potionId, GENERIC_NAME, false);
        }
    }


    @Nullable
    public Potion getTippedArrowPotion() {
        final int damage = getMeta();
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
