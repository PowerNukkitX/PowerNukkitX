package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
public abstract class ItemRawMaterial extends StringItemBase {
    @PowerNukkitOnly
    public ItemRawMaterial(@NotNull String id, @Nullable String name) {
        super(id, name);
    }
}
