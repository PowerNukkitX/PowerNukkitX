package cn.nukkit.item;

import cn.nukkit.block.BlockState;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.CreativeItemRegistry;
import cn.nukkit.registry.Registries;
import com.google.gson.Gson;
import io.netty.util.internal.EmptyArrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CreativeItemTest {
    @Test
    void init() {
        Registries.BLOCK.init();
        Registries.POTION.init();
        Registries.ITEM.init();
        Assertions.assertDoesNotThrow(() -> {
            try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.json")) {
                Map data = new Gson().fromJson(new InputStreamReader(input), Map.class);
                List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
                for (int i = 0; i < items.size(); i++) {
                    Map<String, Object> tag = items.get(i);
                    int damage = ((Number) tag.getOrDefault("damage", 0)).intValue();
                    var nbt = tag.containsKey("nbt_b64") ? Base64.getDecoder().decode(tag.get("nbt_b64").toString()) : EmptyArrays.EMPTY_BYTES;
                    String name = tag.get("id").toString();
                    Item item = Item.get(name, damage, 1, nbt, false);
                    if (item.isNull() || (item.isBlock() && item.getBlockUnsafe().isAir())) {
                        throw new IllegalArgumentException("creative index " + i + " " + name);
                    }
                    var isBlock = tag.containsKey("block_state_b64");
                    if (isBlock) {
                        byte[] blockTag = Base64.getDecoder().decode(tag.get("block_state_b64").toString());
                        CompoundTag blockCompoundTag = NBTIO.read(blockTag, ByteOrder.LITTLE_ENDIAN);
                        int blockHash = blockCompoundTag.getInt("network_id");
                        BlockState block = Registries.BLOCKSTATE.get(blockHash);
                        if (block == null) {
                            throw new IllegalArgumentException("creative index " + i + " " + blockHash);
                        }
                        item.setBlockUnsafe(block.toBlock());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
