package cn.nukkit.level.generator.populator.impl.structure.oceanruin;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.ChunkPosition;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.oceanruin.loot.RuinBigChest;
import cn.nukkit.level.generator.populator.impl.structure.oceanruin.loot.RuinSmallChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.ReadOnlyLegacyStructureTemplate;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.ReadableStructureTemplate;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.StructurePlaceSettings;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.level.generator.task.CallbackableChunkGenerationTask;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Utils;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorOceanRuin extends PopulatorStructure {

    protected static final List<ChunkPosition> ADJACENT_CHUNKS = Lists.newArrayList(
            new ChunkPosition(-1, 0, -1),
            new ChunkPosition(-1, 0, 0),
            new ChunkPosition(-1, 0, 1),
            new ChunkPosition(0, 0, -1),
            new ChunkPosition(0, 0, 1),
            new ChunkPosition(1, 0, -1),
            new ChunkPosition(1, 0, 0),
            new ChunkPosition(1, 0, 1)
    );

    protected static final int SPACING = 20;
    protected static final int SEPARATION = 8;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, chunk.getHighestBlockAt(7, 7), 7);
        if (chunk.getProvider().isOverWorld() && (biome == EnumBiome.OCEAN.id || biome == EnumBiome.FROZEN_OCEAN.id || biome == EnumBiome.DEEP_OCEAN.id || biome >= 44 && biome <= 50)
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
//            boolean isWarm = biome == 44 || biome == 45 || biome == 47 || biome == 48;
            boolean isWarm = random.nextBoundedInt(10) < 4;
            boolean isLarge = random.nextBoundedInt(100) <= 30;

            ReadableStructureTemplate template;
            int index;

            if (isWarm) {
                index = -1;
                if (isLarge) {
                    template = BIG_WARM_RUINS[random.nextBoundedInt(BIG_WARM_RUINS.length)];
                } else {
                    template = WARM_RUINS[random.nextBoundedInt(WARM_RUINS.length)];
                }
            } else if (isLarge) {
                index = random.nextBoundedInt(BIG_RUINS_BRICK.length);
                template = BIG_RUINS_BRICK[index];
            } else {
                index = random.nextBoundedInt(RUINS_BRICK.length);
                template = RUINS_BRICK[index];
            }

            this.placeRuin(template, chunk, random.nextInt(), isLarge, index);

            if (isLarge && random.nextBoundedInt(100) <= 90) {
                List<ChunkPosition> adjacentChunks = Lists.newArrayList(ADJACENT_CHUNKS);
                for (int i = 0; i < random.nextRange(4, 8); i++) {
                    ChunkPosition chunkPos = adjacentChunks.remove(random.nextBoundedInt(adjacentChunks.size()));
                    this.placeAdjacentRuin(level.getChunk(chunkX + chunkPos.x, chunkZ + chunkPos.z), random, isWarm);
                }
            }
        }
    }

    protected void placeAdjacentRuin(BaseFullChunk chunk, NukkitRandom random, boolean isWarm) {
        ReadableStructureTemplate template;
        int index;

        if (isWarm) {
            template = WARM_RUINS[random.nextBoundedInt(WARM_RUINS.length)];
            index = -1;
        } else {
            index = random.nextBoundedInt(RUINS_BRICK.length);
            template = RUINS_BRICK[random.nextBoundedInt(RUINS_BRICK.length)];
        }

        int seed = random.nextInt();

        if (!chunk.isGenerated()) {
            Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<>(
                    chunk.getProvider().getLevel(), chunk, this,
                    populator -> populator.placeRuin(template, chunk, seed, false, index)));
        } else {
            this.placeRuin(template, chunk, seed, false, index);
        }
    }

    protected void placeRuin(ReadableStructureTemplate template, FullChunk chunk, int seed, boolean isLarge, int index) {
        NukkitRandom random = new NukkitRandom(seed);

        BlockVector3 size = template.getSize();
        int x = random.nextBoundedInt(16 - size.getX());
        int z = random.nextBoundedInt(16 - size.getZ());
        int y = 256;

        for (int cx = x; cx < x + size.getX(); cx++) {
            for (int cz = z; cz < z + size.getZ(); cz++) {
                int h = chunk.getHighestBlockAt(cx, cz);

                int id = chunk.getBlockId(cx, h, cz);
                while (Utils.isPlantOrFluid[id] && h > 1) {
                    id = chunk.getBlockId(cx, --h, cz);
                }

                y = Math.min(h, y);
            }
        }

        BlockVector3 vec = new BlockVector3((chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z);
        template.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                .setIgnoreAir(true)
                .setIntegrity(isLarge ? 90 : 80)
                .setBlockActorProcessor(isLarge ? getBigRuinProcessor(chunk, random) : getSmallRuinProcessor(chunk, random)));

        if (index != -1) {
            ReadableStructureTemplate mossy;
            ReadableStructureTemplate cracked;

            if (isLarge) {
                mossy = BIG_RUINS_MOSSY[index];
                cracked = BIG_RUINS_CRACKED[index];
            } else {
                mossy = RUINS_MOSSY[index];
                cracked = RUINS_CRACKED[index];
            }

            mossy.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                    .setIgnoreAir(true)
                    .setIntegrity(70)
                    .setBlockActorProcessor(isLarge ? getBigRuinProcessor(chunk, random) : getSmallRuinProcessor(chunk, random)));
            cracked.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                    .setIgnoreAir(true)
                    .setIntegrity(50)
                    .setBlockActorProcessor(isLarge ? getBigRuinProcessor(chunk, random) : getSmallRuinProcessor(chunk, random)));
        }
    }

    protected static Consumer<CompoundTag> getSmallRuinProcessor(FullChunk chunk, NukkitRandom random) {
        return nbt -> {
            if (nbt.getString("id").equals(BlockEntity.STRUCTURE_BLOCK) && "chest".equals(nbt.getString("metadata"))) {
                BlockVector3 pos = new BlockVector3(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                CompoundTag tag = BlockEntity.getDefaultCompound(pos.asVector3(), BlockEntity.CHEST);

                ListTag<CompoundTag> items = new ListTag<>("Items");
                RuinSmallChest.get().create(items, random);
                tag.putList(items);

                chunk.setBlock(pos.getX() & 0xf, pos.getY(), pos.getZ() & 0xf, CHEST, 2);
                Server.getInstance().getScheduler().scheduleDelayedTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), tag), 2);
            }
        };
    }

    protected static Consumer<CompoundTag> getBigRuinProcessor(FullChunk chunk, NukkitRandom random) {
        return nbt -> {
            if (nbt.getString("id").equals(BlockEntity.STRUCTURE_BLOCK) && "chest".equals(nbt.getString("metadata"))) {
                BlockVector3 pos = new BlockVector3(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                CompoundTag tag = BlockEntity.getDefaultCompound(pos.asVector3(), BlockEntity.CHEST);

                ListTag<CompoundTag> items = new ListTag<>("Items");
                RuinBigChest.get().create(items, random);
                tag.putList(items);

                chunk.setBlock(pos.getX() & 0xf, pos.getY(), pos.getZ() & 0xf, CHEST, 2);
                Server.getInstance().getScheduler().scheduleDelayedTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), tag), 2);
            }
        };
    }

    protected static final ReadableStructureTemplate[] WARM_RUINS = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm1.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm2.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm3.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm4.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm5.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm6.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm7.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin_warm8.nbt"))
    };

    protected static final ReadableStructureTemplate[] RUINS_BRICK = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin1_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin2_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin3_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin4_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin5_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin6_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin7_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin8_brick.nbt"))
    };

    protected static final ReadableStructureTemplate[] RUINS_CRACKED = { //70
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin1_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin2_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin3_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin4_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin5_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin6_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin7_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin8_cracked.nbt"))
    };

    protected static final ReadableStructureTemplate[] RUINS_MOSSY = { //50
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin1_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin2_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin3_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin4_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin5_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin6_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin7_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/ruin8_mossy.nbt"))
    };

    protected static final ReadableStructureTemplate[] BIG_WARM_RUINS = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin_warm4.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin_warm5.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin_warm6.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin_warm7.nbt"))
    };

    protected static final ReadableStructureTemplate[] BIG_RUINS_BRICK = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin1_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin2_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin3_brick.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin8_brick.nbt"))
    };

    protected static final ReadableStructureTemplate[] BIG_RUINS_MOSSY = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin1_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin2_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin3_cracked.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin8_cracked.nbt"))
    };

    protected static final ReadableStructureTemplate[] BIG_RUINS_CRACKED = {
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin1_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin2_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin3_mossy.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(loadNBT("structures/ruin/big_ruin8_mossy.nbt"))
    };

    private static CompoundTag loadNBT(String path) {
        try (InputStream inputStream = PopulatorOceanRuin.class.getModule().getResourceAsStream(path)) {
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
