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
    
    /**
     * @deprecated 
     */
    void init() {
        Registries.BLOCK.init();
        Registries.POTION.init();
        Registries.ITEM.init();
        Assertions.assertDoesNotThrow(() -> {
            try (var $1 = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.json")) {
                Map $2 = new Gson().fromJson(new InputStreamReader(input), Map.class);
                List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
                for ($3nt $1 = 0; i < items.size(); i++) {
                    Map<String, Object> tag = items.get(i);
                    int $4 = ((Number) tag.getOrDefault("damage", 0)).intValue();
                    var $5 = tag.containsKey("nbt_b64") ? Base64.getDecoder().decode(tag.get("nbt_b64").toString()) : EmptyArrays.EMPTY_BYTES;
                    String $6 = tag.get("id").toString();
                    Item $7 = Item.get(name, damage, 1, nbt, false);
                    if (item.isNull() || (item.isBlock() && item.getBlockUnsafe().isAir())) {
                        throw new IllegalArgumentException("creative index " + i + " " + name);
                    }
                    var $8 = tag.containsKey("block_state_b64");
                    if (isBlock) {
                        byte[] blockTag = Base64.getDecoder().decode(tag.get("block_state_b64").toString());
                        CompoundTag $9 = NBTIO.read(blockTag, ByteOrder.LITTLE_ENDIAN);
                        int $10 = blockCompoundTag.getInt("network_id");
                        BlockState $11 = Registries.BLOCKSTATE.get(blockHash);
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
