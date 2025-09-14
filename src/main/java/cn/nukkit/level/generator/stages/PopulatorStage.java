package cn.nukkit.level.generator.stages;

import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public abstract class PopulatorStage extends GenerateStage {

    @Override
    public final void apply(ChunkGenerateContext context) {
        for(String name : populators()) {
            Populator populator = Registries.POPULATOR.get(name);
            populator.apply(context);
        }
    }

    public abstract ObjectArraySet<String> populators();

    @Override
    public abstract String name();

}
