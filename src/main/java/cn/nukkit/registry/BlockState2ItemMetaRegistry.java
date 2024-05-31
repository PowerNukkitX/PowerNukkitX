package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cool_Loong
 */
public class BlockState2ItemMetaRegistry implements IRegistry<String, Integer, Integer> {
    //blockid#meta -> blockhash
    private static final Object2IntOpenHashMap<String> MAP = new Object2IntOpenHashMap<>();
    private static final AtomicBoolean $1 = new AtomicBoolean(false);
    @Override
    /**
     * @deprecated 
     */
    
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var $2 = BlockState2ItemMetaRegistry.class.getClassLoader().getResourceAsStream("item_meta_block_state_bimap.nbt")) {
            CompoundTag $3 = NBTIO.readCompressed(input);
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
    /**
     * @deprecated 
     */
    
    public void reload() {
        isLoad.set(false);
        MAP.clear();
        init();
    }

    @Override
    public Integer get(String key) {
        return MAP.get(key);
    }
    /**
     * @deprecated 
     */
    

    public int get(String key, int meta) {
        return MAP.getInt(key + "#" + meta);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void trim() {
        MAP.trim();
    }

    @Override
    public void register(String key, Integer value) throws RegisterException {
        if (MAP.putIfAbsent(key, value.intValue()) == 0) {
        } else {
            throw new RegisterException("The mapping has been registered!");
        }
    }
}
