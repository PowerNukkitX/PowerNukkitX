package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockCobblestone;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.block.BlockMossyCobblestone;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityMobSpawner;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Arrays;
import java.util.Optional;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class ObjectMonsterRoom extends RuledObjectGenerator {

    private static final String[] MOBS = {EntityID.SKELETON, EntityID.ZOMBIE, EntityID.ZOMBIE, EntityID.SPIDER};

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
                    CHEST_POPULATOR.create(((BlockEntityHolder<BlockEntityChest>) helper.getBlockAt(x, 0, z)).getOrCreateBlockEntity().getInventory(), random);
                }, 10);
            }
        }
        helper.setBlockStateAt(0, 0, 0, MOB_SPAWNER);
        object.getLevel().getScheduler().scheduleDelayedTask(() -> {
            ((BlockEntityHolder<BlockEntityMobSpawner>) helper.getBlockAt(0, 0, 0)).getOrCreateBlockEntity().setSpawnEntityType(Registries.ENTITY.getEntityNetworkId(MOBS[random.nextInt(MOBS.length)]));
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
