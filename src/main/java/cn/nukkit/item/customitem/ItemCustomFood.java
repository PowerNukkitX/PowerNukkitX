package cn.nukkit.item.customitem;

import cn.nukkit.item.ItemFood;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public abstract class ItemCustomFood extends ItemFood implements CustomItem {
    /**
     * @deprecated 
     */
    

    public ItemCustomFood(@NotNull String id) {
        super(id);
    }
    /**
     * @deprecated 
     */
    

    public boolean isDrink() {
        return false;
    }

}
