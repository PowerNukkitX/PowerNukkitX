package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.TheEndBiomePicker;
import cn.nukkit.level.generator.biome.result.TheEndBiomeResult;
import cn.nukkit.level.generator.holder.TheEndObjectHolder;
import cn.nukkit.level.generator.holder.ObjectHolder;
import cn.nukkit.level.generator.stages.BiomeMapStage;
import cn.nukkit.level.generator.stages.GeneratedStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.end.TheEndPopulatorStage;
import cn.nukkit.level.generator.stages.end.TheEndTerrainStage;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.Xoroshiro128;

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
    public BiomePicker<TheEndBiomeResult> createBiomePicker(Dimension level) {
        return new TheEndBiomePicker();
    }

    @Override
    public ObjectHolder createObjectHolder(Dimension level) {
        return new TheEndObjectHolder(new Xoroshiro128(level.getSeed()));
    }

    @Override
    public String getName() {
        return "the_end";
    }
}
