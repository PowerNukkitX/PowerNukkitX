package cn.nukkit.item.customitem;

import cn.nukkit.item.ItemArmor;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public abstract class ItemCustomArmor extends ItemArmor implements CustomItem {
    public ItemCustomArmor(@NotNull String id) {
        super(id);
    }
}
