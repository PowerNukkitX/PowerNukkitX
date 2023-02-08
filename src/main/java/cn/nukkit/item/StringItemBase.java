package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public abstract class StringItemBase extends Item implements StringItem {
    private final String id;

    public StringItemBase(@NotNull String id, @Nullable String name) {
        super(STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));

        this.id = id;
        clearNamedTag();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public String getNamespaceId() {
        return this.id;
    }

    @Override
    public final int getId() {
        return StringItem.super.getId();
    }

    @Override
    public StringItemBase clone() {
        return (StringItemBase) super.clone();
    }
}
