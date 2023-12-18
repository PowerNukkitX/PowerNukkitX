package cn.nukkit.registry;

import cn.nukkit.utils.OK;

public interface IRegistry<K, V, R> {
    V get(K key);

    void trim();

    OK<?> register(K key, R value);

    void init();
}
