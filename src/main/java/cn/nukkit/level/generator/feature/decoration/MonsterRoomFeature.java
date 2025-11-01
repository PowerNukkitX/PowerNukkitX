package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectMonsterRoom;
import cn.nukkit.math.Vector3;

public class MonsterRoomFeature extends GenerateFeature {


    public static final String NAME = "minecraft:monster_room";

    protected final static ObjectMonsterRoom MONSTER_ROOM = new ObjectMonsterRoom();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) + name().hashCode());
        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;

        chance:
        for (int chance = 0; chance < 8; ++chance) {
            int x = sourceX + random.nextBoundedInt(16) + 8;
            int y = random.nextBoundedInt(384) - 64;
            int z = sourceZ + random.nextBoundedInt(16) + 8;

            int xv = random.nextBoundedInt(2) + 2;
            int x1 = -xv - 1;
            int x2 = xv + 1;

            int zv = random.nextBoundedInt(2) + 2;
            int z1 = -zv - 1;
            int z2 = zv + 1;

            int t = 0;

            for (int dx = x1; dx <= x2; ++dx) {
                for (int dy = -1; dy <= 4; ++dy) {
                    for (int dz = z1; dz <= z2; ++dz) {
                        int tx = x + dx;
                        int ty = y + dy;
                        int tz = z + dz;

                        Block id = level.getBlock(tx, ty, tz);
                        boolean isSolid = id.isSolid();

                        if (dy == -1 && !isSolid) {
                            continue chance;
                        }
                        if (dy == 4 && !isSolid) {
                            continue chance;
                        }
                        if ((dx == x1 || dx == x2 || dz == z1 || dz == z2) && dy == 0 && level.getBlock(tx, ty + 1, tz).isAir()) {
                            ++t;
                        }
                    }
                }
            }
            if (t >= 1 && t <= 5) {
                BlockManager manager = new BlockManager(level);
                MONSTER_ROOM.generate(manager, random, new Vector3(x, y, z));
                queueObject(chunk, manager);
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
