package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.registry.Registries;
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
        if (!root.getBlocks().isEmpty()) {
            root.applySubChunkUpdate();
            root.getBlocks().forEach(block -> block.getChunk().setChanged());
        }
    }

    public abstract ObjectArraySet<String> populators();

    @Override
    public abstract String name();

}
