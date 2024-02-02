package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.registry.CreativeItemRegistry;
import cn.nukkit.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.TreeMap;

public class CreativeItemTest {
    @Test
    void init() {
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Assertions.assertDoesNotThrow(() -> {
            try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.nbt")) {
                CompoundTag compoundTag = NBTIO.readCompressed(input);
                TreeMap<Integer, Tag> tagTreeMap = new TreeMap<>();
                compoundTag.getTags().forEach((key, value) -> tagTreeMap.put(Integer.parseInt(key), value));

                for (var entry : tagTreeMap.entrySet()) {
                    int index = entry.getKey();
                    CompoundTag tag = (CompoundTag) entry.getValue();
                    int damage = tag.getInt("damage");
                    if (tag.containsInt("blockStateHash")) {
                        int blockStateHash = tag.getInt("blockStateHash");
                        BlockState blockState = Registries.BLOCKSTATE.get(blockStateHash);
                        if (blockState == null) {
                            throw new IllegalArgumentException();
                        }
                        Block block = Registries.BLOCK.get(blockState);
                        new ItemBlock(block, damage);
                    } else {
                        String name = tag.getString("name");
                        Item item = Item.get(name, damage);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
