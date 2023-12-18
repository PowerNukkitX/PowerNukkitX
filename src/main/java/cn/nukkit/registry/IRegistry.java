package cn.nukkit.registry;

import cn.nukkit.utils.OK;

import java.util.function.Consumer;

public interface IRegistry<K, V, R> {
    V get(K key);

    void trim();

    OK<?> register(K key, R value);

    void populate(Consumer<IRegistry<K, V, R>> registryConsumer);
}
