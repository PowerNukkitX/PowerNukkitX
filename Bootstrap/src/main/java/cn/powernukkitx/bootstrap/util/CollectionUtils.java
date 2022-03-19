package cn.powernukkitx.bootstrap.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CollectionUtils {
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor, int initialAllocSize) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>(initialAllocSize);
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        return distinctByKey(keyExtractor, 16);
    }

    public static <T> boolean has(T[] arr, T obj) {
        for(final T e : arr) {
            if(Objects.equals(e, obj)) {
                return true;
            }
        }
        return false;
    }
}
