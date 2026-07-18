package org.powernukkitx.metadata;

import org.powernukkitx.level.Level;

import java.util.Locale;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable level, String metadataKey) {
        if (!(level instanceof Level)) {
            throw new IllegalArgumentException("Argument must be a Level instance");
        }
        return (((Level) level).getName() + ":" + metadataKey).toLowerCase(Locale.ENGLISH);
    }
}
