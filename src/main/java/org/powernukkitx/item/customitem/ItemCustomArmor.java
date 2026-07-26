package org.powernukkitx.item.customitem;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemArmor;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Extend Item directly and implement CustomItem; custom-item hooks are now in Item and <p>
 * definitions use CustomItemDefinition.SimpleBuilder, so this wrapper is no longer needed. <p>
 * Prefer to extend Item class {@link Item} while implementing {@link CustomItem} for customized items.
 * @author lt_name
 */
@Deprecated
public abstract class ItemCustomArmor extends ItemArmor implements CustomItem {
    public ItemCustomArmor(@NotNull String id) {
        super(id);
    }
}
