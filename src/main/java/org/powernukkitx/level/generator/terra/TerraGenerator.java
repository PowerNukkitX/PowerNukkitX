package org.powernukkitx.level.generator.terra;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.PopulatedGenerator;
import org.powernukkitx.level.generator.populator.generic.PopulatorRuinedPortal;
import org.powernukkitx.level.generator.populator.nether.BastionRemnantPopulator;
import org.powernukkitx.level.generator.populator.nether.NetherFortressPopulator;
import org.powernukkitx.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import org.powernukkitx.level.generator.populator.the_end.EndCityPopulator;
import org.powernukkitx.level.generator.stages.GeneratedStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.PopulatorStage;
import org.powernukkitx.level.generator.stages.normal.NormalPopulatorStage;
import org.powernukkitx.level.generator.terra.delegate.PNXProtoChunk;
import org.powernukkitx.level.generator.terra.delegate.PNXProtoWorld;
import org.powernukkitx.level.generator.terra.delegate.PNXServerWorld;
import org.powernukkitx.registry.Registries;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TerraGenerator extends PopulatedGenerator implements GeneratorWrapper {
    private final BiomeProvider biomeProvider;
    private final ConfigPack configPack;
    private final WorldProperties worldProperties;
    private final ChunkGenerator chunkGenerator;

    public TerraGenerator(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
        String packName = options.getOrDefault("pack", "overworld").toString();
        this.configPack = createConfigPack(packName);
        this.chunkGenerator = createGenerator(this.configPack);
        this.biomeProvider = this.configPack.getBiomeProvider();
        this.worldProperties = new WorldProperties() {

            @Override
            public long getSeed() {
                return level.getSeed();
            }

            @Override
            public int getMaxHeight() {
                return dimensionData.getMaxHeight();
            }

            @Override
            public int getMinHeight() {
                return dimensionData.getMinHeight();
            }

            @Override
            public Object getHandle() {
                return null;
            }
        };
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(new TerrainStage());
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));
        if((boolean) options.getOrDefault("structures", true)) {
            builder.next(new StructureStage());
        }
        builder.next(new PopulateStage());
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    public ConfigPack getConfigPack() {
        return configPack;
    }

    private static ConfigPack createConfigPack(final String packName) {
        Optional<ConfigPack> byID = PNXPlatform.getInstance().getConfigRegistry().getByID(packName);
        return byID.orElseGet(
                () -> PNXPlatform.getInstance().getConfigRegistry().getByID(packName.toUpperCase(Locale.ENGLISH))
                        .orElseThrow(() -> new IllegalArgumentException("Can't find terra config pack " + packName))
        );
    }


    private static ChunkGenerator createGenerator(ConfigPack config) {
        return config.getGeneratorProvider().newInstance(config);
    }

    class TerrainStage extends GenerateStage {
        @Override
        public void apply(ChunkGenerateContext context) {
            final IChunk chunk = context.getChunk();
            final int chunkX = chunk.getX();
            final int chunkZ = chunk.getZ();
            chunkGenerator.generateChunkData(new PNXProtoChunk(chunk), worldProperties, biomeProvider, chunkX, chunkZ);
            final int minHeight = level.getMinHeight();
            final int maxHeight = level.getMaxHeight();
            for (int x = 0; x < 16; x++) {
                for (int y = minHeight; y < maxHeight; y++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.setBiomeId(x, y, z, (Integer) biomeProvider.getBiome(chunkX * 16 + x, y, chunkZ * 16 + z, level.getSeed()).getPlatformBiome().getHandle());
                    }
                }
            }
        }

        @Override
        public String name() {
            return "terra_terrain";
        }
    }

    class StructureStage extends PopulatorStage {

        private final ObjectArraySet<String> populators = new ObjectArraySet<>();

        public StructureStage() {
            populators.addAll(switch (dimensionData.getDimensionId()) {
                case 1 -> List.of(BastionRemnantPopulator.NAME,
                    NetherFortressPopulator.NAME,
                    PopulatorRuinedPortal.NAME,
                    NetherFossilPopulator.NAME);
                case 2 -> List.of(EndCityPopulator.NAME);
                default -> NormalPopulatorStage.POPULATORS.clone();
            });
        }

        @Override
        public ObjectArraySet<String> populators() {
            return populators;
        }

        @Override
        public String name() {
            return "terra_structure";
        }
    }
    class PopulateStage extends GenerateStage {

        @Override
        public void apply(ChunkGenerateContext context) {
            final IChunk chunk = context.getChunk();
            chunk.setChunkState(ChunkState.POPULATED);
            final int chunkX = chunk.getX();
            final int chunkZ = chunk.getZ();
            var tmp1 = new PNXServerWorld(TerraGenerator.this, context.getLevel());
            var tmp = new PNXProtoWorld(tmp1, chunkX, chunkZ);
            try {
                for (var generationStage : configPack.getStages()) {
                    generationStage.populate(tmp);
                }
            } catch (Exception e) {
                log.error("", e);
            }
            tmp1.apply(chunk);
        }

        @Override
        public String name() {
            return "terra_populate";
        }
    }

    public String getName() {
        return "terra";
    }

    @Override
    public ChunkGenerator getHandle() {
        return chunkGenerator;
    }

    public BiomeProvider getBiomeProvider() {
        return biomeProvider;
    }

    public DimensionData getDimensionData() {
        return dimensionData;
    }

    public Level getLevel() {
        return level;
    }
}
