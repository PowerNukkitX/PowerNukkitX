package cn.nukkit.level.generator.terra.mappings;

/**
 * Allay Project 2023/10/27
 *
 * @author daoge_cmd
 */
public class SimpleDoubleKeyMappedRegistry<K1, K2, VALUE> implements DoubleKeyMappedRegistry<K1, K2, VALUE> {

    protected MapPair<K1, K2, VALUE> mapPair;

    protected <INPUT> SimpleDoubleKeyMappedRegistry(INPUT input, RegistryLoader<INPUT, MapPair<K1, K2, VALUE>> registryLoader) {
        this.mapPair = registryLoader.load(input);
    }

    @Override
    public MapPair<K1, K2, VALUE> getContent() {
        return mapPair;
    }

    @Override
    public void setContent(MapPair<K1, K2, VALUE> mapPair) {
        this.mapPair = mapPair;
    }
}
