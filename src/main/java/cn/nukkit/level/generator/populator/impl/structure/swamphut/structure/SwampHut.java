package cn.nukkit.level.generator.populator.impl.structure.swamphut.structure;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructureBuilder;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class SwampHut extends ScatteredStructurePiece {

    public SwampHut(BlockVector3 pos) {
        super(pos, new BlockVector3(7, 5, 9));
    }

    @Override
    public void generate(ChunkManager level, NukkitRandom random) {
        this.adjustHorizPos(level);

        ScatteredStructureBuilder builder = new ScatteredStructureBuilder(level, this);
        builder.fill(new BlockVector3(1, 1, 2), new BlockVector3(5, 4, 7), Block.WOODEN_PLANK, 1, Block.AIR, 0); // hut body
        builder.fill(new BlockVector3(1, 1, 1), new BlockVector3(5, 1, 1), Block.WOODEN_PLANK, 1); // hut steps
        builder.fill(new BlockVector3(2, 1, 0), new BlockVector3(4, 1, 0), Block.WOODEN_PLANK, 1); // hut steps
        builder.fill(new BlockVector3(4, 2, 2), new BlockVector3(4, 3, 2), Block.AIR); // hut door
        builder.fill(new BlockVector3(5, 3, 4), new BlockVector3(5, 3, 5), Block.AIR); // left window
        builder.setBlock(new BlockVector3(1, 3, 4), Block.AIR);

        builder.setBlock(new BlockVector3(1, 3, 5), Block.FLOWER_POT_BLOCK);
        builder.setTile(new BlockVector3(1, 3, 5), BlockEntity.FLOWER_POT, new CompoundTag()
                .putShort("item", Block.RED_MUSHROOM));

        builder.setBlock(new BlockVector3(2, 3, 2), Block.FENCE);
        builder.setBlock(new BlockVector3(3, 3, 7), Block.FENCE);

        builder.fill(new BlockVector3(0, 4, 1), new BlockVector3(6, 4, 1), Block.SPRUCE_WOOD_STAIRS, 2); // N
        builder.fill(new BlockVector3(6, 4, 2), new BlockVector3(6, 4, 7), Block.SPRUCE_WOOD_STAIRS, 1); // E
        builder.fill(new BlockVector3(0, 4, 8), new BlockVector3(6, 4, 8), Block.SPRUCE_WOOD_STAIRS, 3); // S
        builder.fill(new BlockVector3(0, 4, 2), new BlockVector3(0, 4, 7), Block.SPRUCE_WOOD_STAIRS, 0); // W

        builder.fill(new BlockVector3(1, 0, 2), new BlockVector3(1, 3, 2), Block.LOG);
        builder.fill(new BlockVector3(5, 0, 2), new BlockVector3(5, 3, 2), Block.LOG);
        builder.fill(new BlockVector3(1, 0, 7), new BlockVector3(1, 3, 7), Block.LOG);
        builder.fill(new BlockVector3(5, 0, 7), new BlockVector3(5, 3, 7), Block.LOG);

        builder.setBlock(new BlockVector3(1, 2, 1), Block.FENCE);
        builder.setBlock(new BlockVector3(5, 2, 1), Block.FENCE);

        builder.setBlock(new BlockVector3(4, 2, 6), Block.CAULDRON_BLOCK);
        builder.setTile(new BlockVector3(4, 2, 6), BlockEntity.CAULDRON);

        builder.setBlock(new BlockVector3(3, 2, 6), Block.WORKBENCH);

        builder.setBlockDownward(new BlockVector3(1, -1, 2), Block.LOG);
        builder.setBlockDownward(new BlockVector3(5, -1, 2), Block.LOG);
        builder.setBlockDownward(new BlockVector3(1, -1, 7), Block.LOG);
        builder.setBlockDownward(new BlockVector3(5, -1, 7), Block.LOG);

        //TODO: builder.spawnMob(new BlockVector3(2, 2, 5), EntityWitch.NETWORK_ID);
        //TODO: builder.spawnMob(new BlockVector3(2, 2, 5), EntityCat.NETWORK_ID);
    }
}
