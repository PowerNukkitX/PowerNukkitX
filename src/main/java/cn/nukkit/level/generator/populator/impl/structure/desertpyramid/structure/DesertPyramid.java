package cn.nukkit.level.generator.populator.impl.structure.desertpyramid.structure;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.desertpyramid.loot.DesertPyramidChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructureBuilder;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class DesertPyramid extends ScatteredStructurePiece {

    public DesertPyramid(BlockVector3 pos) {
        super(pos.add(0, -18, 0), new BlockVector3(21, 29, 21));
    }

    @Override
    public void generate(ChunkManager level, NukkitRandom random) {
        ScatteredStructureBuilder builder = new ScatteredStructureBuilder(level, this);
        for (int x = 0; x < 21; x++) {
            for (int z = 0; z < 21; z++) {
                builder.setBlockDownward(new BlockVector3(x, 13, z), Block.SANDSTONE);
            }
        }
        builder.fill(new BlockVector3(0, 14, 0), new BlockVector3(20, 18, 20), Block.SANDSTONE);
        for (int i = 1; i <= 9; i++) {
            builder.fill(new BlockVector3(i, i + 18, i), new BlockVector3(20 - i, i + 18, 20 - i), Block.SANDSTONE);
            builder.fill(new BlockVector3(i + 1, i + 18, i + 1), new BlockVector3(19 - i, i + 18, 19 - i), Block.AIR);
        }
        // east tower
        builder.fill(new BlockVector3(0, 18, 0), new BlockVector3(4, 27, 4), Block.SANDSTONE, 0, Block.AIR, 0);
        builder.fill(new BlockVector3(1, 28, 1), new BlockVector3(3, 28, 3), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(2, 28, 0), Block.SANDSTONE_STAIRS, 2); // N
        builder.setBlock(new BlockVector3(4, 28, 2), Block.SANDSTONE_STAIRS, 1); // E
        builder.setBlock(new BlockVector3(2, 28, 4), Block.SANDSTONE_STAIRS, 3); // S
        builder.setBlock(new BlockVector3(0, 28, 2), Block.SANDSTONE_STAIRS, 0); // W
        builder.fill(new BlockVector3(1, 19, 5), new BlockVector3(3, 22, 11), Block.SANDSTONE);
        builder.fill(new BlockVector3(2, 22, 4), new BlockVector3(2, 24, 4), Block.AIR);
        builder.fill(new BlockVector3(1, 19, 3), new BlockVector3(2, 20, 3), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(1, 19, 2), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(1, 20, 2), Block.STONE_SLAB, 1);
        builder.setBlock(new BlockVector3(2, 19, 2), Block.SANDSTONE_STAIRS, 1); // E
        for (int i = 0; i < 2; i++) {
            builder.setBlock(new BlockVector3(2, 21 + i, 4 + i), Block.SANDSTONE_STAIRS, 2); // N
        }
        // west tower
        builder.fill(new BlockVector3(16, 18, 0), new BlockVector3(20, 27, 4), Block.SANDSTONE, 0, Block.AIR, 0);
        builder.fill(new BlockVector3(17, 28, 1), new BlockVector3(19, 28, 3), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(18, 28, 0), Block.SANDSTONE_STAIRS, 2); // N
        builder.setBlock(new BlockVector3(20, 28, 2), Block.SANDSTONE_STAIRS, 1); // E
        builder.setBlock(new BlockVector3(18, 28, 4), Block.SANDSTONE_STAIRS, 3); // S
        builder.setBlock(new BlockVector3(16, 28, 2), Block.SANDSTONE_STAIRS, 0); // W
        builder.fill(new BlockVector3(17, 19, 5), new BlockVector3(19, 22, 11), Block.SANDSTONE);
        builder.fill(new BlockVector3(18, 22, 4), new BlockVector3(18, 24, 4), Block.AIR);
        builder.fill(new BlockVector3(18, 19, 3), new BlockVector3(19, 20, 3), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(19, 19, 2), Block.SANDSTONE);
        builder.setBlock(new BlockVector3(19, 20, 2), Block.STONE_SLAB, 1);
        builder.setBlock(new BlockVector3(18, 19, 2), Block.SANDSTONE_STAIRS, 0); // W
        for (int i = 0; i < 2; i++) {
            builder.setBlock(new BlockVector3(18, 21 + i, 4 + i), Block.SANDSTONE_STAIRS, 2); // N
        }
        // tower symbols
        for (int i = 0; i < 2; i++) {
            // front
            builder.fill(new BlockVector3(1 + (i << 4), 20, 0), new BlockVector3(1 + (i << 4), 21, 0), Block.SANDSTONE, 2);
            builder.fill(new BlockVector3(2 + (i << 4), 20, 0), new BlockVector3(2 + (i << 4), 21, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(3 + (i << 4), 20, 0), new BlockVector3(3 + (i << 4), 21, 0), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(1 + (i << 4), 22, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(2 + (i << 4), 22, 0), Block.SANDSTONE, 1);
            builder.setBlock(new BlockVector3(3 + (i << 4), 22, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(1 + (i << 4), 23, 0), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(2 + (i << 4), 23, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(3 + (i << 4), 23, 0), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(1 + (i << 4), 24, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(2 + (i << 4), 24, 0), Block.SANDSTONE, 1);
            builder.setBlock(new BlockVector3(3 + (i << 4), 24, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(1 + (i << 4), 25, 0), new BlockVector3(3 + (i << 4), 25, 0), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(1 + (i << 4), 26, 0), new BlockVector3(3 + (i << 4), 26, 0), Block.SANDSTONE, 2);
            // side
            builder.fill(new BlockVector3(i * 20, 20, 1), new BlockVector3(i * 20, 21, 1), Block.SANDSTONE, 2);
            builder.fill(new BlockVector3(i * 20, 20, 2), new BlockVector3(i * 20, 21, 2), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(i * 20, 20, 3), new BlockVector3(i * 20, 21, 3), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(i * 20, 22, 1), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(i * 20, 22, 2), Block.SANDSTONE, 1);
            builder.setBlock(new BlockVector3(i * 20, 22, 3), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(i * 20, 23, 1), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(i * 20, 23, 2), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(i * 20, 23, 3), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(i * 20, 24, 1), Block.STAINED_HARDENED_CLAY, 1);
            builder.setBlock(new BlockVector3(i * 20, 24, 2), Block.SANDSTONE, 1);
            builder.setBlock(new BlockVector3(i * 20, 24, 3), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(i * 20, 25, 1), new BlockVector3(i * 20, 25, 3), Block.STAINED_HARDENED_CLAY, 1);
            builder.fill(new BlockVector3(i * 20, 26, 1), new BlockVector3(i * 20, 26, 3), Block.SANDSTONE, 2);
        }
        // front entrance
        builder.fill(new BlockVector3(8, 18, 1), new BlockVector3(12, 22, 4), Block.SANDSTONE, 0, Block.AIR, 0);
        builder.fill(new BlockVector3(9, 19, 0), new BlockVector3(11, 21, 4), Block.AIR);
        builder.fill(new BlockVector3(9, 19, 1), new BlockVector3(9, 20, 1), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(11, 19, 1), new BlockVector3(11, 20, 1), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(9, 21, 1), new BlockVector3(11, 21, 1), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(8, 18, 0), new BlockVector3(8, 21, 0), Block.SANDSTONE);
        builder.fill(new BlockVector3(12, 18, 0), new BlockVector3(12, 21, 0), Block.SANDSTONE);
        builder.fill(new BlockVector3(8, 22, 0), new BlockVector3(12, 22, 0), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(8, 23, 0), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(9, 23, 0), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(10, 23, 0), Block.SANDSTONE, 1);
        builder.setBlock(new BlockVector3(11, 23, 0), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(12, 23, 0), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(9, 24, 0), new BlockVector3(11, 24, 0), Block.SANDSTONE, 2);
        // east entrance
        builder.fill(new BlockVector3(5, 23, 9), new BlockVector3(5, 25, 11), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(6, 25, 9), new BlockVector3(6, 25, 11), Block.SANDSTONE);
        builder.fill(new BlockVector3(5, 23, 10), new BlockVector3(6, 24, 10), Block.AIR);
        // west entrance
        builder.fill(new BlockVector3(15, 23, 9), new BlockVector3(15, 25, 11), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(14, 25, 9), new BlockVector3(14, 25, 11), Block.SANDSTONE);
        builder.fill(new BlockVector3(14, 23, 10), new BlockVector3(15, 24, 10), Block.AIR);
        // corridor to east tower
        builder.fill(new BlockVector3(4, 19, 1), new BlockVector3(8, 21, 3), Block.SANDSTONE, 0, Block.AIR, 0);
        builder.fill(new BlockVector3(4, 19, 2), new BlockVector3(8, 20, 2), Block.AIR);
        // corridor to west tower
        builder.fill(new BlockVector3(12, 19, 1), new BlockVector3(16, 21, 3), Block.SANDSTONE, 0, Block.AIR, 0);
        builder.fill(new BlockVector3(12, 19, 2), new BlockVector3(16, 20, 2), Block.AIR);
        // pillars in the middle of 1st floor
        builder.fill(new BlockVector3(8, 19, 8), new BlockVector3(8, 21, 8), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(12, 19, 8), new BlockVector3(12, 21, 8), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(12, 19, 12), new BlockVector3(12, 21, 12), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(8, 19, 12), new BlockVector3(8, 21, 12), Block.SANDSTONE, 2);
        // 2nd floor
        builder.fill(new BlockVector3(5, 22, 5), new BlockVector3(15, 22, 15), Block.SANDSTONE);
        builder.fill(new BlockVector3(9, 22, 9), new BlockVector3(11, 22, 11), Block.AIR);
        // east and west corridors
        builder.fill(new BlockVector3(3, 19, 5), new BlockVector3(3, 20, 11), Block.AIR);
        builder.fill(new BlockVector3(4, 21, 5), new BlockVector3(4, 21, 16), Block.SANDSTONE);
        builder.fill(new BlockVector3(17, 19, 5), new BlockVector3(17, 20, 11), Block.AIR);
        builder.fill(new BlockVector3(16, 21, 5), new BlockVector3(16, 21, 16), Block.SANDSTONE);
        builder.fill(new BlockVector3(2, 19, 12), new BlockVector3(2, 19, 18), Block.SANDSTONE);
        builder.fill(new BlockVector3(18, 19, 12), new BlockVector3(18, 19, 18), Block.SANDSTONE);
        builder.fill(new BlockVector3(3, 19, 18), new BlockVector3(18, 19, 18), Block.SANDSTONE);
        for (int i = 0; i < 7; i++) {
            builder.setBlock(new BlockVector3(4, 19, 5 + (i << 1)), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(4, 20, 5 + (i << 1)), Block.SANDSTONE, 1);
            builder.setBlock(new BlockVector3(16, 19, 5 + (i << 1)), Block.SANDSTONE, 2);
            builder.setBlock(new BlockVector3(16, 20, 5 + (i << 1)), Block.SANDSTONE, 1);
        }
        // floor symbols
        builder.setBlock(new BlockVector3(9, 18, 9), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(11, 18, 9), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(11, 18, 11), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(9, 18, 11), Block.STAINED_HARDENED_CLAY, 1);
        builder.setBlock(new BlockVector3(10, 18, 10), Block.STAINED_HARDENED_CLAY, 11);
        builder.fill(new BlockVector3(10, 18, 7), new BlockVector3(10, 18, 8), Block.STAINED_HARDENED_CLAY, 1);
        builder.fill(new BlockVector3(12, 18, 10), new BlockVector3(13, 18, 10), Block.STAINED_HARDENED_CLAY, 1);
        builder.fill(new BlockVector3(10, 18, 12), new BlockVector3(10, 18, 13), Block.STAINED_HARDENED_CLAY, 1);
        builder.fill(new BlockVector3(7, 18, 10), new BlockVector3(8, 18, 10), Block.STAINED_HARDENED_CLAY, 1);
        // trap chamber
        builder.fill(new BlockVector3(8, 0, 8), new BlockVector3(12, 3, 12), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(8, 4, 8), new BlockVector3(12, 4, 12), Block.SANDSTONE, 1);
        builder.fill(new BlockVector3(8, 5, 8), new BlockVector3(12, 5, 12), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(8, 6, 8), new BlockVector3(12, 13, 12), Block.SANDSTONE);
        builder.fill(new BlockVector3(9, 3, 9), new BlockVector3(11, 17, 11), Block.AIR);
        builder.fill(new BlockVector3(9, 1, 9), new BlockVector3(11, 1, 11), Block.TNT);
        builder.fill(new BlockVector3(9, 2, 9), new BlockVector3(11, 2, 11), Block.SANDSTONE, 2);
        builder.fill(new BlockVector3(10, 3, 8), new BlockVector3(10, 4, 8), Block.AIR);
        builder.setBlock(new BlockVector3(10, 3, 7), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(10, 4, 7), Block.SANDSTONE, 1);
        builder.fill(new BlockVector3(12, 3, 10), new BlockVector3(12, 4, 10), Block.AIR);
        builder.setBlock(new BlockVector3(13, 3, 10), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(13, 4, 10), Block.SANDSTONE, 1);
        builder.fill(new BlockVector3(10, 3, 12), new BlockVector3(10, 4, 12), Block.AIR);
        builder.setBlock(new BlockVector3(10, 3, 13), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(10, 4, 13), Block.SANDSTONE, 1);
        builder.fill(new BlockVector3(8, 3, 10), new BlockVector3(8, 4, 10), Block.AIR);
        builder.setBlock(new BlockVector3(7, 3, 10), Block.SANDSTONE, 2);
        builder.setBlock(new BlockVector3(7, 4, 10), Block.SANDSTONE, 1);
        builder.setBlock(new BlockVector3(10, 3, 10), Block.STONE_PRESSURE_PLATE);

        builder.setBlock(new BlockVector3(10, 3, 12), Block.CHEST, 2); // S
        ListTag<CompoundTag> chestS = new ListTag<>("Items");
        DesertPyramidChest.get().create(chestS, random);
        builder.setTile(new BlockVector3(10, 3, 12), BlockEntity.CHEST, new CompoundTag().putList(chestS));
        builder.setBlock(new BlockVector3(8, 3, 10), Block.CHEST, 5); // W
        ListTag<CompoundTag> chestW = new ListTag<>("Items");
        DesertPyramidChest.get().create(chestW, random);
        builder.setTile(new BlockVector3(8, 3, 10), BlockEntity.CHEST, new CompoundTag().putList(chestW));
        builder.setBlock(new BlockVector3(10, 3, 8), Block.CHEST, 3); // N
        ListTag<CompoundTag> chestN = new ListTag<>("Items");
        DesertPyramidChest.get().create(chestN, random);
        builder.setTile(new BlockVector3(10, 3, 8), BlockEntity.CHEST, new CompoundTag().putList(chestN));
        builder.setBlock(new BlockVector3(12, 3, 10), Block.CHEST, 4); // E
        ListTag<CompoundTag> chestE = new ListTag<>("Items");
        DesertPyramidChest.get().create(chestE, random);
        builder.setTile(new BlockVector3(12, 3, 10), BlockEntity.CHEST, new CompoundTag().putList(chestE));
    }
}
