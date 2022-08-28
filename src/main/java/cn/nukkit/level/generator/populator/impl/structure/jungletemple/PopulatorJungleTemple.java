package cn.nukkit.level.generator.populator.impl.structure.jungletemple;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.impl.structure.jungletemple.structure.JungleTemple;
import cn.nukkit.level.generator.populator.impl.structure.utils.populator.PopulatorScatteredStructure;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.NukkitRandom;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorJungleTemple extends PopulatorScatteredStructure {

    @Override
    protected boolean canGenerate(int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, chunk.getHighestBlockAt(7, 7), 7);
        return (biome == EnumBiome.JUNGLE.id || biome == EnumBiome.JUNGLE_EDGE.id || biome == EnumBiome.JUNGLE_EDGE_M.id || biome == EnumBiome.JUNGLE_HILLS.id || biome == EnumBiome.JUNGLE_M.id) && super.canGenerate(chunkX, chunkZ, random, chunk);
    }

    @Override
    protected ScatteredStructurePiece getPiece(int chunkX, int chunkZ) {
        return new JungleTemple(this.getStart(chunkX, chunkZ));
    }
}
