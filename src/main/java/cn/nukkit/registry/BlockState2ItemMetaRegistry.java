package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.IOException;

/**
 * @author Cool_Loong
 */
public class BlockState2ItemMetaRegistry extends BaseRegistry<String, Integer, Integer> {
    //blockid#meta -> blockhash
    private static final Object2IntOpenHashMap<String> MAP = new Object2IntOpenHashMap<>();

    @Override
    public void init() {
        try (var input = BlockState2ItemMetaRegistry.class.getClassLoader().getResourceAsStream("item_meta_block_state_bimap.nbt")) {
            CompoundTag compoundTag = NBTIO.readCompressed(input);
            for (var entry : compoundTag.getTags().entrySet()) {
                for (var entry2 : ((CompoundTag) entry.getValue()).getTags().entrySet()) {
                    MAP.put(entry.getKey() + "#" + entry2.getKey(), ((IntTag) entry2.getValue()).getData().intValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer get(String key) {
        return MAP.get(key);
    }

    public int get(String key, int meta) {
        return MAP.getInt(key + "#" + meta);
    }


    @Override
    public void trim() {
        MAP.trim();
    }

    @Override
    public OK<?> register(String key, Integer value) {
        if (MAP.putIfAbsent(key, value.intValue()) == 0) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The mapping has been registered!"));
        }
    }
}
