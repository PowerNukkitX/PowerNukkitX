package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemPoisonousPotato extends ItemPotato {
    public static final ItemDefinition DEFINITION = ItemPotato.DEFINITION.toBuilder()
            .nutrition(2)
            .saturation(1.2f)
            .build();

    public ItemPoisonousPotato() {
        this(0, 1);
    }

    public ItemPoisonousPotato(Integer meta) {
        this(meta, 1);
    }

    public ItemPoisonousPotato(Integer meta, int count) {
        super(POISONOUS_POTATO, meta, count, "Poisonous Potato", DEFINITION);
        this.block = Block.get(BlockID.POTATOES);
    }

    @Override
    public boolean onEaten(Player player) {
        if (0.6F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.POISON).setDuration(80));
        }
        return super.onEaten(player);
    }
}
