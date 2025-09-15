package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.noise.f.vanilla.NoiseGeneratorPerlinF;
import cn.nukkit.level.generator.noise.spline.JaggednessSpline;
import cn.nukkit.level.generator.noise.spline.OffsetSpline;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalStructureStage extends GenerateStage {

    public static final String NAME = "normal_structure";
    private final ThreadLocal<NukkitRandom> random = ThreadLocal.withInitial(NukkitRandom::new);

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        Level level = chunk.getLevel();
        NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed());

        if(random.nextBoolean()) {
            BlockManager temp = new BlockManager(chunk.getLevel());
            Object2ObjectOpenHashMap<String, AbstractStructure> structures = Registries.GENERATE_STRUCTURE.get(chunk.getBiomeId(0,67,0));
            //if present, pick a random structure
            if(structures != null && !structures.isEmpty()) {
                AbstractStructure structure = (AbstractStructure) structures.values().toArray()[(int) (Math.random() * structures.size())];

                structure.preparePlace(new Position(baseX, chunk.getHeightMap(0, 0), baseZ, level), temp);
            }

            //remove all structure blocks
            for(Block b : temp.getBlocks()) {
                if(b instanceof BlockStructureBlock) {
                    temp.setBlockStateAt((int) b.x, (int) b.y, (int) b.z, BlockAir.STATE);
                }
            }

            temp.applySubChunkUpdate(temp.getBlocks());
        }
    }


    @Override
    public String name() {
        return NAME;
    }
}
