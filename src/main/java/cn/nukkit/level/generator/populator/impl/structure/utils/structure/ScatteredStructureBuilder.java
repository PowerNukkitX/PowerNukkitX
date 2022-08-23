package cn.nukkit.level.generator.populator.impl.structure.utils.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.task.TileSyncTask;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Map;
import java.util.Map.Entry;

@PowerNukkitXOnly
@Since("1.19.21-r6")
public class ScatteredStructureBuilder {

    /**
     * The level this structure is built in.
     */
    private final ChunkManager level;
    /**
     * The structure piece whose coordinate origin and orientation we're using.
     */
    private final ScatteredStructurePiece structure;

    public ScatteredStructureBuilder(ChunkManager level, ScatteredStructurePiece structure) {
        this.level = level;
        this.structure = structure;
    }

    public void addRandomBlock(Map<Integer, Integer> blocks, int weight, int id) {
        this.addRandomBlock(blocks, weight, id, 0);
    }

    public void addRandomBlock(Map<Integer, Integer> blocks, int weight, int id, int meta) {
        blocks.put((id << 4) | meta, weight);
    }

    /**
     * Chooses a random block from a weighted list.
     *
     * @param random the PRNG to use
     * @param blocks a map of blocks to integer weights
     * @return a random block full id
     */
    public int getRandomBlock(NukkitRandom random, Map<Integer, Integer> blocks) {
        int totalWeight = 0;
        for (int weight : blocks.values()) {
            totalWeight += weight;
        }
        int weight = random.nextBoundedInt(totalWeight);
        for (Entry<Integer, Integer> entry : blocks.entrySet()) {
            weight -= entry.getValue();
            if (weight < 0) {
                return entry.getKey();
            }
        }
        return Block.AIR;
    }

    /**
     * Sets the block entity at a given point.
     *
     * @param pos a point relative to this structure's root point
     * @param id the new block entity id
     */
    public void setTile(BlockVector3 pos, String id) {
        this.setTile(pos, id, null);
    }

    /**
     * Sets the block entity at a given point.
     *
     * @param pos a point relative to this structure's root point
     * @param id the new block entity id
     * @param data extra nbt data
     */
    public void setTile(BlockVector3 pos, String id, CompoundTag data) {
        BlockVector3 vec = this.translate(pos);
        BaseFullChunk chunk = this.level.getChunk(vec.x >> 4, vec.z >> 4);
        CompoundTag nbt = BlockEntity.getDefaultCompound(new Vector3(vec.x, vec.y, vec.z), id);
        if (data != null) {
            data.getTags().forEach((key, value) -> nbt.put(key, value));
        }
        Server.getInstance().getScheduler().scheduleTask(null, new TileSyncTask(id, chunk, nbt));
    }

    /**
     * Sets the block at a given point.
     *
     * @param pos a point relative to this structure's root point
     * @param id the new block id
     */
    public void setBlock(BlockVector3 pos, int id) {
        this.setBlock(pos, id, 0);
    }

    /**
     * Sets the block at a given point.
     *
     * @param pos a point relative to this structure's root point
     * @param id the new block id
     * @param meta the new block meta
     */
    public void setBlock(BlockVector3 pos, int id, int meta) {
        BlockVector3 vec = this.translate(pos);
        this.level.setBlockAt(vec.x, vec.y, vec.z, id, meta);
    }

    /**
     * Builds a 1x1 column out of the given block, replacing non-solid blocks starting at a given
     * location and proceeding downward until a solid block is reached.
     *
     * @param pos the highest point to possibly replace, relative to this structure's root point
     * @param id the block id to fill
     */
    public void setBlockDownward(BlockVector3 pos, int id) {
        this.setBlockDownward(pos, id, 0);
    }

    /**
     * Builds a 1x1 column out of the given block, replacing non-solid blocks starting at a given
     * location and proceeding downward until a solid block is reached.
     *
     * @param pos the highest point to possibly replace, relative to this structure's root point
     * @param id the block id to fill
     * @param meta the block meta
     */
    public void setBlockDownward(BlockVector3 pos, int id, int meta) {
        BlockVector3 vec = this.translate(pos);
        int y = vec.y;
        while (!Block.solid[this.level.getBlockIdAt(vec.x, y, vec.z)] && y > 1) {
            this.level.setBlockAt(vec.x, y, vec.z, id, meta);
            y--;
        }
    }

    public void setBlockWithRandomBlock(BlockVector3 pos, NukkitRandom random, Map<Integer, Integer> blocks) {
        int fullId = this.getRandomBlock(random, blocks);
        this.setBlock(pos, fullId >> 4, fullId & 0xf);
    }

    /**
     * Fills a box with the given block.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param id the block id
     */
    public void fill(BlockVector3 min, BlockVector3 max, int id) {
        this.fill(min, max, id, 0);
    }

    /**
     * Fills a box with the given block.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param id the block id
     * @param meta the block meta
     */
    public void fill(BlockVector3 min, BlockVector3 max, int id, int meta) {
        this.fill(min, max, id, meta, id, meta);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerId the block id for the faces
     * @param outerMeta the block meta for the faces
     * @param innerId the block id for the interior
     * @param innerMeta the block meta for the interior
     */
    public void fill(BlockVector3 min, BlockVector3 max, int outerId, int outerMeta, int innerId, int innerMeta) {
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    int id;
                    int meta;
                    if (x != min.x && x != max.x && z != min.z && z != max.z && y != min.y && y != max.y) {
                        id = innerId;
                        meta = innerMeta;
                    } else {
                        id = outerId;
                        meta = outerMeta;
                    }
                    this.setBlock(new BlockVector3(x, y, z), id, meta);
                }
            }
        }
    }

    /**
     * Sets a box of blocks to have random types, chosen independently.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param random the PRNG to use
     * @param blocks a map of possible blocks to integer weights
     */
    public void fillWithRandomBlock(BlockVector3 min, BlockVector3 max, NukkitRandom random, Map<Integer, Integer> blocks) {
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    int fullId = this.getRandomBlock(random, blocks);
                    this.setBlock(new BlockVector3(x, y, z), fullId >> 4, fullId & 0xf);
                }
            }
        }
    }

    private BlockVector3 translate(BlockVector3 pos) {
        return this.structure.getBoundingBox().getMin().add(pos);
    }
}
