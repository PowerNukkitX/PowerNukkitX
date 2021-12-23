package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import lombok.ToString;

@PowerNukkitOnly
@ToString(callSuper = true)
public class TakeLevelAction extends InventoryAction {
    
    private final int levels;

    @PowerNukkitOnly
    public TakeLevelAction(int levels) {
        super(Item.get(0), Item.get(0));
        this.levels = levels;
    }

    @PowerNukkitOnly
    public int getLevels() {
        return levels;
    }
    
    @Override
    public boolean isValid(Player source) {
        return source.isCreative() || source.getExperienceLevel() >= levels;
    }
    
    @Override
    public boolean execute(Player source) {
        if (source.isCreative()) {
            return true;
        }
        int playerLevels = source.getExperienceLevel();
        if (playerLevels < levels) {
            return false;
        }
        source.setExperience(source.getExperience(), playerLevels - levels, false);
        return true;
    }
    
    @Override
    public void onExecuteSuccess(Player source) {
    
    }
    
    @Override
    public void onExecuteFail(Player source) {
    
    }
}
