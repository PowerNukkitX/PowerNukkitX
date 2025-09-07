package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Extend Item directly and implement CustomItem; custom-item hooks are now in Item and <p>
 * definitions use CustomItemDefinition.SimpleBuilder, so this wrapper is no longer needed. <p>
 * Prefer to extend Item class {@link Item} while implementing {@link CustomItem} for customized items.
 * @author lt_name
 */
@Deprecated
public abstract class ItemCustomFood extends ItemFood implements CustomItem {

    public ItemCustomFood(@NotNull String id) {
        super(id);
    }

    public boolean isDrink() {
        return false;
    }
}
