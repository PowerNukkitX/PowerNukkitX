package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.task.ActorSpawnTask;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Comparator;
import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class ReadOnlyLegacyStructureTemplate extends AbstractLegacyStructureTemplate implements ReadableStructureTemplate {

    @Override
    public ReadableStructureTemplate load(CompoundTag root) {
        this.clean();

        ListTag<IntTag> size = root.getList("size", IntTag.class);
        this.size = new BlockVector3(size.get(0).data, size.get(1).data, size.get(2).data);

        SimplePalette palette = new SimplePalette();

        ListTag<CompoundTag> paletteTag = root.getList("palette", CompoundTag.class);
        for (int i = 0; i < paletteTag.size(); ++i) {
            CompoundTag tag = paletteTag.get(i);
            palette.addMapping(new BlockEntry(tag.getShort("id"), tag.getShort("meta")), i);
        }

        ListTag<CompoundTag> blocks = root.getList("blocks", CompoundTag.class);
        for (int i = 0; i < blocks.size(); ++i) {
            CompoundTag block = blocks.get(i);
            ListTag<IntTag> pos = block.getList("pos", IntTag.class);

            this.blockInfoList.add(new StructureBlockInfo(
                    new BlockVector3(pos.get(0).data, pos.get(1).data, pos.get(2).data),
                    palette.stateFor(block.getInt("state")),
                    block.contains("nbt") ? block.getCompound("nbt") : null));
        }

        this.blockInfoList.sort(Comparator.comparingInt(block -> block.pos.getY()));

        ListTag<CompoundTag> entities = root.getList("entities", CompoundTag.class);
        for (int i = 0; i < entities.size(); ++i) {
            CompoundTag entity = entities.get(i);
            if (entity.contains("nbt")) {
                ListTag<DoubleTag> pos = entity.getList("pos", DoubleTag.class);
                ListTag<IntTag> blockPos = entity.getList("blockPos", IntTag.class);

                this.entityInfoList.add(new StructureEntityInfo(
                        new Vector3(pos.get(0).data, pos.get(1).data, pos.get(2).data),
                        new BlockVector3(blockPos.get(0).data, blockPos.get(1).data, blockPos.get(2).data),
                        entity.getCompound("nbt")));
            }
        }

        return this;
    }

    @Override
    public boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor) {
        if (this.isInvalid() || this.size.getX() > 16 || this.size.getZ() > 16) {
            return false;
        }

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            if (entry.getId() == Block.AIR || integrity <= random.nextBoundedInt(100) && entry.getId() != BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            BlockVector3 vec = blockInfo.pos.add(0, position.getY());
            
            if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                int BlockIDLayer0 = chunk.getBlockId(vec.getX(), vec.getY(), vec.getZ(), 0);
                int BlockIDLayer1 = chunk.getBlockId(vec.getX(), vec.getY(), vec.getZ(), 1);
                boolean shouldFillWater = BlockIDLayer0 == BlockID.FLOWING_WATER || BlockIDLayer0 == BlockID.STILL_WATER ||
                        BlockIDLayer1 == BlockID.FLOWING_WATER || BlockIDLayer1 == BlockID.STILL_WATER;
                chunk.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 0, entry.getId(), entry.getMeta());
                if (shouldFillWater) chunk.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 1, BlockID.STILL_WATER);
            }

            if (blockInfo.nbt != null) {
                CompoundTag nbt = blockInfo.nbt.clone();

                BlockVector3 pos = blockInfo.pos.add(position);
                nbt.putInt("x", pos.getX());
                nbt.putInt("y", pos.getY());
                nbt.putInt("z", pos.getZ());

                if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }

                if (blockActorProcessor != null) {
                    blockActorProcessor.accept(nbt);
                }
            }
        }

        this.placeEntities(chunk.getProvider().getLevel(), position);

        return true;
    }

    @Override
    public boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor) {
        if (this.isInvalid()) {
            return false;
        }

        Level world = level.getChunk(position.getX() >> 4, position.getZ() >> 4).getProvider().getLevel();

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            if (entry.getId() == Block.AIR || integrity <= random.nextBoundedInt(100) && entry.getId() != BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            BlockVector3 vec = blockInfo.pos.add(position);

            if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                int BlockIDLayer0 = level.getBlockIdAt(vec.getX(), vec.getY(), vec.getZ(), 0);
                int BlockIDLayer1 = level.getBlockIdAt(vec.getX(), vec.getY(), vec.getZ(), 1);
                boolean shouldFillWater = BlockIDLayer0 == BlockID.FLOWING_WATER || BlockIDLayer0 == BlockID.STILL_WATER ||
                                          BlockIDLayer1 == BlockID.FLOWING_WATER || BlockIDLayer1 == BlockID.STILL_WATER;
                level.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 0, entry.getId(), entry.getMeta());
                if (shouldFillWater) level.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 1, BlockID.STILL_WATER);
            }

            if (blockInfo.nbt != null) {
                CompoundTag nbt = blockInfo.nbt.clone();

                nbt.putInt("x", vec.getX());
                nbt.putInt("y", vec.getY());
                nbt.putInt("z", vec.getZ());

                if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(world, nbt));
                }

                if (blockActorProcessor != null) {
                    blockActorProcessor.accept(nbt);
                }
            }
        }

        this.placeEntities(world, position);

        return true;
    }

    @Override
    public boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, StructurePlaceSettings settings) {
        if (this.isInvalid() || this.size.getX() > 16 || this.size.getZ() > 16) {
            return false;
        }

        boolean isIgnoreAir = settings.isIgnoreAir();
        int integrity = settings.getIntegrity();
        Consumer<CompoundTag> blockActorProcessor = settings.getBlockActorProcessor();
        boolean isIntact = integrity >= 100;

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            if (entry.getId() == Block.AIR && isIgnoreAir || !isIntact && integrity <= random.nextBoundedInt(100) && entry.getId() != BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            BlockVector3 vec = blockInfo.pos.add(0, position.y);

            if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                int BlockIDLayer0 = chunk.getBlockId(vec.getX(), vec.getY(), vec.getZ(), 0);
                int BlockIDLayer1 = chunk.getBlockId(vec.getX(), vec.getY(), vec.getZ(), 1);
                boolean shouldFillWater = BlockIDLayer0 == BlockID.FLOWING_WATER || BlockIDLayer0 == BlockID.STILL_WATER ||
                        BlockIDLayer1 == BlockID.FLOWING_WATER || BlockIDLayer1 == BlockID.STILL_WATER;
                chunk.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 0, entry.getId(), entry.getMeta());
                if (shouldFillWater) chunk.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 1, BlockID.STILL_WATER);
            } else if (!isIgnoreAir) {
                chunk.setBlock(vec.getX() & 0xf, vec.getY(), vec.getZ() & 0xf, Block.AIR);
            }

            if (blockInfo.nbt != null) {
                CompoundTag nbt = blockInfo.nbt.clone();

                nbt.putInt("x", vec.getX());
                nbt.putInt("y", vec.getY());
                nbt.putInt("z", vec.getZ());

                if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }

                if (blockActorProcessor != null) {
                    blockActorProcessor.accept(nbt);
                }
            }
        }

        if (!settings.isIgnoreEntities()) {
            this.placeEntities(chunk.getProvider().getLevel(), position);
        }

        return true;
    }

    @Override
    public boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, StructurePlaceSettings settings) {
        if (this.isInvalid()) {
            return false;
        }

        boolean isIgnoreAir = settings.isIgnoreAir();
        int integrity = settings.getIntegrity();
        Consumer<CompoundTag> blockActorProcessor = settings.getBlockActorProcessor();
        boolean isIntact = integrity >= 100;
        Level world = level.getChunk(position.getX() >> 4, position.getZ() >> 4).getProvider().getLevel();

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            if (entry.getId() == Block.AIR && isIgnoreAir || !isIntact && integrity <= random.nextBoundedInt(100) && entry.getId() != BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            BlockVector3 vec = blockInfo.pos.add(position);

            if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                int BlockIDLayer0 = level.getBlockIdAt(vec.getX(), vec.getY(), vec.getZ(), 0);
                int BlockIDLayer1 = level.getBlockIdAt(vec.getX(), vec.getY(), vec.getZ(), 1);
                boolean shouldFillWater = BlockIDLayer0 == BlockID.FLOWING_WATER || BlockIDLayer0 == BlockID.STILL_WATER ||
                        BlockIDLayer1 == BlockID.FLOWING_WATER || BlockIDLayer1 == BlockID.STILL_WATER;
                level.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 0, entry.getId(), entry.getMeta());
                if (shouldFillWater) level.setBlockAtLayer(vec.getX(), vec.getY(), vec.getZ(), 1, BlockID.STILL_WATER);
            } else if (!isIgnoreAir) {
                level.setBlockAt(vec.getX(), vec.getY(), vec.getZ(), Block.AIR);
            }

            if (blockInfo.nbt != null) {
                CompoundTag nbt = blockInfo.nbt.clone();

                nbt.putInt("x", vec.getX());
                nbt.putInt("y", vec.getY());
                nbt.putInt("z", vec.getZ());

                if (entry.getId() != BlockID.STRUCTURE_BLOCK) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(world, nbt));
                }

                if (blockActorProcessor != null) {
                    blockActorProcessor.accept(nbt);
                }
            }
        }

        if (!settings.isIgnoreEntities()) {
            this.placeEntities(world, position);
        }

        return true;
    }

    protected void placeEntities(Level level, BlockVector3 position) {
        for (StructureEntityInfo entityInfo : this.entityInfoList) {
            CompoundTag nbt = entityInfo.nbt.clone();

            Vector3 pos = entityInfo.pos.add(position.getX(), position.getY(), position.getZ());

            ListTag<DoubleTag> posTag = new ListTag<>("Pos");
            posTag.add(new DoubleTag("", pos.x));
            posTag.add(new DoubleTag("", pos.y));
            posTag.add(new DoubleTag("", pos.z));
            nbt.putList(posTag);

            Server.getInstance().getScheduler().scheduleTask(new ActorSpawnTask(level, nbt));
        }
    }
}
