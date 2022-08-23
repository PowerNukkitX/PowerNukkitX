package cn.nukkit.level.generator.populator.impl.structure.desertpyramid;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.impl.structure.desertpyramid.structure.DesertPyramid;
import cn.nukkit.level.generator.populator.impl.structure.utils.populator.PopulatorScatteredStructure;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.NukkitRandom;


@PowerNukkitXOnly
@Since("1.19.21-r6")
public class PopulatorDesertPyramid extends PopulatorScatteredStructure {

    @Override
    protected boolean canGenerate(int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, 7);
        return (biome == EnumBiome.DESERT.id || biome == EnumBiome.DESERT_HILLS.id || biome == EnumBiome.DESERT_M.id) && super.canGenerate(chunkX, chunkZ, random, chunk);
    }

    @Override
    protected ScatteredStructurePiece getPiece(int chunkX, int chunkZ) {
        return new DesertPyramid(this.getStart(chunkX, chunkZ));
    }
}
