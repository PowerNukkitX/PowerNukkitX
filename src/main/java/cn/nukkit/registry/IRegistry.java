package cn.nukkit.registry;

import me.sunlan.fastreflection.FastMemberLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

public interface IRegistry<K, V, R> {
    @ApiStatus.Internal
    HashMap<String, FastMemberLoader> fastMemberLoaderCache = new HashMap<>();

    void init();

    V get(K key);

    void trim();

    void reload();

    void register(K key, R value) throws RegisterException;
}
