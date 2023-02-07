package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public final class StringItemUnknown extends StringItemBase {
    @PowerNukkitOnly
    public StringItemUnknown(@NotNull String id) {
        super(id, UNKNOWN_STR);
    }
}
