package cn.nukkit.level.terra.handles;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXEntityType;
import cn.nukkit.utils.Config;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXWorldHandle implements WorldHandle {
    public static Config jeBlockMappingConfig;
    public static Map<State,Object> jeBlockMapping = new HashMap<>();

    static{
        jeBlockMappingConfig = new Config(Config.JSON);
        jeBlockMappingConfig.load(PNXWorldHandle.class.getClassLoader().getResourceAsStream("jeBlocksMapping.json"));
        jeBlockMappingConfig.getAll().forEach((k,v)->{
            jeBlockMapping.put(new State(k),v);
        });
    }

    public static final PNXBlockStateDelegate AIR = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
    public static int finish = 0;//todo: remove these when complete
    public static int err = 0;

    @Override
    public @NotNull
    BlockState createBlockState(@NotNull String s) {
        State jeBlockStateData = new State(s);
        Map<String,Object> mappedData = (Map<String, Object>) jeBlockMapping.get(jeBlockStateData);
        if (mappedData == null) {
            jeBlockStateData.equalsIgnoreAttributes = true;
            mappedData = (Map<String, Object>) jeBlockMapping.get(jeBlockStateData);
        }
        if (mappedData == null) {
            return new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.of(BlockID.AIR));
        }
        boolean hasStates = false;
        Map<String,Object> states = (Map<String, Object>) mappedData.get("bedrock_states");
        Map<String,Object> statesConverted = new HashMap<>();
        if (states != null){
            hasStates = true;
            states.forEach((k, v) -> {
                if (v instanceof Boolean) {
                    if ((Boolean) v) {
                        statesConverted.put(k, 1);
                    } else {
                        statesConverted.put(k, 0);
                    }
                    return;
                }
                if (v instanceof Number){
                    statesConverted.put(k, ((Number) v).intValue());
                    return;
                }
                statesConverted.put(k, v);
            });
        }
        String identifier = (String) mappedData.get("bedrock_identifier");
        StringBuilder data = new StringBuilder();
        data.append(identifier);
        if (hasStates){
            statesConverted.forEach((k,v) -> data.append(";").append(k).append("=").append(v));
        }
        try {
            finish++;
            return new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.of(data.toString()));
        } catch (Exception e) {
            err++;//todo: java.util.NoSuchElementException: Block minecraft:concretePowder not found.
            return new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.of(BlockID.AIR));
        }
    }

    @Override
    public @NotNull
    BlockState air() {
        return AIR;
    }

    @Override
    public @NotNull
    EntityType getEntity(@NotNull String s) {
        if (s.startsWith("minecraft:")) s = s.substring(10);
        return new PNXEntityType(Entity.createEntity(s, new Position(0, 0, 0, Server.getInstance().getDefaultLevel())));
    }

    private static class State{

        public boolean equalsIgnoreAttributes = false;
        private String identifier;
        private Map<String,Object> attributes = new HashMap<>();

        public State(String str) {
            String[] strs = str.replaceAll("\\[",",").replaceAll("]",",").split(",");
            identifier = strs[0];
            if (strs.length>1) {
                for (int i = 1; i < strs.length; i++) {
                    attributes.put(strs[i].split("=")[0], strs[i].split("=")[1]);
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State state = (State) obj;
                if (!equalsIgnoreAttributes) {
                    if (state.identifier.equals(identifier) && state.attributes.equals(attributes)) {
                        return true;
                    }
                }else{
                    if (state.identifier.equals(identifier)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return identifier.hashCode();
        }
    }
}
