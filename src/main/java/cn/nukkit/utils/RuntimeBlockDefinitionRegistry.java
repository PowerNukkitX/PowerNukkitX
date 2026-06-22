package cn.nukkit.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Value;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;

/**
 * @author Kaooot
 * @version 1.0
 */
public class RuntimeBlockDefinitionRegistry implements DefinitionRegistry<BlockDefinition> {

    private final Int2ObjectMap<BlockDefinition> definitions = new Int2ObjectOpenHashMap<>();

    public RuntimeBlockDefinitionRegistry() {

    }

    @Override
    public BlockDefinition getDefinition(int runtimeId) {
        return this.definitions.computeIfAbsent(runtimeId, RuntimeBlockDefinition::new);
    }

    @Override
    public boolean isRegistered(BlockDefinition definition) {
        return true;
    }

    @Value
    public static class RuntimeBlockDefinition implements BlockDefinition {
        int runtimeId;
    }
}