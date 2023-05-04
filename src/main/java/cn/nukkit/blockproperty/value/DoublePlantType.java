package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @author joserobjr
 * @since 2021-05-22
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public enum DoublePlantType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    SUNFLOWER,

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    SYRINGA("Lilac", false),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    GRASS("Double Tallgrass", true),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    FERN("Large Fern", true),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    ROSE("Rose Bush", false),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    PAEONIA("Peony", false)
    ;
    private final String englishName;
    private final boolean replaceable;

    @SuppressWarnings("UnstableApiUsage")
    DoublePlantType() {
        englishName = capitalize(name());
        replaceable = false;
    }

    public static @Nullable String capitalize(@Nullable String str) {
        return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT) : str;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isReplaceable() {
        return replaceable;
    }
}
