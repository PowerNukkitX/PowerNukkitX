package cn.nukkit.level.generator.populator.impl.structure.shipwreck;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.shipwreck.loot.ShipwreckMapChest;
import cn.nukkit.level.generator.populator.impl.structure.shipwreck.loot.ShipwreckSupplyChest;
import cn.nukkit.level.generator.populator.impl.structure.shipwreck.loot.ShipwreckTreasureChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.populator.CallbackableTemplateStructurePopulator;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.ReadOnlyLegacyStructureTemplate;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.ReadableStructureTemplate;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.CallbackableChunkGenerationTask;
import cn.nukkit.level.generator.task.LootSpawnTask;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorShipwreck extends PopulatorStructure implements CallbackableTemplateStructurePopulator {

    protected static final ReadableStructureTemplate WITH_MAST = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swwithmast.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_FULL = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownfull.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_FRONTHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownfronthalf.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_BACKHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownbackhalf.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_FULL = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysfull.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_FRONTHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysfronthalf.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_BACKHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysbackhalf.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_FULL = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupfull.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_FRONTHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupfronthalf.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_BACKHALF = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupbackhalf.nbt"));
    protected static final ReadableStructureTemplate WITH_MAST_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swwithmastdegraded.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_FULL_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownfulldegraded.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_FRONTHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownfronthalfdegraded.nbt"));
    protected static final ReadableStructureTemplate UPSIDEDOWN_BACKHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swupsidedownbackhalfdegraded.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_FULL_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysfulldegraded.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_FRONTHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysfronthalfdegraded.nbt"));
    protected static final ReadableStructureTemplate SIDEWAYS_BACKHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swsidewaysbackhalfdegraded.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_FULL_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupfulldegraded.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_FRONTHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupfronthalfdegraded.nbt"));
    protected static final ReadableStructureTemplate RIGHTSIDEUP_BACKHALF_DEGRADED = new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/shipwreck/swrightsideupbackhalfdegraded.nbt"));

    protected static final ReadableStructureTemplate[] STRUCTURE_LOCATION_BEACHED = new ReadableStructureTemplate[]{
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

    protected static final ReadableStructureTemplate[] STRUCTURE_LOCATION_OCEAN = new ReadableStructureTemplate[]{
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
    protected static boolean[] VALID_BIOMES = new boolean[256];
    protected static boolean[] VALID_BEACH_BIOMES = new boolean[256];

    static {
        // Oceans
        VALID_BIOMES[EnumBiome.OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.FROZEN_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.LUKEWARM_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.COLD_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.WARM_OCEAN.id] = true;

        //Deep Oceans
        VALID_BIOMES[EnumBiome.DEEP_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.DEEP_WARM_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.DEEP_LUKEWARM_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.DEEP_COLD_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.DEEP_FROZEN_OCEAN.id] = true;

        //Beach
        VALID_BIOMES[EnumBiome.BEACH.id] = VALID_BEACH_BIOMES[EnumBiome.BEACH.id] = true;
        VALID_BIOMES[EnumBiome.COLD_BEACH.id] = VALID_BEACH_BIOMES[EnumBiome.COLD_BEACH.id] = true;
    }

    protected static final int SPACING = 24;
    protected static final int SEPARATION = 4;

    protected final Map<Long, Set<Long>> waitingChunks = Maps.newConcurrentMap();

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(5, chunk.getHighestBlockAt(5, 5), 5);
        if (chunk.getProvider().isOverWorld() && VALID_BIOMES[biome]
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            ReadableStructureTemplate template;

            if (VALID_BEACH_BIOMES[biome]) {
                template = STRUCTURE_LOCATION_BEACHED[random.nextBoundedInt(STRUCTURE_LOCATION_BEACHED.length)];
            } else {
                template = STRUCTURE_LOCATION_OCEAN[random.nextBoundedInt(STRUCTURE_LOCATION_OCEAN.length)];
            }

            BlockVector3 size = template.getSize();
            int sumY = 0;
            int blockCount = 0;

            for (int x = 0; x < size.getX() && x < 16; x++) {
                for (int z = 0; z < size.getZ() && z < 16; z++) {
                    int y = chunk.getHighestBlockAt(x, z);

                    int id = chunk.getBlockId(x, y, z);
                    while (FILTER[id] && y > -64) {
                        id = chunk.getBlockId(x, --y, z);
                    }

                    sumY += y;
                    blockCount++;
                }
            }

            int y = sumY / blockCount;

            int seed = random.nextInt();
            boolean isLarge = false;

            Set<BaseFullChunk> chunks = Sets.newHashSet();
            Set<Long> indexes = Sets.newConcurrentHashSet();

            if (size.getX() > 16) {
                isLarge = true;

                BaseFullChunk ck = level.getChunk(chunkX + 1, chunkZ);
                if (!ck.isGenerated()) {
                    chunks.add(ck);
                    indexes.add(Level.chunkHash(ck.getX(), chunkZ));
                }
            }
            if (size.getZ() > 16) {
                isLarge = true;

                BaseFullChunk ck = level.getChunk(chunkX, chunkZ + 1);
                if (!ck.isGenerated()) {
                    chunks.add(ck);
                    indexes.add(Level.chunkHash(chunkX, ck.getZ()));
                }
            }

            if (!chunks.isEmpty()) {
                this.waitingChunks.put(Level.chunkHash(chunkX, chunkZ), indexes);
                for (BaseFullChunk ck : chunks) {
                    Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<CallbackableTemplateStructurePopulator>(
                            chunk.getProvider().getLevel(), ck, this,
                            populator -> populator.generateChunkCallback(template, seed, level, chunkX, chunkZ, y, ck.getX(), ck.getZ())));
                }
                return;
            }

            if (isLarge) {
                this.placeInLevel(level, chunkX, chunkZ, template, seed, y);
            } else {
                random.setSeed(seed);

                BlockVector3 vec = new BlockVector3(chunkX << 4, y, chunkZ << 4);
                template.placeInChunk(chunk, random, vec, 100, getBlockActorProcessor(chunk, random));
            }
        }
    }

    protected void placeInLevel(ChunkManager level, int chunkX, int chunkZ, ReadableStructureTemplate template, int seed, int y) {
        NukkitRandom random = new NukkitRandom(seed);

        BlockVector3 vec = new BlockVector3(chunkX << 4, y, chunkZ << 4);
        template.placeInLevel(level, random, vec, 100, getBlockActorProcessor(level.getChunk(chunkX, chunkZ), random));

        this.waitingChunks.remove(Level.chunkHash(chunkX, chunkZ));
    }

    @Override
    public void generateChunkCallback(ReadableStructureTemplate template, int seed, ChunkManager level, int startChunkX, int startChunkZ, int y, int chunkX, int chunkZ) {
        Set<Long> indexes = this.waitingChunks.get(Level.chunkHash(startChunkX, startChunkZ));
        indexes.remove(Level.chunkHash(chunkX, chunkZ));
        if (indexes.isEmpty()) {
            this.placeInLevel(level, startChunkX, startChunkZ, template, seed, y);
        }
    }

    protected static Consumer<CompoundTag> getBlockActorProcessor(FullChunk chunk, NukkitRandom random) {
        return nbt -> {
            if (nbt.getString("id").equals(BlockEntity.STRUCTURE_BLOCK)) {
                switch (nbt.getString("metadata")) {
                    case "supplyChest":
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        ShipwreckSupplyChest.get().create(itemList, random);

                        Server.getInstance().getScheduler().scheduleDelayedTask(new LootSpawnTask(chunk.getProvider().getLevel(),
                                new BlockVector3(nbt.getInt("x"), nbt.getInt("y") - 1, nbt.getInt("z")), itemList), 2);
                        break;
                    case "mapChest":
                        itemList = new ListTag<>("Items");
                        ShipwreckMapChest.get().create(itemList, random);

                        Server.getInstance().getScheduler().scheduleDelayedTask(new LootSpawnTask(chunk.getProvider().getLevel(),
                                new BlockVector3(nbt.getInt("x"), nbt.getInt("y") - 1, nbt.getInt("z")), itemList), 2);
                        break;
                    case "treasureChest":
                        itemList = new ListTag<>("Items");
                        ShipwreckTreasureChest.get().create(itemList, random);

                        Server.getInstance().getScheduler().scheduleDelayedTask(new LootSpawnTask(chunk.getProvider().getLevel(),
                                new BlockVector3(nbt.getInt("x"), nbt.getInt("y") - 1, nbt.getInt("z")), itemList), 2);
                        break;
                }
            }
        };
    }

    public static final boolean[] FILTER = new boolean[2048];

    static {
        FILTER[AIR] = true;
        FILTER[LOG] = true;
        FILTER[FLOWING_WATER] = true;
        FILTER[STILL_WATER] = true;
        FILTER[FLOWING_LAVA] = true;
        FILTER[STILL_LAVA] = true;
        FILTER[LEAVES] = true;
        FILTER[TALL_GRASS] = true;
        FILTER[DEAD_BUSH] = true;
        FILTER[DANDELION] = true;
        FILTER[RED_FLOWER] = true;
        FILTER[BROWN_MUSHROOM] = true;
        FILTER[RED_MUSHROOM] = true;
        FILTER[SNOW_LAYER] = true;
        FILTER[ICE] = true;
        FILTER[CACTUS] = true;
        FILTER[REEDS] = true;
        FILTER[PUMPKIN] = true;
        FILTER[BROWN_MUSHROOM_BLOCK] = true;
        FILTER[RED_MUSHROOM_BLOCK] = true;
        FILTER[MELON_BLOCK] = true;
        FILTER[VINE] = true;
        FILTER[WATERLILY] = true;
        FILTER[COCOA] = true;
        FILTER[LEAVES2] = true;
        FILTER[LOG2] = true;
        FILTER[PACKED_ICE] = true;
        FILTER[DOUBLE_PLANT] = true;
    }

    private static CompoundTag loadNBT(String path) {
        try (InputStream inputStream = PopulatorShipwreck.class.getModule().getResourceAsStream(path)) {
            return NBTIO.readCompressed(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Since("1.19.21-r2")
    @Override
    public boolean isAsync() {
        return true;
    }
}
