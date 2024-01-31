package cn.nukkit.item.customitem;

import cn.nukkit.Player;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.food.Food;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author lt_name
 */


public abstract class ItemCustomEdible extends ItemEdible implements CustomItem {

    public ItemCustomEdible(@NotNull String id) {
        super(id);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().isHungry() || player.isCreative() || canAlwaysEat()) {
            return true;
        }
        player.getFoodData().sendFood();
        return false;
    }

    public abstract Map.Entry<Plugin, Food> getFood();

    public boolean isDrink() {
        return false;
    }

    public boolean canAlwaysEat() {
        return false;
    }
}
