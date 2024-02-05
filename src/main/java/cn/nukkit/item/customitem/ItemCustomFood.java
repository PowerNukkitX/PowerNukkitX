package cn.nukkit.item.customitem;

import cn.nukkit.Player;
import cn.nukkit.item.ItemFood;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author lt_name
 */


public abstract class ItemCustomFood extends ItemFood implements CustomItem {

    public ItemCustomFood(@NotNull String id) {
        super(id);
    }

    public boolean isDrink() {
        return false;
    }

}
