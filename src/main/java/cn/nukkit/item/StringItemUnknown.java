package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public final class StringItemUnknown extends StringItemBase {
    @PowerNukkitOnly
    public StringItemUnknown(@Nonnull String id) {
        super(id, UNKNOWN_STR);
    }
}
