package cn.nukkit.level.generator.terra.mappings;

import com.google.common.collect.BiMap;

/**
 * Allay Project 2023/10/28
 *
 * @author daoge_cmd
 */
public interface BiMappedRegistry<LEFT, RIGHT> extends Registry<BiMap<LEFT, RIGHT>> {

    default RIGHT getByLeft(LEFT left) {
        return getContent().get(left);
    }

    default LEFT getByRight(RIGHT right) {
        return getContent().inverse().get(right);
    }

    default RIGHT getByLeftOrDefault(LEFT left, RIGHT defaultValue) {
        return getContent().getOrDefault(left, defaultValue);
    }

    default LEFT getByRightOrDefault(RIGHT right, LEFT defaultValue) {
        return getContent().inverse().getOrDefault(right, defaultValue);
    }

    default void register(LEFT left, RIGHT right) {
        getContent().put(left, right);
    }

}
