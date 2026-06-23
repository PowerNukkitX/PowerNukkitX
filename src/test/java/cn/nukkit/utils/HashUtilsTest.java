package cn.nukkit.utils;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

public class HashUtilsTest {
    @Test
    void hashBlockState() {
        NbtMapBuilder compoundTag = NbtMap.builder();

        final NbtMap state = NbtMap.builder()
                .putBoolean("button_pressed_bit", false)
                .putInt("facing_direction", 5)
                .build();

        compoundTag.putString("name", "minecraft:warped_button")
                .putCompound("states", NbtMap.fromMap(new TreeMap<>(state)));
        int i = HashUtils.fnv1a_32_nbt(compoundTag.build());
        Assertions.assertEquals(1204504330,i);
    }
}