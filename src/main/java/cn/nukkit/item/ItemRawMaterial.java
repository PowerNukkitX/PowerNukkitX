package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

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
