package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BlockStateTest {


    @Test
    @SneakyThrows
    void BlockStateTest() {
        Registries.BLOCK.init();
        int blocks = 0;
        try (var stream = new FileInputStream("src/main/resources/gamedata/endstone/block_states.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonArray blockStateData = JsonParser.parseReader(reader).getAsJsonArray();
            for(int i = 0; i < blockStateData.size(); i++) {
                JsonObject entry = blockStateData.get(i).getAsJsonObject();
                int hash = entry.get("blockStateHash").getAsInt();
                String name = entry.get("name").getAsString();
                if(BlockRegistry.skipBlockSet.contains(name)) continue;
                BlockState state = Registries.BLOCKSTATE.get(hash);
                if(state == null) {
                    throw new RuntimeException(name + " (" + hash + ") was not a part of block_states.json.");
                } else {
                    if(!state.getIdentifier().equals(name)) {
                        throw new RuntimeException("BlockState " + hash + " was not " + name + ". Instead it is " + state.getIdentifier());
                    }
                }
                blocks++;
            }
            Assertions.assertEquals(blocks, Registries.BLOCKSTATE.getAllState().size());
        }
    }

    @Test
    @SneakyThrows
    void BlockStateImpl_computeSpecialValue() {
        short i1 = BlockState.computeSpecialValue(new BlockPropertyType.BlockPropertyValue[]{
                CommonBlockProperties.DIRECTION.createValue(1),//2bit
                CommonBlockProperties.OPEN_BIT.createValue(false),//1bit
                CommonBlockProperties.UPSIDE_DOWN_BIT.createValue(false)//1bit
        });
        Assertions.assertEquals((1 << 2 | 0 << 1 | 0), i1);
    }
}
