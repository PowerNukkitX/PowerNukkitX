package cn.nukkit.block;

import cn.nukkit.GameMockExtension;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.HashUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

public class BlockPropertiesTest {

    @Test
    @SneakyThrows
    void BlockPaletteTest() {
        Registries.BLOCK.init();
        HashMap<String, String> errors = new HashMap<>();
        try (var stream = BlockProperties.class.getClassLoader().getResourceAsStream("block_palette.nbt")) {
            CompoundTag nbt = NBTIO.readCompressed(stream);
            ListTag<CompoundTag> blocks = nbt.getList("blocks", CompoundTag.class);
            for (var b : blocks.getAll()) {
                int i = HashUtils.fnv1a_32_nbt_palette(b);
                BlockState blockState = Registries.BLOCKSTATE.get(i);
                if (blockState == null) {
                    errors.put(b.getString("name"), "palette not match vanilla,expected: " + i + " block: " + b.getString("name"));
                }
            }
        }
        Assertions.assertEquals(184, errors.size());//Version 1.20.60, There are now a total of 184 blocks for 1.21 content or educational content
    }
}
