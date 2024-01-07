package cn.nukkit.block;

import cn.nukkit.PNXTestExtension;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.HashUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(PNXTestExtension.class)
public class BlockPropertiesTest {

    @Test
    @SneakyThrows
    void BlockPaletteTest() {
        List<String> errorList = new ArrayList<>();
        try (var stream = BlockProperties.class.getClassLoader().getResourceAsStream("block_palette.nbt")) {
            CompoundTag nbt = NBTIO.readCompressed(stream);
            ListTag<CompoundTag> blocks = nbt.getList("blocks", CompoundTag.class);
            for (var b : blocks.getAll()) {
                int i = HashUtils.fnv1a_32_nbt_palette(b);
                BlockState blockState = Registries.BLOCKSTATE.get(i);
                if (blockState == null)
                    errorList.add("palette not match vanilla,expected: " + i + " block: " + b.toSNBT(4));
            }
        }
        System.out.println(errorList);
    }
}
