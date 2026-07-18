package org.powernukkitx.level.generator.object.structures;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.BlockCobblestone;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockMobSpawner;
import org.powernukkitx.block.BlockMossyCobblestone;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.blockentity.BlockEntityMobSpawner;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.RandomizableContainer;
import org.powernukkitx.level.generator.object.RuledObjectGenerator;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.Arrays;
import java.util.Optional;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class ObjectMonsterRoom extends ObjectGenerator implements RuledObjectGenerator {

    private static final String[] MOBS = {EntityID.SKELETON, EntityID.ZOMBIE, EntityID.ZOMBIE, EntityID.SPIDER};
    protected final Xoroshiro128 random = new Xoroshiro128();

    protected static final BlockState MOSSY_COBBLESTONE = BlockMossyCobblestone.PROPERTIES.getDefaultState();
    protected static final BlockState COBBLESTONE = BlockCobblestone.PROPERTIES.getDefaultState();
    protected static final BlockState MOB_SPAWNER = BlockMobSpawner.PROPERTIES.getDefaultState();

    protected static final ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    @Override
    public boolean generate(BlockManager object, RandomSourceProvider rand, Vector3 position) {
        StructureHelper helper = new StructureHelper(object.getLevel(), position.asBlockVector3());
        helper.fill(new BlockVector3(-3, -1, -3), new BlockVector3(3, 3, 3), COBBLESTONE, BlockAir.STATE);
        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                helper.setBlockStateAt(x, -1, z, rand.nextInt(4) == 0 ? COBBLESTONE : MOSSY_COBBLESTONE);
                helper.setBlockStateAt(x, 3, z, BlockAir.STATE);
            }
        }
        for(int i = 0; i < 2; i++) {
            int x = rand.nextBoolean() ? -2 : 2;
            int z = rand.nextInt(5) - 2;
            Optional<BlockFace> faceOptional = Arrays.stream(BlockFace.getHorizontals()).filter(f -> helper.getBlockAt(x, 0, z).getSide(f).isSolid()).findFirst();
            if(faceOptional.isPresent()) {
                BlockFace face = faceOptional.get();
                helper.setBlockStateAt(x, 0, z, BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.valueOf(face.getName().toUpperCase()))));
                object.getLevel().getScheduler().scheduleDelayedTask(() -> {
                    Block block = helper.getBlockAt(x, 0, z);
                    if (block instanceof BlockEntityHolder<?> holder && holder.getBlockEntity() instanceof BlockEntityChest chest) {
                        CHEST_POPULATOR.create(chest.getInventory(), random);
                    }
                }, 10);
            }
        }
        helper.setBlockStateAt(0, 0, 0, MOB_SPAWNER);
        object.getLevel().getScheduler().scheduleDelayedTask(() -> {
            Block block = helper.getBlockAt(0, 0, 0);
            if (block instanceof BlockEntityHolder<?> holder && holder.getBlockEntity() instanceof BlockEntityMobSpawner spawner) {
                int randomIndex = random.nextInt(MOBS.length);
                int entityId = Registries.ENTITY.getEntityNetworkId(MOBS[randomIndex]);
                spawner.setSpawnEntityType(entityId);
            }
        }, 10);
        object.merge(helper);
        return true;
    }

    @Override
    public String getName() {
        return "monster_room";
    }

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        Level level = location.getLevel();
        random.setSeed(level.getSeed() ^ (x+y+z));

        chance:
        for (int chance = 0; chance < 8; ++chance) {
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

                        Block block = level.getBlock(tx, ty, tz);
                        boolean isSolid = block.isSolid();

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
                return true;
            }
        }
        return false;
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.SADDLE, 20))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 15))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 2))
                    .register(new ItemEntry(Item.MUSIC_DISC_13, 15))
                    .register(new ItemEntry(Item.MUSIC_DISC_CAT, 15))
                    .register(new ItemEntry(Item.NAME_TAG, 20))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 10))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 15))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 10, getDefaultEnchantments()));
            this.pools.put(pool1.build(), new RollEntry(3, 1, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 4, 10))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 4, 5))
                    .register(new ItemEntry(Item.BREAD, 20))
                    .register(new ItemEntry(Block.WHEAT, 0, 4, 20))
                    .register(new ItemEntry(Item.BUCKET, 10))
                    .register(new ItemEntry(Item.REDSTONE, 0, 4, 15))
                    .register(new ItemEntry(Item.COAL, 0, 4, 15))
                    .register(new ItemEntry(Item.MELON_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.PUMPKIN_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 4, 2, 10));
            this.pools.put(pool2.build(), new RollEntry(4, 1, pool2.getTotalWeight()));

            PoolBuilder pool3 = new PoolBuilder()
                    .register(new ItemEntry(Item.BONE, 0, 8, 10))
                    .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 10))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 10))
                    .register(new ItemEntry(Item.STRING, 0, 8, 10));
            this.pools.put(pool3.build(), new RollEntry(3, pool3.getTotalWeight()));
        }
    }
}
