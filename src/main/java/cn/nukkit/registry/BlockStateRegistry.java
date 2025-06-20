package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.utils.BlockColor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public final class BlockStateRegistry implements IRegistry<Integer, BlockState, BlockState> {
    private static final Int2ObjectOpenHashMap<BlockState> REGISTRY = new Int2ObjectOpenHashMap<>();

    @Override
    public void init() {
        try (var stream = this.getClass().getClassLoader().getResourceAsStream("gamedata/endstone/block_states.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonArray blockStateData = JsonParser.parseReader(reader).getAsJsonArray();
            for(int i = 0; i < blockStateData.size(); i++) {
                JsonObject entry = blockStateData.get(i).getAsJsonObject();
                int hash = entry.get("blockStateHash").getAsInt();
                String name = entry.get("name").getAsString();
                if(BlockRegistry.skipBlockSet.contains(name)) continue;
                BlockState state = Registries.BLOCKSTATE.get(hash);
                if(state == null) {
                    Server.getInstance().getLogger().alert(name + " (" + hash + ") was not a part of block_states.json.");
                } else {
                    if(!state.getIdentifier().equals(name)) {
                        Server.getInstance().getLogger().alert("BlockState " + hash + " was not " + name + ". Instead it is " + state.getIdentifier());
                    }
                }
                String hexString = entry.get("mapColor").getAsString().substring(1, 9);
                int r = Integer.parseInt(hexString.substring(0,2), 16);
                int g = Integer.parseInt(hexString.substring(2,4), 16);
                int b = Integer.parseInt(hexString.substring(4,6), 16);
                int a = Integer.parseInt(hexString.substring(6,8), 16);
                BlockColor.Tint tint = BlockColor.Tint.get(entry.get("tintMethod").toString().replace("\"", ""));
                Block.VANILLA_BLOCK_COLOR_MAP.put(hash, new BlockColor(r, g, b, a, tint));
            }
        } catch (IOException e) {
        }
    }

    @Override
    public BlockState get(Integer key) {
        return REGISTRY.get(key.intValue());
    }

    public BlockState get(int blockHash) {
        return REGISTRY.get(blockHash);
    }

    @UnmodifiableView
    public Set<BlockState> getAllState() {
        return Set.copyOf(REGISTRY.values());
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        REGISTRY.clear();
    }

    @Override
    public void register(Integer key, BlockState value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) == null) {
        } else {
            throw new RegisterException("The blockstate has been registered!");
        }
    }

    public void register(BlockState value) throws RegisterException {
        BlockState now;
        if ((now = REGISTRY.put(value.blockStateHash(), value)) == null) {
        } else {
            throw new RegisterException("The blockstate " + value + "has been registered,\n current value: " + now);
        }
    }

    @ApiStatus.Internal
    public void registerInternal(BlockState value) {
        try {
            register(value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
