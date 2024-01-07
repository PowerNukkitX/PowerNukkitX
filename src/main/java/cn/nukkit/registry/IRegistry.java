package cn.nukkit.registry;

public interface IRegistry<K, V, R> {

    void init();

    V get(K key);

    void trim();

    void register(K key, R value) throws RegisterException;

}
