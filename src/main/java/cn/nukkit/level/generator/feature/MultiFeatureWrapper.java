package cn.nukkit.level.generator.feature;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.registry.Registries;

public abstract class MultiFeatureWrapper extends GenerateFeature {

    protected abstract String[] getFeatures();

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        for(String name : getFeatures()) {
            Registries.GENERATE_FEATURE.get(name).apply(context);
        }
    }
}
