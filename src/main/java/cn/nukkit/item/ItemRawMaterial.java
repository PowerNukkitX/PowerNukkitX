package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
@Since("FUTURE")
public abstract class ItemRawMaterial extends StringItem {
    @PowerNukkitOnly
    @Since("FUTURE")
    public ItemRawMaterial(@Nonnull String id, @Nullable String name) {
        super(id, name);
    }
}
