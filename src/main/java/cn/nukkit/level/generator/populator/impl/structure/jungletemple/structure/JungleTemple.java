package cn.nukkit.level.generator.populator.impl.structure.jungletemple.structure;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.jungletemple.loot.JungleTempleChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructureBuilder;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.ScatteredStructurePiece;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class JungleTemple extends ScatteredStructurePiece {

    public JungleTemple(BlockVector3 pos) {
        super(pos, new BlockVector3(12, 14, 15));
    }

    @Override
    public void generate(ChunkManager level, NukkitRandom random) {
        this.adjustHorizPos(level);

        this.boundingBox.offset(new BlockVector3(0, -4, 0));

        ScatteredStructureBuilder builder = new ScatteredStructureBuilder(level, this);
        Map<Integer, Integer> stones = new Int2IntOpenHashMap();
        builder.addRandomBlock(stones, 4, Block.COBBLESTONE);
        builder.addRandomBlock(stones, 6, Block.MOSSY_STONE);

        // 1st floor
        builder.fillWithRandomBlock(new BlockVector3(0, 0, 0), new BlockVector3(11, 0, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 0), new BlockVector3(11, 3, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(11, 1, 1), new BlockVector3(11, 3, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 1), new BlockVector3(0, 3, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 14), new BlockVector3(11, 3, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 4, 0), new BlockVector3(11, 4, 14), random, stones);
        builder.fill(new BlockVector3(4, 4, 0), new BlockVector3(7, 4, 0), Block.COBBLESTONE_STAIRS, 2); // N
        builder.fill(new BlockVector3(1, 1, 1), new BlockVector3(10, 3, 13), Block.AIR);
        builder.fill(new BlockVector3(5, 4, 7), new BlockVector3(6, 4, 9), Block.AIR);

        // 2nd floor
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 2), new BlockVector3(9, 6, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 5, 3), new BlockVector3(9, 6, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 12), new BlockVector3(9, 6, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 3), new BlockVector3(2, 6, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 7, 1), new BlockVector3(10, 7, 13), random, stones);
        builder.fill(new BlockVector3(3, 5, 3), new BlockVector3(8, 6, 11), Block.AIR);
        builder.fill(new BlockVector3(4, 7, 6), new BlockVector3(7, 7, 9), Block.AIR);
        builder.fill(new BlockVector3(5, 5, 2), new BlockVector3(6, 6, 2), Block.AIR);
        builder.fill(new BlockVector3(5, 6, 12), new BlockVector3(6, 6, 12), Block.AIR);

        // 3rd floor
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 1), new BlockVector3(10, 9, 1), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(10, 8, 2), new BlockVector3(10, 9, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 13), new BlockVector3(10, 9, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 2), new BlockVector3(1, 9, 12), random, stones);
        builder.fill(new BlockVector3(2, 8, 2), new BlockVector3(9, 9, 12), Block.AIR);
        builder.fill(new BlockVector3(5, 9, 1), new BlockVector3(6, 9, 1), Block.AIR);
        builder.fill(new BlockVector3(5, 9, 13), new BlockVector3(6, 9, 13), Block.AIR);
        builder.setBlock(new BlockVector3(10, 9, 5), Block.AIR);
        builder.setBlock(new BlockVector3(10, 9, 9), Block.AIR);
        builder.setBlock(new BlockVector3(1, 9, 5), Block.AIR);
        builder.setBlock(new BlockVector3(1, 9, 9), Block.AIR);

        // roof
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 1), new BlockVector3(10, 10, 4), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 10, 5), new BlockVector3(10, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 5), new BlockVector3(3, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 10), new BlockVector3(10, 10, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 3), new BlockVector3(8, 11, 5), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 11, 6), new BlockVector3(8, 11, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 6), new BlockVector3(4, 11, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 9), new BlockVector3(8, 11, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 12, 4), new BlockVector3(7, 12, 10), random, stones);
        builder.fill(new BlockVector3(4, 10, 5), new BlockVector3(7, 10, 9), Block.AIR);
        builder.fill(new BlockVector3(5, 11, 6), new BlockVector3(6, 11, 8), Block.AIR);

        // outside walls decorations
        builder.fillWithRandomBlock(new BlockVector3(2, 8, 0), new BlockVector3(2, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 8, 0), new BlockVector3(4, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 8, 0), new BlockVector3(7, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 8, 0), new BlockVector3(9, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 10, 0), new BlockVector3(6, 10, 0), random, stones);
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomBlock(new BlockVector3(11, 8, 2 + (i << 1)), new BlockVector3(11, 9, 2 + (i << 1)), random, stones);
            builder.fillWithRandomBlock(new BlockVector3(0, 8, 2 + (i << 1)), new BlockVector3(0, 9, 2 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomBlock(new BlockVector3(11, 10, 5), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(11, 10, 9), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(0, 10, 5), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(0, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 8, 14), new BlockVector3(2, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 8, 14), new BlockVector3(4, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 8, 14), new BlockVector3(7, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 8, 14), new BlockVector3(9, 9, 14), random, stones);

        // roof decorations
        builder.fillWithRandomBlock(new BlockVector3(2, 11, 2), new BlockVector3(2, 13, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 11, 2), new BlockVector3(9, 13, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 11, 12), new BlockVector3(9, 13, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 11, 12), new BlockVector3(2, 13, 12), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 13, 4), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 13, 4), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 13, 10), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 13, 10), random, stones);
        builder.fill(new BlockVector3(5, 13, 6), new BlockVector3(6, 13, 6), Block.COBBLESTONE_STAIRS, 2); // N
        builder.fillWithRandomBlock(new BlockVector3(5, 13, 7), new BlockVector3(6, 13, 7), random, stones);
        builder.fill(new BlockVector3(5, 13, 8), new BlockVector3(6, 13, 8), Block.COBBLESTONE_STAIRS, 3); // S

        // 1st floor inside
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomBlock(new BlockVector3(1, 3, 2 + (i << 1)), new BlockVector3(3, 3, 2 + (i << 1)), random, stones);
        }
        for (int i = 0; i < 7; i++) {
            builder.fillWithRandomBlock(new BlockVector3(1, 1, 1 + (i << 1)), new BlockVector3(1, 2, 1 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomBlock(new BlockVector3(2, 2, 1), random, stones);
        builder.setBlock(new BlockVector3(3, 1, 1), Block.MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(4, 2, 1), new BlockVector3(5, 2, 1), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 1, 1), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 3, 1), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 2, 1), new BlockVector3(9, 2, 1), random, stones);
        builder.setBlock(new BlockVector3(8, 1, 1), Block.MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(10, 1, 1), new BlockVector3(10, 3, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 3, 1), new BlockVector3(9, 3, 7), random, stones);
        builder.setBlock(new BlockVector3(9, 1, 2), Block.MOSSY_STONE);
        builder.setBlock(new BlockVector3(9, 1, 4), Block.MOSSY_STONE);
        builder.setBlock(new BlockVector3(8, 1, 5), Block.MOSSY_STONE);
        builder.fill(new BlockVector3(7, 2, 5), new BlockVector3(7, 3, 5), Block.MOSSY_STONE);
        builder.setBlock(new BlockVector3(6, 1, 5), Block.MOSSY_STONE);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 2, 5), random, stones);
        builder.fill(new BlockVector3(5, 2, 5), new BlockVector3(5, 3, 5), Block.MOSSY_STONE);
        builder.setBlock(new BlockVector3(4, 1, 5), Block.MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(7, 1, 6), new BlockVector3(7, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 1, 6), new BlockVector3(4, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 3, 11), new BlockVector3(6, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 3, 11), new BlockVector3(10, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 11), new BlockVector3(10, 1, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 1, 8), new BlockVector3(6, 1, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(6, 1, 7), new BlockVector3(6, 2, 7), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(5, 2, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(6, 1, 6), new BlockVector3(6, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 2, 6), new BlockVector3(5, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 2, 6), new BlockVector3(9, 2, 6), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(8, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 1, 7), new BlockVector3(9, 2, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 7), new BlockVector3(8, 3, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(10, 1, 8), new BlockVector3(10, 1, 10), random, stones);
        builder.setBlock(new BlockVector3(10, 2, 9), Block.MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 8), new BlockVector3(8, 1, 10), random, stones);
        builder.fill(new BlockVector3(8, 2, 11), new BlockVector3(10, 2, 11), Block.STONE_BRICK, 3);
        builder.fill(new BlockVector3(8, 2, 12), new BlockVector3(10, 2, 12), Block.LEVER, 3); // N
        builder.setBlock(new BlockVector3(3, 2, 1), Block.DISPENSER, 3); // N
        //TODO: tile
        builder.setBlock(new BlockVector3(9, 2, 3), Block.DISPENSER, 4); // E
        //TODO: tile
        builder.setBlock(new BlockVector3(3, 2, 2), Block.VINE, 4); // NESW
        builder.fill(new BlockVector3(8, 2, 3), new BlockVector3(8, 3, 3), Block.VINE, 8); // NESW
        builder.fill(new BlockVector3(2, 1, 8), new BlockVector3(3, 1, 8), Block.TRIP_WIRE);
        builder.setBlock(new BlockVector3(4, 1, 8), Block.TRIPWIRE_HOOK, 5); // E
        builder.setBlock(new BlockVector3(1, 1, 8), Block.TRIPWIRE_HOOK, 7); // W
        builder.fill(new BlockVector3(5, 1, 1), new BlockVector3(5, 1, 7), Block.REDSTONE_WIRE);
        builder.setBlock(new BlockVector3(4, 1, 1), Block.REDSTONE_WIRE);
        builder.fill(new BlockVector3(7, 1, 2), new BlockVector3(7, 1, 4), Block.TRIP_WIRE);
        builder.setBlock(new BlockVector3(7, 1, 1), Block.TRIPWIRE_HOOK, 4); // N
        builder.setBlock(new BlockVector3(7, 1, 5), Block.TRIPWIRE_HOOK, 6); // S
        builder.fill(new BlockVector3(8, 1, 6), new BlockVector3(9, 1, 6), Block.REDSTONE_WIRE);
        builder.setBlock(new BlockVector3(9, 1, 5), Block.REDSTONE_WIRE);
        builder.setBlock(new BlockVector3(9, 2, 4), Block.REDSTONE_WIRE);
        builder.fill(new BlockVector3(10, 2, 8), new BlockVector3(10, 3, 8), Block.STICKY_PISTON, 5); // E
        //TODO: tile
        builder.setBlock(new BlockVector3(9, 2, 8), Block.STICKY_PISTON, 1); // U
        //TODO: tile
        builder.setBlock(new BlockVector3(10, 3, 9), Block.REDSTONE_WIRE);
        builder.fill(new BlockVector3(8, 2, 9), new BlockVector3(8, 2, 10), Block.REDSTONE_WIRE);
        builder.setBlock(new BlockVector3(10, 2, 10), Block.UNPOWERED_REPEATER, 0 + 4); // N
        builder.setBlock(new BlockVector3(8, 1, 3), Block.CHEST, 4); // E
        ListTag<CompoundTag> chestE = new ListTag<>("Items");
        JungleTempleChest.get().create(chestE, random);
        builder.setTile(new BlockVector3(8, 1, 3), BlockEntity.CHEST, new CompoundTag().putList(chestE));
        builder.setBlock(new BlockVector3(9, 1, 10), Block.CHEST, 2); // S
        ListTag<CompoundTag> chestS = new ListTag<>("Items");
        JungleTempleChest.get().create(chestS, random);
        builder.setTile(new BlockVector3(9, 1, 10), BlockEntity.CHEST, new CompoundTag().putList(chestS));

        // 2nd floor inside
        for (int i = 0; i < 4; i++) {
            builder.fill(new BlockVector3(5, 4 - i, 6 + i), new BlockVector3(6, 4 - i, 6 + i), Block.STONE_STAIRS, 3); // S
        }
        builder.fillWithRandomBlock(new BlockVector3(4, 5, 10), new BlockVector3(7, 6, 10), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 5, 9), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 5, 9), random, stones);
        for (int i = 0; i < 3; i++) {
            builder.setBlock(new BlockVector3(7, 5 + i, 8 + i), Block.STONE_STAIRS, 2); // N
            builder.setBlock(new BlockVector3(4, 5 + i, 8 + i), Block.STONE_STAIRS, 2); // N
        }

        // 3rd floor inside
        builder.fillWithRandomBlock(new BlockVector3(5, 8, 5), new BlockVector3(6, 8, 5), random, stones);
        builder.setBlock(new BlockVector3(7, 8, 5), Block.STONE_STAIRS, 1); // E
        builder.setBlock(new BlockVector3(4, 8, 5), Block.STONE_STAIRS, 0); // W
    }
}
