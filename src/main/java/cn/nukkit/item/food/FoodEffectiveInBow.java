package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemBowl;

@PowerNukkitOnly
public class FoodEffectiveInBow extends FoodEffective {
    @PowerNukkitOnly
    public FoodEffectiveInBow(int restoreFood, float restoreSaturation) {
        super(restoreFood, restoreSaturation);
    }
    
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemBowl());
        return true;
    }
}
