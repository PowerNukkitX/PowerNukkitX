package cn.nukkit.level.generator.populator.impl.structure.utils.structure;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.StructureBoundingBox;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public abstract class ScatteredStructurePiece {

    private static final IntList PLANTS = new IntArrayList(){
        {
            add(Block.LOG);
            add(Block.LEAVES);
            add(Block.TALL_GRASS);
            add(Block.DEAD_BUSH);
            add(Block.DANDELION);
            add(Block.RED_FLOWER);
            add(Block.BROWN_MUSHROOM);
            add(Block.RED_MUSHROOM);
            add(Block.CACTUS);
            add(Block.SUGARCANE_BLOCK);
            add(Block.PUMPKIN);
            add(Block.BROWN_MUSHROOM_BLOCK);
            add(Block.RED_MUSHROOM_BLOCK);
            add(Block.MELON_BLOCK);
            add(Block.VINE);
            add(Block.LILY_PAD);
            add(Block.COCOA);
            add(Block.LEAVES2);
            add(Block.LOG2);
            add(Block.DOUBLE_PLANT);
        }
    };

    protected StructureBoundingBox boundingBox;
    private int horizPos = -1;

    /**
     * Creates a scattered structure piece.
     *
     * @param pos the root location
     * @param size the size as a width-height-depth vector
     */
    public ScatteredStructurePiece(BlockVector3 pos, BlockVector3 size) {
        this.boundingBox = new StructureBoundingBox(new BlockVector3(pos.x, pos.y, pos.z), new BlockVector3(pos.x + size.x - 1, pos.y + size.y - 1, pos.z + size.z - 1));
    }

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    protected void adjustHorizPos(ChunkManager level) {
        if (this.horizPos >= 0) {
            return;
        }

        int sumY = 0;
        int blockCount = 0;
        for (int x = this.boundingBox.getMin().x; x <= this.boundingBox.getMax().x; x++) {
            for (int z = this.boundingBox.getMin().z; z <= this.boundingBox.getMax().z; z++) {
                int y = level.getChunk(x >> 4, z >> 4).getHighestBlockAt(x & 0xf, z & 0xf);
                int id = level.getBlockIdAt(x, y - 1, z);
                while (PLANTS.contains(id) && y > 1) {
                    y--;
                    id = level.getBlockIdAt(x, y - 1, z);
                }
                sumY += Math.max(64, y + 1);
                blockCount++;
            }
        }

        this.horizPos = sumY / blockCount;
        this.boundingBox.offset(new BlockVector3(0, this.horizPos - this.boundingBox.getMin().y, 0));
    }

    public abstract void generate(ChunkManager world, NukkitRandom random);
}
