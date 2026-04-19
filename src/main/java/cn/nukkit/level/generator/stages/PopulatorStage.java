package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PopulatorStage extends GenerateStage {

    @Override
    public final void apply(ChunkGenerateContext context) {
        context.getChunk().setChunkState(ChunkState.POPULATED);
        BlockManager root = new BlockManager(context.getLevel());
        for(String name : populators()) {
            try {
                Populator populator = Registries.POPULATOR.get(name);
                populator.setRoot(root);
                populator.apply(context);
            } catch (Exception e) {
                log.error("Error while applying populator {}", name, e);
            }
        }
        root.applySubChunkUpdate();
    }

    public abstract ObjectArraySet<String> populators();

    @Override
    public abstract String name();

}
