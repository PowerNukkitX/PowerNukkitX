package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.TheEndBiomePicker;
import org.powernukkitx.level.generator.biome.result.TheEndBiomeResult;
import org.powernukkitx.level.generator.holder.TheEndObjectHolder;
import org.powernukkitx.level.generator.holder.ObjectHolder;
import org.powernukkitx.level.generator.stages.BiomeMapStage;
import org.powernukkitx.level.generator.stages.GeneratedStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.level.generator.stages.end.TheEndPopulatorStage;
import org.powernukkitx.level.generator.stages.end.TheEndTerrainStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.Map;

public class TheEnd extends PopulatedGenerator implements BiomedGenerator {

    public TheEnd(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(TheEndTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));

        builder.next(Registries.GENERATE_STAGE.get(TheEndPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public BiomePicker<TheEndBiomeResult> createBiomePicker(Level level) {
        return new TheEndBiomePicker();
    }

    @Override
    public ObjectHolder createObjectHolder(Level level) {
        return new TheEndObjectHolder(new Xoroshiro128(level.getSeed()));
    }

    @Override
    public String getName() {
        return "the_end";
    }
}
