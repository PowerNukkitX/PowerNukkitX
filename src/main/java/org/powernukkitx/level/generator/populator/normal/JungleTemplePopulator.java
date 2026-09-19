package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.ObjectJungleTemple;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.Vector3;

import java.util.List;

public class JungleTemplePopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_jungle_temple";

    protected static final ObjectJungleTemple JUNGLE_TEMPLE = new ObjectJungleTemple();
    public static final StructurePlacement PLACEMENT = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
            .salt(14357617L)
            .minDistance(8)
            .maxDistance(32)
            .isBiomeValid(biome -> biome == BiomeID.JUNGLE)
            .build(), StructureRandomSpreadPlacement.SpreadType.LINEAR);

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7) - 1, 7);
        if (!PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, biome)) {
            return;
        }

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int x = (chunkX << 4) + random.nextBoundedInt(15);
        int z = (chunkZ << 4) + random.nextBoundedInt(15);
        int y = level.getHeightMap(x, z) - 1;

        BlockManager manager = new BlockManager(level);
        JUNGLE_TEMPLE.generate(manager, random, new Vector3(x, y, z));
        StructureAabbVolumes.addDynamic(
                level,
                "minecraft:jungle_pyramid",
                List.of(new BoundingBox(x, y, z, x + 11, y + 9, z + 14))
        );
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }

}
