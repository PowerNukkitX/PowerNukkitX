package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.block.BlockWater;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class ShipwreckPopulator extends Populator {

    public static final String NAME = "normal_shipwreck";

    protected static final PNXStructure WITH_MAST = (PNXStructure) Registries.STRUCTURE.get("shipwreck/with_mast");
    protected static final PNXStructure UPSIDEDOWN_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_full");
    protected static final PNXStructure UPSIDEDOWN_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_fronthalf");
    protected static final PNXStructure UPSIDEDOWN_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_bachhalf");
    protected static final PNXStructure SIDEWAYS_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_full");
    protected static final PNXStructure SIDEWAYS_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_fronthalf");
    protected static final PNXStructure SIDEWAYS_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_backhalf");
    protected static final PNXStructure RIGHTSIDEUP_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_full");
    protected static final PNXStructure RIGHTSIDEUP_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_fronthalf");
    protected static final PNXStructure RIGHTSIDEUP_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_backhalf");
    protected static final PNXStructure WITH_MAST_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/with_mast_degraded");
    protected static final PNXStructure UPSIDEDOWN_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_full_degraded");
    protected static final PNXStructure UPSIDEDOWN_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_fronthalf_degraded");
    protected static final PNXStructure UPSIDEDOWN_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_bachhalf_degraded");
    protected static final PNXStructure SIDEWAYS_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_full_degraded");
    protected static final PNXStructure SIDEWAYS_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_fronthalf_degraded");
    protected static final PNXStructure SIDEWAYS_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_backhalf_degraded");
    protected static final PNXStructure RIGHTSIDEUP_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_full_degraded");
    protected static final PNXStructure RIGHTSIDEUP_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_fronthalf_degraded");
    protected static final PNXStructure RIGHTSIDEUP_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_backhalf_degraded");
    protected static final PNXStructure[] STRUCTURE_LOCATION_BEACHED = new PNXStructure[]{
            WITH_MAST,
            SIDEWAYS_FULL,
            SIDEWAYS_FRONTHALF,
            SIDEWAYS_BACKHALF,
            RIGHTSIDEUP_FULL,
            RIGHTSIDEUP_FRONTHALF,
            RIGHTSIDEUP_BACKHALF,
            WITH_MAST_DEGRADED,
            RIGHTSIDEUP_FULL_DEGRADED,
            RIGHTSIDEUP_FRONTHALF_DEGRADED,
            RIGHTSIDEUP_BACKHALF_DEGRADED
    };
    protected static final PNXStructure[] STRUCTURE_LOCATION_OCEAN = new PNXStructure[]{
            WITH_MAST,
            UPSIDEDOWN_FULL,
            UPSIDEDOWN_FRONTHALF,
            UPSIDEDOWN_BACKHALF,
            SIDEWAYS_FULL,
            SIDEWAYS_FRONTHALF,
            SIDEWAYS_BACKHALF,
            RIGHTSIDEUP_FULL,
            RIGHTSIDEUP_FRONTHALF,
            RIGHTSIDEUP_BACKHALF,
            WITH_MAST_DEGRADED,
            UPSIDEDOWN_FULL_DEGRADED,
            UPSIDEDOWN_FRONTHALF_DEGRADED,
            UPSIDEDOWN_BACKHALF_DEGRADED,
            SIDEWAYS_FULL_DEGRADED,
            SIDEWAYS_FRONTHALF_DEGRADED,
            SIDEWAYS_BACKHALF_DEGRADED,
            RIGHTSIDEUP_FULL_DEGRADED,
            RIGHTSIDEUP_FRONTHALF_DEGRADED,
            RIGHTSIDEUP_BACKHALF_DEGRADED
    };
    protected static final int SPACING = 24;
    protected static final int SEPARATION = 4;


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(5, chunk.getHeightMap(5, 5), 5);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if ((definition.getTags().contains(BiomeTags.OCEAN) || definition.getTags().contains(BiomeTags.BEACH))
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            PNXStructure template;
            boolean beach = definition.getTags().contains(BiomeTags.BEACH);
            if (beach) {
                template = STRUCTURE_LOCATION_BEACHED[random.nextBoundedInt(STRUCTURE_LOCATION_BEACHED.length)];
            } else {
                template = STRUCTURE_LOCATION_OCEAN[random.nextBoundedInt(STRUCTURE_LOCATION_OCEAN.length)];
            }

            BlockVector3 size = new BlockVector3(template.getSizeX(), template.getSizeY(), template.getSizeZ());
            int sumY = 0;
            int blockCount = 0;

            for (int x = 0; x < size.getX() && x < 16; x++) {
                for (int z = 0; z < size.getZ() && z < 16; z++) {
                    int y = chunk.getHeightMap(x, z);

                    Block b = chunk.getBlockState(x, y, z).toBlock();
                    while (b.canBeReplaced() && y > level.getMinHeight()) {
                        b = chunk.getBlockState(x, --y, z).toBlock();
                    }

                    sumY += y;
                    blockCount++;
                }
            }

            int y = sumY / blockCount;

            Set<IChunk> chunks = Sets.newHashSet();
            Set<Long> indexes = Sets.newConcurrentHashSet();

            if (size.getX() > 16) {
                IChunk ck = level.getChunk(chunkX + 1, chunkZ);
                if (!ck.isGenerated()) {
                    chunks.add(ck);
                    indexes.add(Level.chunkHash(ck.getX(), chunkZ));
                }
            }
            if (size.getZ() > 16) {
                IChunk ck = level.getChunk(chunkX, chunkZ + 1);
                if (!ck.isGenerated()) {
                    chunks.add(ck);
                    indexes.add(Level.chunkHash(chunkX, ck.getZ()));
                }
            }



            BlockManager manager = new BlockManager(level);
            this.placeInLevel(manager, chunkX, chunkZ, template, y);
            for(Block block : manager.getBlocks()) {
                if(block instanceof BlockAir) manager.unsetBlockStateAt(block);
                if(block instanceof BlockStructureBlock) manager.unsetBlockStateAt(block);
                if(block.getFloorY() <= SEA_LEVEL) {
                    manager.getLevel().setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), 1, BlockWater.PROPERTIES.getDefaultState());
                }
            }
            queueObject(chunk, manager);
        }
    }

    protected void placeInLevel(BlockManager manager, int chunkX, int chunkZ, PNXStructure template, int y) {
        Position vec = new Position(chunkX << 4, y, chunkZ << 4, manager.getLevel());
        template.preparePlace(vec, manager);
    }


    @Override
    public String name() {
        return NAME;
    }
}
