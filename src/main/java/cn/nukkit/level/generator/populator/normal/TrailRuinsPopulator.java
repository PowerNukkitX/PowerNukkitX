package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.trailruins.TrailRuinsStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;

/**
 * Trail Ruins for PowerNukkitX
 * @author Buddelbubi
 * @since 2026/03/31
 */
public class TrailRuinsPopulator extends Populator {

    public static final String NAME = "normal_trail_ruins";

    private static final TrailRuinsStructure TRAIL_RUINS = new TrailRuinsStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = findGenerationY(chunk, level);
        if (!TRAIL_RUINS.canGenerateAt(new Location(originX, originY, originZ, level))) {
            return;
        }
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        TRAIL_RUINS.place(helper, random.fork());
    }

    private int findGenerationY(IChunk chunk, Level level) {
        int worldX = (chunk.getX() << 4) + 7;
        int worldZ = (chunk.getZ() << 4) + 7;
        int y = level.getHeightMap(worldX, worldZ);
        while (y > level.getMinHeight() && level.getBlock(worldX, y, worldZ).canBeReplaced()) {
            y--;
        }
        return Math.max(level.getMinHeight() + 1, y - 15);
    }

    @Override
    public String name() {
        return NAME;
    }
}
