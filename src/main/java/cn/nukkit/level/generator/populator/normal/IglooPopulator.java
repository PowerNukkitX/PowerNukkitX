package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.mob.EntityZombieVillagerV2;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.item.ItemSplashPotion;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.Registries;

import java.util.Random;

public class IglooPopulator extends Populator {

    public static final String NAME = "normal_igloo";

    protected static final int SPACING = 32;
    protected static final int SEPARATION = 8;

    protected final PNXStructure TOP = (PNXStructure) Registries.STRUCTURE.get("igloo/top");
    protected final PNXStructure MIDDLE = (PNXStructure) Registries.STRUCTURE.get("igloo/middle");
    protected final PNXStructure BOTTOM = (PNXStructure) Registries.STRUCTURE.get("igloo/bottom");

    protected static final ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(3, chunk.getHeightMap(3, 3), 3);
        if ((biome == BiomeID.ICE_PLAINS || biome == BiomeID.COLD_TAIGA|| biome == BiomeID.SNOWY_SLOPES)
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            boolean hasLaboratory = random.nextBoolean();

            BlockVector3 size = new BlockVector3(TOP.getSizeX(), TOP.getSizeY(), TOP.getSizeZ());
            int sumY = 0;
            int blockCount = 0;

            for (int x = 0; x < size.getX(); x++) {
                for (int z = 2; z < size.getZ() + 2; z++) {
                    int y = chunk.getHeightMap(x, z);

                    Block block = chunk.getBlockState(x, y, z).toBlock();
                    while (block.canBeReplaced() && y > 64) {
                        block = chunk.getBlockState(x, --y, z).toBlock();
                    }

                    sumY += Math.max(64, y);
                    blockCount++;
                }
            }
            BlockManager object = new BlockManager(level);
            BlockVector3 vec = new BlockVector3(chunkX << 4, sumY / blockCount, (chunkZ << 4) + 2);
            TOP.preparePlace(new Position(vec.x, vec.y, vec.z, level), object);
            if (hasLaboratory) {

                int yOffset = MIDDLE.getSizeY();
                vec.x += 2;
                vec.z += 4;

                for (int i = 0; i < random.nextBoundedInt(8) + 3; ++i) {
                    vec.y -= yOffset;
                    MIDDLE.preparePlace(new Position(vec.x, vec.y, vec.z, level), object);
                }

                vec.x -= 2;
                vec.z -= 6;
                vec.y -= BOTTOM.getSizeY();

                BOTTOM.preparePlace(new Position(vec.x, vec.y, vec.z, level), object);
                level.getScheduler().scheduleTask(() -> {
                    CompoundTag nbt = new CompoundTag()
                            .putList("Pos", new ListTag<DoubleTag>()
                                    .add(new DoubleTag( vec.x + 2.5))
                                    .add(new DoubleTag(vec.y + 1))
                                    .add(new DoubleTag(vec.z + 1.5)))
                            .putList("Motion", new ListTag<DoubleTag>()
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0)))
                            .putList("Rotation", new ListTag<FloatTag>()
                                    .add(new FloatTag(new Random().nextFloat() * 360))
                                    .add(new FloatTag(0)));
                    EntityVillagerV2 entity = (EntityVillagerV2) Entity.createEntity(Entity.VILLAGER_V2, chunk, nbt);
                    entity.setProfession(random.nextInt(Profession.getProfessions().size()), true);
                    CompoundTag nbt2 = new CompoundTag()
                            .putList("Pos", new ListTag<DoubleTag>()
                                    .add(new DoubleTag( vec.x + 4.5))
                                    .add(new DoubleTag(vec.y + 1))
                                    .add(new DoubleTag(vec.z + 1.5)))
                            .putList("Motion", new ListTag<DoubleTag>()
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0))
                                    .add(new DoubleTag(0)))
                            .putList("Rotation", new ListTag<FloatTag>()
                                    .add(new FloatTag(new Random().nextFloat() * 360))
                                    .add(new FloatTag(0)));
                    EntityZombieVillagerV2 entity2 = (EntityZombieVillagerV2) Entity.createEntity(Entity.ZOMBIE_VILLAGER_V2, chunk, nbt2);
                    entity2.spawnToAll();
                });
            } else object.setBlockStateAt(new BlockVector3(vec.x +3, vec.y, vec.z+5), BlockSnow.PROPERTIES.getDefaultState());
            for(Block block : object.getBlocks()) {
                if(block instanceof BlockStructureBlock) object.setBlockStateAt(block, BlockAir.STATE);
                if(block instanceof BlockIce) object.setBlockStateAt(block, BlockPackedIce.PROPERTIES.getDefaultState());
                if(block instanceof BlockBrewingStand stand) {
                    stand.getOrCreateBlockEntity().getInventory().setResult(2, ItemSplashPotion.get(ItemPotion.SPLASH_POTION, PotionType.WEAKNESS.id()));
                }
                if(block instanceof BlockChest chest) {
                    CHEST_POPULATOR.create(chest.getOrCreateBlockEntity().getInventory(), random);
                }
                if(block instanceof BlockBed bed) {
                    bed.createBlockEntity(new CompoundTag().putByte("color", 14));
                }
                if(block instanceof BlockFlowerPot pot) {
                    pot.setFlower(Item.get(Block.CACTUS));
                }
            }
            object.generateChunks();
            queueObject(chunk, object);
        }
    }

    @Override
    public String name() {
        return NAME;
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.APPLE, 0, 3, 15))
                    .register(new ItemEntry(Item.COAL, 0, 4, 15))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 0, 3, 10))
                    .register(new ItemEntry(Item.STONE_AXE, 2))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 10))
                    .register(new ItemEntry(Item.EMERALD, 1))
                    .register(new ItemEntry(Block.WHEAT, 0, 3, 2, 10));
            this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 1));
            this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
        }
    }
}
