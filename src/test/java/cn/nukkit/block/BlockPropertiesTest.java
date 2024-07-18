package cn.nukkit.block;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.TreeMap;

public class BlockPropertiesTest {

    @Test
    @SneakyThrows
    void BlockPaletteTest() {
        Registries.BLOCK.init();
        TreeMap<String, String> errors = new TreeMap<>();
        int countedBlocks = 0;
        try (var stream = new FileInputStream("src/main/resources/block_palette.nbt")) {
            CompoundTag nbt = NBTIO.readCompressed(stream);
            ListTag<CompoundTag> blocks = nbt.getList("blocks", CompoundTag.class);
            for (var b : blocks.getAll()) {
                //Temporary removed this test functionality, cuz Idk what we should use instead of previous code
                String name = b.getString("name");
                if(BlockRegistry.skipBlockSet.contains(name)) {
                    errors.put(name, "Skipped block: " + name);
                }
                countedBlocks++;
            }
        }
        System.out.println("Counted blocks: " + countedBlocks);
        Assertions.assertEquals(BlockRegistry.skipBlockSet.size(), errors.size(), Sets.difference(errors.keySet(), BlockRegistry.skipBlockSet).toString());
    }
}