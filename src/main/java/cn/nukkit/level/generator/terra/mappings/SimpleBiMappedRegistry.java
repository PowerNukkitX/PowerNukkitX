package cn.nukkit.level.generator.terra.mappings;

import com.google.common.collect.BiMap;

/**
 * Allay Project 2023/10/28
 *
 * @author daoge_cmd
 */
public class SimpleBiMappedRegistry<LEFT, RIGHT> implements BiMappedRegistry<LEFT, RIGHT> {

    protected BiMap<LEFT, RIGHT> mappings;

    protected <INPUT> SimpleBiMappedRegistry(INPUT input, RegistryLoader<INPUT, BiMap<LEFT, RIGHT>> registryLoader) {
        this.mappings = registryLoader.load(input);
    }

    public static <INPUT, LEFT, RIGHT> SimpleBiMappedRegistry<LEFT, RIGHT> of(RegistryLoader<INPUT, BiMap<LEFT, RIGHT>> registryLoader) {
        return new SimpleBiMappedRegistry<>(null, registryLoader);
    }

    @Override
    public BiMap<LEFT, RIGHT> getContent() {
        return mappings;
    }

    @Override
    public void setContent(BiMap<LEFT, RIGHT> mappings) {
        this.mappings = mappings;
    }
}
