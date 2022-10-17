package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.StringItem;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustom extends StringItem {
    @Getter
    private String textureName;

    public ItemCustom(@Nonnull String id, @Nullable String name) {
        super(id.toLowerCase(Locale.ENGLISH), name);
    }

    public ItemCustom(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
        this(id, name);
        this.textureName = textureName;
    }

    public abstract CustomItemDefinition getDefinition();

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }
}
