package cn.nukkit.metadata;

import cn.nukkit.level.Dimension;

import java.util.Locale;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable level, String metadataKey) {
        if (!(level instanceof Dimension)) {
            throw new IllegalArgumentException("Argument must be a Dimension instance");
        }
        return (((Dimension) level).getName() + ":" + metadataKey).toLowerCase(Locale.ENGLISH);
    }
}
