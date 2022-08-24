package cn.nukkit.level.generator.populator.impl.structure.swamphut;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.impl.structure.swamphut.structure.SwampHut;
import cn.nukkit.level.generator.populator.impl.structure.utils.populator.PopulatorScatteredStructure;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.NukkitRandom;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorSwampHut extends PopulatorScatteredStructure {

    @Override
    protected boolean canGenerate(int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, chunk.getHighestBlockAt(7, 7), 7);
        return (biome == EnumBiome.SWAMP.id || biome == EnumBiome.SWAMPLAND_M.id) && super.canGenerate(chunkX, chunkZ, random, chunk);
    }

    @Override
    protected ScatteredStructurePiece getPiece(int chunkX, int chunkZ) {
        return new SwampHut(this.getStart(chunkX, chunkZ));
    }
}
