package org.powernukkitx.level.generator.feature;

import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.registry.Registries;

public abstract class MultiFeatureWrapper extends GenerateFeature {

    protected abstract String[] getFeatures();

    @Override
    public final void apply(ChunkGenerateContext context) {
        for(String name : getFeatures()) {
            GenerateFeature feature = Registries.GENERATE_FEATURE.get(name);
            feature.setRoot(this.root);
            feature.apply(context);
        }
    }
}
