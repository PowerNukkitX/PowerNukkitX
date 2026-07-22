package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockStructureBlock;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.RandomizableContainer;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.level.structure.PNXStructure;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import com.google.common.collect.Sets;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class ShipwreckPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_shipwreck";

    private static final MapChestPopulator MAP = new MapChestPopulator();
    private static final SupplyChestPopulator SUPPLY = new SupplyChestPopulator();
    private static final TreasureChestPopulator TREASURE = new TreasureChestPopulator();

    protected static final PNXStructure WITH_MAST = (PNXStructure) Registries.STRUCTURE.get("shipwreck/with_mast");
    protected static final PNXStructure UPSIDEDOWN_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_full");
    protected static final PNXStructure UPSIDEDOWN_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_fronthalf");
    protected static final PNXStructure UPSIDEDOWN_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_backhalf");
    protected static final PNXStructure SIDEWAYS_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_full");
    protected static final PNXStructure SIDEWAYS_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_fronthalf");
    protected static final PNXStructure SIDEWAYS_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_backhalf");
    protected static final PNXStructure RIGHTSIDEUP_FULL = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_full");
    protected static final PNXStructure RIGHTSIDEUP_FRONTHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_fronthalf");
    protected static final PNXStructure RIGHTSIDEUP_BACKHALF = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_backhalf");
    protected static final PNXStructure WITH_MAST_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/with_mast_degraded");
    protected static final PNXStructure UPSIDEDOWN_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_full_degraded");
    protected static final PNXStructure UPSIDEDOWN_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_fronthalf_degraded");
    protected static final PNXStructure UPSIDEDOWN_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/upsidedown_backhalf_degraded");
    protected static final PNXStructure SIDEWAYS_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_full_degraded");
    protected static final PNXStructure SIDEWAYS_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_fronthalf_degraded");
    protected static final PNXStructure SIDEWAYS_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/sideways_backhalf_degraded");
    protected static final PNXStructure RIGHTSIDEUP_FULL_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_full_degraded");
    protected static final PNXStructure RIGHTSIDEUP_FRONTHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_fronthalf_degraded");
    protected static final PNXStructure RIGHTSIDEUP_BACKHALF_DEGRADED = (PNXStructure) Registries.STRUCTURE.get("shipwreck/rightsideup_backhalf_degraded");
    protected static final PNXStructure[] STRUCTURE_LOCATION_BEACHED = new PNXStructure[]{
            WITH_MAST,
            SIDEWAYS_FULL,
            SIDEWAYS_FRONTHALF,
            SIDEWAYS_BACKHALF,
            RIGHTSIDEUP_FULL,
            RIGHTSIDEUP_FRONTHALF,
            RIGHTSIDEUP_BACKHALF,
            WITH_MAST_DEGRADED,
            RIGHTSIDEUP_FULL_DEGRADED,
            RIGHTSIDEUP_FRONTHALF_DEGRADED,
            RIGHTSIDEUP_BACKHALF_DEGRADED
    };
    protected static final PNXStructure[] STRUCTURE_LOCATION_OCEAN = new PNXStructure[]{
            WITH_MAST,
            UPSIDEDOWN_FULL,
            UPSIDEDOWN_FRONTHALF,
            UPSIDEDOWN_BACKHALF,
            SIDEWAYS_FULL,
            SIDEWAYS_FRONTHALF,
            SIDEWAYS_BACKHALF,
            RIGHTSIDEUP_FULL,
            RIGHTSIDEUP_FRONTHALF,
            RIGHTSIDEUP_BACKHALF,
            WITH_MAST_DEGRADED,
            UPSIDEDOWN_FULL_DEGRADED,
            UPSIDEDOWN_FRONTHALF_DEGRADED,
            UPSIDEDOWN_BACKHALF_DEGRADED,
            SIDEWAYS_FULL_DEGRADED,
            SIDEWAYS_FRONTHALF_DEGRADED,
            SIDEWAYS_BACKHALF_DEGRADED,
            RIGHTSIDEUP_FULL_DEGRADED,
            RIGHTSIDEUP_FRONTHALF_DEGRADED,
            RIGHTSIDEUP_BACKHALF_DEGRADED
    };

    protected enum ShipwreckWood {
        OAK(Block.STRIPPED_OAK_LOG, Block.OAK_PLANKS, Block.OAK_STAIRS, Block.OAK_SLAB, Block.OAK_FENCE, Block.TRAPDOOR, Block.WOODEN_DOOR),
        SPRUCE(Block.STRIPPED_SPRUCE_LOG, Block.SPRUCE_PLANKS, Block.SPRUCE_STAIRS, Block.SPRUCE_SLAB, Block.SPRUCE_FENCE, Block.SPRUCE_TRAPDOOR, Block.SPRUCE_DOOR),
        BIRCH(Block.STRIPPED_BIRCH_LOG, Block.BIRCH_PLANKS, Block.BIRCH_STAIRS, Block.BIRCH_SLAB, Block.BIRCH_FENCE, Block.BIRCH_TRAPDOOR, Block.BIRCH_DOOR),
        JUNGLE(Block.STRIPPED_JUNGLE_LOG, Block.JUNGLE_PLANKS, Block.JUNGLE_STAIRS, Block.JUNGLE_SLAB, Block.JUNGLE_FENCE, Block.JUNGLE_TRAPDOOR, Block.JUNGLE_DOOR),
        ACACIA(Block.STRIPPED_ACACIA_LOG, Block.ACACIA_PLANKS, Block.ACACIA_STAIRS, Block.ACACIA_SLAB, Block.ACACIA_FENCE, Block.ACACIA_TRAPDOOR, Block.ACACIA_DOOR),
        DARK_OAK(Block.STRIPPED_DARK_OAK_LOG, Block.DARK_OAK_PLANKS, Block.DARK_OAK_STAIRS, Block.DARK_OAK_SLAB, Block.DARK_OAK_FENCE, Block.DARK_OAK_TRAPDOOR, Block.DARK_OAK_DOOR);

        final String strippedLog;
        final String planks;
        final String stairs;
        final String slab;
        final String fence;
        final String trapdoor;
        final String door;

        ShipwreckWood(String strippedLog, String planks, String stairs, String slab, String fence, String trapdoor, String door) {
            this.strippedLog = strippedLog;
            this.planks = planks;
            this.stairs = stairs;
            this.slab = slab;
            this.fence = fence;
            this.trapdoor = trapdoor;
            this.door = door;
        }
    }

    protected static final List<Map<String, String>> WOOD_PALETTES = List.of(
            woodPalette(ShipwreckWood.OAK, ShipwreckWood.SPRUCE),
            woodPalette(ShipwreckWood.OAK, ShipwreckWood.BIRCH),
            woodPalette(ShipwreckWood.OAK, ShipwreckWood.DARK_OAK),
            woodPalette(ShipwreckWood.SPRUCE, ShipwreckWood.OAK),
            woodPalette(ShipwreckWood.SPRUCE, ShipwreckWood.JUNGLE),
            woodPalette(ShipwreckWood.SPRUCE, ShipwreckWood.DARK_OAK),
            woodPalette(ShipwreckWood.BIRCH, ShipwreckWood.OAK),
            woodPalette(ShipwreckWood.BIRCH, ShipwreckWood.SPRUCE),
            woodPalette(ShipwreckWood.JUNGLE, ShipwreckWood.OAK),
            woodPalette(ShipwreckWood.JUNGLE, ShipwreckWood.SPRUCE),
            woodPalette(ShipwreckWood.JUNGLE, ShipwreckWood.ACACIA),
            woodPalette(ShipwreckWood.SPRUCE, ShipwreckWood.ACACIA),
            woodPalette(ShipwreckWood.DARK_OAK, ShipwreckWood.SPRUCE),
            woodPalette(ShipwreckWood.DARK_OAK, ShipwreckWood.JUNGLE),
            woodPalette(ShipwreckWood.DARK_OAK, ShipwreckWood.ACACIA)
    );

    protected static Map<String, String> woodPalette(ShipwreckWood primary, ShipwreckWood secondary) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(Block.OAK_LOG, primary.strippedLog);
        mapping.put(Block.OAK_PLANKS, primary.planks);
        mapping.put(Block.OAK_STAIRS, primary.stairs);
        mapping.put(Block.OAK_SLAB, primary.slab);
        mapping.put(Block.OAK_FENCE, primary.fence);
        mapping.put(Block.TRAPDOOR, primary.trapdoor);
        mapping.put(Block.WOODEN_DOOR, primary.door);
        mapping.put(Block.SPRUCE_LOG, secondary.strippedLog);
        mapping.put(Block.SPRUCE_PLANKS, secondary.planks);
        mapping.put(Block.SPRUCE_STAIRS, secondary.stairs);
        mapping.put(Block.SPRUCE_SLAB, secondary.slab);
        mapping.put(Block.SPRUCE_FENCE, secondary.fence);
        return mapping;
    }

    protected static BlockState applyWood(BlockState state, Map<String, String> mapping) {
        String target = mapping.get(state.getIdentifier());
        if (target == null || target.equals(state.getIdentifier())) return state;
        var values = state.getBlockPropertyValues();
        return Registries.BLOCK.getBlockProperties(target).getBlockState(values.toArray(BlockPropertyType.BlockPropertyValue[]::new));
    }

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(165745295L)
            .minDistance(4)
            .maxDistance(24)
            .isBiomeValid(biome -> {
                return Registries.BIOME.getTags(biome).contains(BiomeTags.OCEAN) || Registries.BIOME.getTags(biome).contains(BiomeTags.BEACH);
            })
            .build());


    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(5, chunk.getHeightMap(5, 5), 5);
        BiomeDefinitionData definition = Registries.BIOME.get(biome).second();
        if (PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, biome)) {
            PNXStructure template;
            boolean beach = Registries.BIOME.containsTag(BiomeTags.BEACH, definition);
            if (beach) {
                template = STRUCTURE_LOCATION_BEACHED[random.nextInt(STRUCTURE_LOCATION_BEACHED.length)];
            } else {
                template = STRUCTURE_LOCATION_OCEAN[random.nextInt(STRUCTURE_LOCATION_OCEAN.length)];
            }

            Map<String, String> wood = WOOD_PALETTES.get(random.nextInt(WOOD_PALETTES.size()));
            template = template.withPalette(state -> applyWood(state, wood));

            BlockVector3 size = new BlockVector3(template.getSizeX(), template.getSizeY(), template.getSizeZ());
            int sumY = 0;
            int blockCount = 0;

            for (int x = 0; x < size.getX() && x < 16; x++) {
                for (int z = 0; z < size.getZ() && z < 16; z++) {
                    int y = chunk.getHeightMap(x, z);

                    Block b = chunk.getBlockState(x, y, z).toBlock();
                    while (b.canBeReplaced() && y > level.getMinHeight()) {
                        b = chunk.getBlockState(x, --y, z).toBlock();
                    }

                    sumY += y;
                    blockCount++;
                }
            }

            int y = sumY / blockCount;

            Set<IChunk> chunks = Sets.newHashSet();
            Set<Long> indexes = Sets.newConcurrentHashSet();

            if (size.getX() > 16) {
                IChunk ck = level.getOrGenerateChunk(chunkX + 1, chunkZ);
                chunks.add(ck);
                indexes.add(Level.chunkHash(ck.getX(), chunkZ));
            }
            if (size.getZ() > 16) {
                IChunk ck = level.getOrGenerateChunk(chunkX, chunkZ + 1);
                chunks.add(ck);
                indexes.add(Level.chunkHash(chunkX, ck.getZ()));
            }



            BlockManager manager = new BlockManager(level);
            this.placeInLevel(manager, chunkX, chunkZ, template, y);
            for(Block block : manager.getBlocks()) {
                if(block instanceof BlockAir) manager.unsetBlockStateAt(block);
                if(block instanceof BlockStructureBlock) manager.unsetBlockStateAt(block);
                if(block instanceof BlockChest chest) {
                    RandomizableContainer container = switch (random.nextInt(6)) {
                        case 0 -> MAP;
                        case 1, 2 -> TREASURE;
                        default -> SUPPLY;
                    };
                    manager.addHook(() -> {
                        container.create(chest.getOrCreateBlockEntity().getInventory(), random);
                    });
                }
                if(block.getFloorY() < SEA_LEVEL) {
                    //WaterLogging does not work with BlockManager. Therefore, we set the water in the level.
                    manager.getLevel().setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), 1, BlockWater.PROPERTIES.getDefaultState());
                }
            }
            queueObject(chunk, manager);
        }
    }

    protected void placeInLevel(BlockManager manager, int chunkX, int chunkZ, PNXStructure template, int y) {
        Position vec = new Position(chunkX << 4, y, chunkZ << 4, manager.getLevel());
        template.preparePlace(vec, manager);
    }

    protected static class MapChestPopulator extends RandomizableContainer {
        public MapChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.EMPTY_MAP, 1)); // 395 exploration_map
            this.pools.put(pool1.build(), new RollEntry(1, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.COMPASS, 1))
                    .register(new ItemEntry(Item.EMPTY_MAP, 1)) // 395
                    .register(new ItemEntry(Item.CLOCK, 1))
                    .register(new ItemEntry(Item.PAPER, 0, 10, 20))
                    .register(new ItemEntry(Item.FEATHER, 0, 5, 10))
                    .register(new ItemEntry(Item.BOOK, 0, 5, 5));
            this.pools.put(pool2.build(), new RollEntry(3, pool2.getTotalWeight()));
        }
    }

    protected static class SupplyChestPopulator extends RandomizableContainer {
        public SupplyChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.PAPER, 0, 12, 8))
                    .register(new ItemEntry(Item.POTATO, 0, 6, 2, 7))
                    .register(new ItemEntry(Item.POISONOUS_POTATO, 0, 6, 2, 7))
                    .register(new ItemEntry(Item.CARROT, 0, 8, 4, 7))
                    .register(new ItemEntry(Block.WHEAT, 0, 21, 8, 7))
                    .register(new ItemEntry(Item.COAL, 0, 8, 2, 6))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 24, 5, 5))
                    .register(new ItemEntry(Block.REEDS, 0, 3, 2))
                    .register(new ItemEntry(Block.PUMPKIN, 0, 3, 2))
                    .register(new ItemEntry(Item.GUNPOWDER, 0, 5, 3))
                    .register(new ItemEntry(Block.TNT, 0, 2, 1))
                    .register(new ItemEntry(Item.LEATHER_HELMET, 0, 1, 1,3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.LEATHER_CHESTPLATE, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.LEATHER_LEGGINGS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.LEATHER_BOOTS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.SUSPICIOUS_STEW, 10));
            this.pools.put(pool1.build(), new RollEntry(10, 3, pool1.getTotalWeight()));
        }
    }

    protected static class TreasureChestPopulator extends RandomizableContainer {
        public TreasureChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 90))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 5, 10))
                    .register(new ItemEntry(Item.EMERALD, 0, 5, 40))
                    .register(new ItemEntry(Item.DIAMOND, 5))
                    .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 5));
            this.pools.put(pool1.build(), new RollEntry(6, 3, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 10, 50))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 10, 10))
                    .register(new ItemEntry(Item.LAPIS_LAZULI, 0, 10, 20));
            this.pools.put(pool2.build(), new RollEntry(5, 2, pool2.getTotalWeight()));
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
