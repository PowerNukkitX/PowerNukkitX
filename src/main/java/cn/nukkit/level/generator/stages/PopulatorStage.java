package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PopulatorStage extends GenerateStage {

    @Override
    public final void apply(ChunkGenerateContext context) {
        for(String name : populators()) {
            try {
                Populator populator = Registries.POPULATOR.get(name);
                populator.apply(context);
            } catch (Exception e) {
                log.error("Error while applying populator " + name, e);
            }
        }
        context.getChunk().setChunkState(ChunkState.POPULATED);
    }

    public abstract ObjectArraySet<String> populators();

    @Override
    public abstract String name();

}
