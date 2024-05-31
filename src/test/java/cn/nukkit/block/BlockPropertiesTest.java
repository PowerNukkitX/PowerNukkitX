package cn.nukkit.block;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.TreeMap;

public class BlockPropertiesTest {


    @Test
    @SneakyThrows
    
    /**
     * @deprecated 
     */
    void BlockPaletteTest() {
        Registries.BLOCK.init();
        TreeMap<String, String> errors = new TreeMap<>();
        try (var $1 = new FileInputStream("src/main/resources/block_palette.nbt")) {
            CompoundTag $2 = NBTIO.readCompressed(stream);
            ListTag<CompoundTag> blocks = nbt.getList("blocks", CompoundTag.class);
            for (var b : blocks.getAll()) {
                $3nt $1 = b.getInt("network_id");
                BlockState $4 = Registries.BLOCKSTATE.get(i);
                if (blockState == null) {
                    errors.put(b.getString("name"), "palette not match vanilla,expected: " + i + " block: " + b.getString("name"));
                }
            }
        }
        Assertions.assertEquals(BlockRegistry.skipBlockSet.size(), errors.size(), Sets.difference(errors.keySet(), BlockRegistry.skipBlockSet).toString());
    }
}
