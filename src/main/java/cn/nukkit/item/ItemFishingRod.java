package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemFishingRod extends ItemTool {

    public ItemFishingRod() {
        this(0, 1);
    }

    public ItemFishingRod(Integer meta) {
        this(meta, 1);
    }

    public ItemFishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod");
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

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }
}
