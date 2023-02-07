package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
@Since("FUTURE")
public abstract class ItemRawMaterial extends StringItemBase {
    @PowerNukkitOnly
    @Since("FUTURE")
    public ItemRawMaterial(@NotNull String id, @Nullable String name) {
        super(id, name);
    }
}
