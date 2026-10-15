package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.math.Vector3;

/**
 * @author joserobjr
 */
public class ItemHoneyBottle extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .maxStackSize(16)
            .nutrition(6)
            .saturation(1.2f)
            .build();

    public ItemHoneyBottle() {
        this(0, 1);
    }

    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemHoneyBottle(Integer meta, int count) {
        super(HONEY_BOTTLE, meta, count, "Honey Bottle", DEFINITION);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public boolean onEaten(Player player) {
        if (player.isAdventure() || player.isSurvival()) {
            player.getInventory().addItem(new ItemGlassBottle());
        }
        player.removeEffect(EffectType.POISON);

        return super.onEaten(player);
    }
}
