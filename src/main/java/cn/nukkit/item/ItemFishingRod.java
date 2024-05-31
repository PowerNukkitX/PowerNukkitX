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
    /**
     * @deprecated 
     */
    

    public ItemFishingRod() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFishingRod(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            player.stopFishing(true);
            player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, player.clone(), VibrationType.ITEM_INTERACT_FINISH));
        } else {
            player.startFishing(this);
            this.meta++;
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnBreak() {
        return true;
    }
}
