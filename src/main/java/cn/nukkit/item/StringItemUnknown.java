package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
@Since("FUTURE")
public final class StringItemUnknown extends StringItem {
    @PowerNukkitOnly
    @Since("FUTURE")
    public StringItemUnknown(@Nonnull String id) {
        super(id, UNKNOWN_STR);
    }
}
