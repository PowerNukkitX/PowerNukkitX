package cn.nukkit.level.generator.populator.impl.structure.dungeon;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.entity.mob.EntitySpider;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.impl.structure.dungeon.loot.DungeonChest;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorDungeon extends PopulatorStructure {

    private static final int[] MOBS = {EntitySkeleton.NETWORK_ID, EntityZombie.NETWORK_ID, EntityZombie.NETWORK_ID, EntitySpider.NETWORK_ID};

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (!chunk.isOverWorld()) return;
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

                        int id = level.getBlockIdAt(tx, ty, tz);
                        boolean isSolid = Block.fullList[(id << 4) + level.getBlockDataAt(tx, ty, tz)].isSolid();

                        if (dy == -1 && !isSolid) {
                            continue chance;
                        }
                        if (dy == 4 && !isSolid) {
                            continue chance;
                        }
                        if ((dx == x1 || dx == x2 || dz == z1 || dz == z2) && dy == 0 && level.getBlockIdAt(tx, ty + 1, tz) == AIR) {
                            ++t;
                        }
                    }
                }
            }

            if (t >= 1 && t <= 5) {
                for (int dx = x1; dx <= x2; ++dx) {
                    for (int dy = 3; dy >= -1; --dy) {
                        for (int dz = z1; dz <= z2; ++dz) {
                            int tx = x + dx;
                            int ty = y + dy;
                            int tz = z + dz;

                            int id = level.getBlockIdAt(tx, ty, tz);

                            if (dx != x1 && dy != -1 && dz != z1 && dx != x2 && dy != 4 && dz != z2) {
                                if (id != CHEST) {
                                    level.setBlockAt(tx, ty, tz, AIR);
                                }
                            } else if (ty >= 0 && !Block.fullList[(level.getBlockIdAt(tx, ty - 1, tz) << 4) + level.getBlockDataAt(tx, ty - 1, tz)].isSolid()) {
                                level.setBlockAt(tx, ty, tz, AIR);
                            } else if (Block.fullList[(id << 4) + level.getBlockDataAt(tx, ty, tz)].isSolid() && id != CHEST) {
                                if (dy == -1 && random.nextBoundedInt(4) != 0) {
                                    level.setBlockAt(tx, ty, tz, MOSSY_STONE);
                                } else {
                                    level.setBlockAt(tx, ty, tz, COBBLESTONE);
                                }
                            }
                        }
                    }
                }

                for (int xx = 0; xx < 2; ++xx) {
                    for (int zz = 0; zz < 3; ++zz) {
                        int tx = x + random.nextBoundedInt(xv * 2 + 1) - xv;
                        int tz = z + random.nextBoundedInt(zv * 2 + 1) - zv;

                        if (level.getBlockIdAt(tx, y, tz) == AIR) {
                            int n = 0;

                            if (Block.fullList[(level.getBlockIdAt(tx - 1, y, tz) << 4) + level.getBlockDataAt(tx - 1, y, tz)].isSolid()) {
                                ++n;
                            }
                            if (Block.fullList[(level.getBlockIdAt(tx + 1, y, tz) << 4) + level.getBlockDataAt(tx + 1, y, tz)].isSolid()) {
                                ++n;
                            }
                            if (Block.fullList[(level.getBlockIdAt(tx, y, tz - 1) << 4) + level.getBlockDataAt(tx, y, tz - 1)].isSolid()) {
                                ++n;
                            }
                            if (Block.fullList[(level.getBlockIdAt(tx, y, tz + 1) << 4) + level.getBlockDataAt(tx, y, tz + 1)].isSolid()) {
                                ++n;
                            }

                            if (n == 1) {
                                level.setBlockAt(tx, y, tz, CHEST, 2);
                                Vector3 vec = new Vector3(tx, y, tz);

                                CompoundTag chest = BlockEntity.getDefaultCompound(vec, BlockEntity.CHEST);
                                ListTag<CompoundTag> items = new ListTag<>("Items");
                                DungeonChest.get().create(items, random);
                                chest.putList(items);
                                Server.getInstance().getScheduler().scheduleTask(null, new BlockActorSpawnTask(chunk.getProvider().getLevel(), chest));
                                break;
                            }
                        }
                    }
                }

                level.setBlockAt(x, y, z, MONSTER_SPAWNER);
                Server.getInstance().getScheduler().scheduleTask(null, new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                        BlockEntity.getDefaultCompound(new Vector3(x, y, z), BlockEntity.MOB_SPAWNER)
                                .putInt("EntityId", MOBS[random.nextBoundedInt(MOBS.length)])));
            }
        }
    }

    @Since("1.19.21-r2")
    @Override
    public boolean isAsync() {
        return true;
    }
}
