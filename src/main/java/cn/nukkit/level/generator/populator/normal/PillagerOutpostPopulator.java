package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockJigsaw;
import cn.nukkit.block.BlockWallBanner;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
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
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.network.protocol.types.BannerPatternType;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.block.BlockID.*;

public class PillagerOutpostPopulator extends Populator {

    public static final String NAME = "normal_pillager_outpost";

    protected static final ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    protected static final PNXStructure WATCHTOWER = (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/watchtower");
    protected static final PNXStructure WATCHTOWER_OVERGROWN = (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/watchtower_overgrown");

    protected static final PNXStructure[] FEATURES = new PNXStructure[]{
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_cage1"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_cage2"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_logs"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_tent1"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_tent2"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_targets"),
            (PNXStructure) Registries.STRUCTURE.get("pillager_outpost/feature_cage_with_allays")
    };

    public static final BannerPattern[] BANNER = new BannerPattern[] {
            new BannerPattern(BannerPatternType.RHOMBUS, DyeColor.CYAN),
            new BannerPattern(BannerPatternType.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY),
            new BannerPattern(BannerPatternType.STRIPE_CENTER, DyeColor.GRAY),
            new BannerPattern(BannerPatternType.BORDER, DyeColor.LIGHT_GRAY),
            new BannerPattern(BannerPatternType.STRIPE_MIDDLE, DyeColor.BLACK),
            new BannerPattern(BannerPatternType.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY),
            new BannerPattern(BannerPatternType.CIRCLE, DyeColor.LIGHT_GRAY),
            new BannerPattern(BannerPatternType.BORDER, DyeColor.BLACK)
    };


    protected static final int SPACING = 32;
    protected static final int SEPARATION = 8;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        if ((biome == BiomeID.PLAINS || biome == BiomeID.DESERT || biome == BiomeID.SAVANNA || biome == BiomeID.TAIGA || biome == BiomeID.ICE_PLAINS || biome == BiomeID.COLD_TAIGA
                || biome == BiomeID.SUNFLOWER_PLAINS || biome == BiomeID.MEADOW || biome == BiomeID.GROVE || biome == BiomeID.SNOWY_SLOPES || biome == BiomeID.JAGGED_PEAKS
                || biome == BiomeID.FROZEN_PEAKS || biome == BiomeID.STONY_PEAKS || biome == BiomeID.CHERRY_GROVE)
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextInt(SPACING - SEPARATION)) {
            random.setSeed(((chunkX >> 4) ^ (chunkZ >> 4) << 4) ^ level.getSeed());
            random.nextInt();
            if (random.nextInt(5) == 0) {
                int y = chunk.getHeightMap(0, 0);

                Block block = chunk.getBlockState(0, y, 0).toBlock();
                while (block.canBeReplaced() && y > 1) {
                    block = chunk.getBlockState(0, --y, 0).toBlock();
                }
                BlockManager manager = new BlockManager(level);
                Position vec = new Position(chunkX << 4, y, chunkZ << 4);
                WATCHTOWER.preparePlace(vec, manager);
                BlockManager manager2 = new BlockManager(level);
                WATCHTOWER_OVERGROWN.preparePlace(vec, manager2);
                for(Block b : manager2.getBlocks()) {
                    if(random.nextInt(20) != 0) manager2.unsetBlockStateAt(b);
                }
                manager.merge(manager2);

                BlockVector3 size = new BlockVector3(WATCHTOWER.getSizeX(), WATCHTOWER.getSizeY(), WATCHTOWER.getSizeZ());
                fillBase(level.getChunk(chunkX, chunkZ), y, 0, 0, size.getX(), size.getZ());
                random.setSeed(((chunkX >> 4) ^ (chunkZ >> 4) << 4) ^ level.getSeed());
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ - 1), random, manager);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ + 1), random, manager);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ - 1), random, manager);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ + 1), random, manager);
                }
                for(Block block1 : manager.getBlocks()) {
                    if(block1.isAir()) manager.unsetBlockStateAt(block1);
                    if(block1 instanceof BlockChest chest) {
                        level.getScheduler().scheduleDelayedTask(() -> {
                            CHEST_POPULATOR.create(chest.getOrCreateBlockEntity().getInventory(), random);
                        }, 10);
                    }
                    if(block1 instanceof BlockJigsaw) {
                        manager.unsetBlockStateAt(block1);
                    }
                    if(block1 instanceof BlockWallBanner banner) {
                        level.getScheduler().scheduleDelayedTask(() -> {
                            BlockEntityBanner be = banner.getOrCreateBlockEntity();
                            be.setType(1);
                            be.spawnToAll();
                        }, 1);
                    }
                }
                queueObject(chunk, manager);
            }
        }
    }

    protected static void fillBase(IChunk chunk, int baseY, int startX, int startZ, int sizeX, int sizeZ) {
        for (int x = startX; x < startX + sizeX; x++) {
            for (int z = startZ; z < startZ + sizeZ; z++) {
                String baseId = chunk.getBlockState(x, baseY, z).getIdentifier();

                switch (baseId) {
                    case COBBLESTONE:
                    case MOSSY_COBBLESTONE:
                    case OAK_LOG:
                    case DARK_OAK_LOG:
                    case OAK_PLANKS:
                    case DARK_OAK_PLANKS:
                    case OAK_FENCE:
                    case DARK_OAK_FENCE:
                        int y = baseY - 1;
                        Block id = chunk.getBlockState(x, y, z).toBlock();
                        while (id.canBeReplaced() && y > 1) {
                            chunk.setBlockState(x, y, z, Registries.BLOCK.get(baseId).getBlockState());
                            id = chunk.getBlockState(x, --y, z).toBlock();
                        }
                }
            }
        }
    }

    protected void tryPlaceFeature(IChunk chunk, RandomSourceProvider random, BlockManager manager) {
        PNXStructure template = FEATURES[random.nextInt(FEATURES.length)];
        int seed = random.nextInt();
        this.placeFeature(template, chunk, seed, manager);
    }

    protected void placeFeature(PNXStructure template, IChunk chunk, int seed, BlockManager manager) {
        NukkitRandom random = new NukkitRandom(seed);

        BlockVector3 size = new BlockVector3(template.getSizeX(), template.getSizeY(), template.getSizeZ());
        int x = random.nextBoundedInt(16 - size.getX());
        int z = random.nextBoundedInt(16 - size.getZ());
        int y = chunk.getHeightMap(x, z);

        Position base = new Position((chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z, manager.getLevel());
        template.preparePlace(base, manager);
        chunk.getLevel().getScheduler().scheduleDelayedTask(() -> {
            switch (template.getName()) {
                case "pillager_outpost/feature_cage1",
                     "pillager_outpost/feature_cage2" -> {
                    Entity entity = Entity.createEntity(Entity.IRON_GOLEM, base.add(3.5,1,3.5));
                    entity.spawnToAll();
                }
                case "pillager_outpost/feature_cage_with_allays" ->{
                    Entity entity = Entity.createEntity(Entity.ALLAY, base.add(4.5,2,4.5));
                    entity.spawnToAll();
                }
            }
        }, 10);
        fillBase(chunk, y, x, z, size.getX(), size.getZ());
    }

    @Override
    public String name() {
        return NAME;
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.CROSSBOW, 1));
            this.pools.put(pool1.build(), new RollEntry(1, 0, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Block.WHEAT, 0, 5, 3, 7))
                    .register(new ItemEntry(Item.POTATO, 0, 5, 2, 5))
                    .register(new ItemEntry(Item.CARROT, 0, 5, 3, 5));
            this.pools.put(pool2.build(), new RollEntry(3, 2, pool2.getTotalWeight()));

            PoolBuilder pool3 = new PoolBuilder()
                    .register(new ItemEntry(DARK_OAK_LOG, 0, 3, 2, 1));
            this.pools.put(pool3.build(), new RollEntry(3, 1, pool3.getTotalWeight()));

            PoolBuilder pool4 = new PoolBuilder()
                    .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 7))
                    .register(new ItemEntry(Item.STRING, 0, 6, 4))
                    .register(new ItemEntry(Item.ARROW, 0, 7, 2, 4))
                    .register(new ItemEntry(Block.TRIPWIRE_HOOK, 0, 3, 3))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 3, 3))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0,1, 1, 1, getDefaultEnchantments()));
            this.pools.put(pool4.build(), new RollEntry(3, 2, pool4.getTotalWeight()));
        }
    }
}
