package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.Vector3;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemFishingRod extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .maxDurability(ItemTool.DURABILITY_FISHING_ROD)
            .noDamageOnAttack(true)
            .noDamageOnBreak(true)
            .build();

    public ItemFishingRod() {
        this(0, 1);
    }

    public ItemFishingRod(Integer meta) {
        this(meta, 1);
    }

    public ItemFishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod", DEFINITION);
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            player.stopFishing(true);
            player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, player.getLocation(), VibrationType.ITEM_INTERACT_FINISH));
        } else {
            player.startFishing(this);
            this.meta++;
        }
        return true;
    }
}
