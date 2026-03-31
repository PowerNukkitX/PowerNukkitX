package cn.nukkit.level.generator.object.structures.jigsaw.trialchambers;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBarrel;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockDecoratedPot;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockTrialSpawner;
import cn.nukkit.block.copper.bulb.BlockCopperBulb;
import cn.nukkit.blockentity.BlockEntityDecoratedPot;
import cn.nukkit.blockentity.BlockEntityTrialSpawner;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Map;

/**
 * Trial Chamber Structure for PowerNukkitX
 * @author Buddelbubi
 */
public class TrialChambersStructure extends JigsawStructure implements RuledObjectGenerator {

    private static final String START = "trial_chambers/chamber/end";
    private static final String HALLWAY_FALLBACK = "trial_chambers/hallway/fallback";
    private static final StructurePoolCollection COLLECTION;

    private static final Object2ObjectMap<String, RandomizableContainer> CHEST_LOOT_CATEGORY_LOOKUP;
    private static final Object2ObjectMap<String, RandomizableContainer> DISPENSER_LOOT_CATEGORY_LOOKUP;
    private static final Object2ObjectMap<String, String> TRIAL_SPAWNER_ENTITY_LOOKUP;

    static {
        CHEST_LOOT_CATEGORY_LOOKUP = new Object2ObjectArrayMap<>();
        CHEST_LOOT_CATEGORY_LOOKUP.put("corridor", new CorridorChestPopulator());
        CHEST_LOOT_CATEGORY_LOOKUP.put("hallway", new CorridorChestPopulator());
        CHEST_LOOT_CATEGORY_LOOKUP.put("entrance", new EntranceChestPopulator());
        CHEST_LOOT_CATEGORY_LOOKUP.put("intersection", new IntersectionChestPopulator());
        CHEST_LOOT_CATEGORY_LOOKUP.put("supply", new SupplyChestPopulator());

        DISPENSER_LOOT_CATEGORY_LOOKUP = new Object2ObjectArrayMap<>();
        DISPENSER_LOOT_CATEGORY_LOOKUP.put("corridor", new CorridorDispenserPopulator());
        DISPENSER_LOOT_CATEGORY_LOOKUP.put("water", new WaterDispenserPopulator());
        DISPENSER_LOOT_CATEGORY_LOOKUP.put("chamber", new ChamberDispenserPopulator());

        TRIAL_SPAWNER_ENTITY_LOOKUP = new Object2ObjectArrayMap<>();
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("slime", EntityID.SLIME);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("cave_spider", EntityID.CAVE_SPIDER);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("silverfish", EntityID.SILVERFISH);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("spider", EntityID.SPIDER);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("breeze", EntityID.BREEZE);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("stray", EntityID.STRAY);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("poison_skeleton", EntityID.BOGGED);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("skeleton", EntityID.SKELETON);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("husk", EntityID.HUSK);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("zombie", EntityID.ZOMBIE);
        TRIAL_SPAWNER_ENTITY_LOOKUP.put("baby_zombie", EntityID.ZOMBIE);

    }
    private static final IntersectionBarrelPopulator INTERSECTION_BARREL = new IntersectionBarrelPopulator();

    private static final DecoratedPotPopulator DECORATED_POT = new DecoratedPotPopulator();

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    protected boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
        return intersects(first, second)
                && !contains(first, second)
                && !contains(second, first);
    }

    private boolean intersects(BoundingBox first, BoundingBox second) {
        return first.x1 > second.x0 && first.x0 < second.x1
                && first.y1 > second.y0 && first.y0 < second.y1
                && first.z1 > second.z0 && first.z0 < second.z1;
    }

    private boolean contains(BoundingBox outer, BoundingBox inner) {
        return outer.x0 <= inner.x0 && outer.x1 >= inner.x1
                && outer.y0 <= inner.y0 && outer.y1 >= inner.y1
                && outer.z0 <= inner.z0 && outer.z1 >= inner.z1;
    }

    @Override
    public String getEntryPool() {
        return START;
    }

    @Override
    protected int getMaxDepth() {
        return 20;
    }

    protected RandomizableContainer getChestLootContainer(String structureName) {
        for (Map.Entry<String, RandomizableContainer> entry : CHEST_LOOT_CATEGORY_LOOKUP.object2ObjectEntrySet()) {
            if (structureName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected RandomizableContainer getDispenserLootContainer(String structureName) {
        for (Map.Entry<String, RandomizableContainer> entry : DISPENSER_LOOT_CATEGORY_LOOKUP.object2ObjectEntrySet()) {
            if (structureName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected String getTrialSpawnerEntityId(String structureName) {
        int lastSlash = structureName.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash + 1 >= structureName.length()) {
            return null;
        }
        return TRIAL_SPAWNER_ENTITY_LOOKUP.get(structureName.substring(lastSlash + 1));
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        RandomizableContainer container = getChestLootContainer(structureName);
        RandomizableContainer dispenserLootContainer = getDispenserLootContainer(structureName);
        for (Block block : blockManager.getBlocks()) {
            Level level = blockManager.getLevel();
            switch (block) {
                case BlockChest chest -> {
                    if(container != null) {
                        blockManager.addHook(() -> {
                            container.create(chest.getOrCreateBlockEntity().getInventory(), createRandom(level, block.asBlockVector3()));
                        });
                    }
                }
                case BlockBarrel barrel -> {
                    blockManager.addHook(() -> {
                        INTERSECTION_BARREL.create(barrel.getOrCreateBlockEntity().getInventory(), createRandom(level, block.asBlockVector3()));
                    });
                }
                case BlockDispenser dispenser -> {
                    blockManager.addHook(() -> {
                        dispenserLootContainer.create(dispenser.getOrCreateBlockEntity().getInventory(), createRandom(level, block.asBlockVector3()));
                    });
                }
                case BlockDecoratedPot decoratedPot -> {
                    blockManager.addHook(() -> {
                        DECORATED_POT.create(decoratedPot.getOrCreateBlockEntity(), createRandom(level, block.asBlockVector3()));
                    });
                }
                case BlockCopperBulb copperBulb -> {
                    blockManager.addHook(() -> {
                        level.updateBlockSkyLight(copperBulb.getFloorX(), copperBulb.getFloorY(), copperBulb.getFloorZ());
                    });
                }
                case BlockTrialSpawner trialSpawner -> {
                    String entityId = getTrialSpawnerEntityId(structureName);
                    if (entityId != null) {
                        blockManager.addHook(() -> {
                            BlockEntityTrialSpawner blockEntity = trialSpawner.getOrCreateBlockEntity();
                            blockEntity.setSpawnEntityType(entityId);
                            blockEntity.applyTrialChamberDefaults(structureName);
                        });
                    }
                }
                default -> {}
            };
        }
    }

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        helper.applySubChunkUpdate();
    }

    protected RandomSourceProvider createRandom(Level level, BlockVector3 pos) {
        long seed = level.getSeed();
        seed ^= 0x9E3779B97F4A7C15L * pos.getX();
        seed ^= 0xC2B2AE3D27D4EB4FL * pos.getY();
        seed ^= 0x165667B19E3779F9L * pos.getZ();
        return new Xoroshiro128(seed);
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put(START, pool(
                START,
                entry("trial_chambers/corridor/end_1", 1),
                entry("trial_chambers/corridor/end_2", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/entrance_cap", pool(
                "trial_chambers/chamber/entrance_cap",
                entry("trial_chambers/chamber/entrance_cap", 1)
        ));

        COLLECTION.put("trial_chambers/chambers/end", fallbackPool(
                "trial_chambers/chambers/end",
                HALLWAY_FALLBACK,
                entry("trial_chambers/chamber/chamber_1", 1),
                entry("trial_chambers/chamber/assembly", 1),
                entry("trial_chambers/chamber/eruption", 1),
                entry("trial_chambers/chamber/slanted", 1)
        ));

        COLLECTION.put("trial_chambers/corridor", pool(
                "trial_chambers/corridor",
                entry("trial_chambers/corridor/second_plate", 1),
                entry("trial_chambers/intersection/intersection_1", 1),
                entry("trial_chambers/intersection/intersection_2", 1),
                entry("trial_chambers/intersection/intersection_3", 1),
                entry("trial_chambers/corridor/first_plate", 1),
                entry("trial_chambers/corridor/atrium_1", 1),
                entry("trial_chambers/corridor/entrance_1", 1),
                entry("trial_chambers/corridor/entrance_2", 1),
                entry("trial_chambers/corridor/entrance_3", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/addon", pool(
                "trial_chambers/chamber/addon",
                entry("trial_chambers/chamber/addon/full_stacked_walkway", 1),
                entry("trial_chambers/chamber/addon/full_stacked_walkway_2", 1),
                entry("trial_chambers/chamber/addon/full_corner_column", 1),
                entry("trial_chambers/chamber/addon/grate_bridge", 1),
                entry("trial_chambers/chamber/addon/hanging_platform", 1),
                entry("trial_chambers/chamber/addon/short_grate_platform", 1),
                entry("trial_chambers/chamber/addon/short_platform", 1),
                entry("trial_chambers/chamber/addon/lower_staircase_down", 1),
                entry("trial_chambers/chamber/addon/walkway_with_bridge_1", 1),
                entry("trial_chambers/chamber/addon/c1_breeze", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/assembly", pool(
                "trial_chambers/chamber/assembly",
                entry("trial_chambers/chamber/assembly/full_column", 2),
                entry("trial_chambers/chamber/assembly/cover_1", 2),
                entry("trial_chambers/chamber/assembly/cover_2", 2),
                entry("trial_chambers/chamber/assembly/cover_3", 2),
                entry("trial_chambers/chamber/assembly/cover_4", 2),
                entry("trial_chambers/chamber/assembly/cover_5", 2),
                entry("trial_chambers/chamber/assembly/cover_6", 2),
                entry("trial_chambers/chamber/assembly/cover_7", 5),
                entry("trial_chambers/chamber/assembly/platform_1", 2),
                entry("trial_chambers/chamber/assembly/spawner_1", 1),
                entry("trial_chambers/chamber/assembly/hanging_1", 2),
                entry("trial_chambers/chamber/assembly/hanging_2", 1),
                entry("trial_chambers/chamber/assembly/hanging_3", 2),
                entry("trial_chambers/chamber/assembly/hanging_4", 2),
                entry("trial_chambers/chamber/assembly/hanging_5", 4),
                entry("trial_chambers/chamber/assembly/left_staircase_1", 1),
                entry("trial_chambers/chamber/assembly/left_staircase_2", 1),
                entry("trial_chambers/chamber/assembly/left_staircase_3", 1),
                entry("trial_chambers/chamber/assembly/right_staircase_1", 1),
                entry("trial_chambers/chamber/assembly/right_staircase_2", 1),
                entry("trial_chambers/chamber/assembly/right_staircase_3", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/eruption", pool(
                "trial_chambers/chamber/eruption",
                entry("trial_chambers/chamber/eruption/center_1", 1),
                entry("trial_chambers/chamber/eruption/breeze_slice_1", 1),
                entry("trial_chambers/chamber/eruption/slice_1", 1),
                entry("trial_chambers/chamber/eruption/slice_2", 1),
                entry("trial_chambers/chamber/eruption/slice_3", 1),
                entry("trial_chambers/chamber/eruption/quadrant_1", 1),
                entry("trial_chambers/chamber/eruption/quadrant_2", 1),
                entry("trial_chambers/chamber/eruption/quadrant_3", 1),
                entry("trial_chambers/chamber/eruption/quadrant_4", 1),
                entry("trial_chambers/chamber/eruption/quadrant_5", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/slanted", pool(
                "trial_chambers/chamber/slanted",
                entry("trial_chambers/chamber/slanted/center", 1),
                entry("trial_chambers/chamber/slanted/hallway_1", 1),
                entry("trial_chambers/chamber/slanted/hallway_2", 1),
                entry("trial_chambers/chamber/slanted/hallway_3", 1),
                entry("trial_chambers/chamber/slanted/quadrant_1", 1),
                entry("trial_chambers/chamber/slanted/quadrant_2", 1),
                entry("trial_chambers/chamber/slanted/quadrant_3", 1),
                entry("trial_chambers/chamber/slanted/quadrant_4", 1),
                entry("trial_chambers/chamber/slanted/ramp_1", 1),
                entry("trial_chambers/chamber/slanted/ramp_2", 1),
                entry("trial_chambers/chamber/slanted/ramp_3", 1),
                entry("trial_chambers/chamber/slanted/ramp_4", 1),
                entry("trial_chambers/chamber/slanted/ominous_upper_arm_1", 1)
        ));

        COLLECTION.put("trial_chambers/chamber/pedestal", pool(
                "trial_chambers/chamber/pedestal",
                entry("trial_chambers/chamber/pedestal/center_1", 1),
                entry("trial_chambers/chamber/pedestal/slice_1", 1),
                entry("trial_chambers/chamber/pedestal/slice_2", 3),
                entry("trial_chambers/chamber/pedestal/slice_3", 3),
                entry("trial_chambers/chamber/pedestal/slice_4", 3),
                entry("trial_chambers/chamber/pedestal/slice_5", 3),
                entry("trial_chambers/chamber/pedestal/ominous_slice_1", 1),
                entry("trial_chambers/chamber/pedestal/quadrant_1", 1),
                entry("trial_chambers/chamber/pedestal/quadrant_2", 1),
                entry("trial_chambers/chamber/pedestal/quadrant_3", 1),
                entry("trial_chambers/chamber/slanted/quadrant_1", 1),
                entry("trial_chambers/chamber/slanted/quadrant_2", 1),
                entry("trial_chambers/chamber/slanted/quadrant_3", 1),
                entry("trial_chambers/chamber/slanted/quadrant_4", 1)
        ));

        COLLECTION.put("trial_chambers/corridor/slices", pool(
                "trial_chambers/corridor/slices",
                entry("trial_chambers/corridor/straight_1", 1),
                entry("trial_chambers/corridor/straight_2", 2),
                entry("trial_chambers/corridor/straight_3", 2),
                entry("trial_chambers/corridor/straight_4", 2),
                entry("trial_chambers/corridor/straight_5", 2),
                entry("trial_chambers/corridor/straight_6", 2),
                entry("trial_chambers/corridor/straight_7", 1),
                entry("trial_chambers/corridor/straight_8", 2)
        ));

        COLLECTION.put(HALLWAY_FALLBACK, pool(
                HALLWAY_FALLBACK,
                entry("trial_chambers/hallway/rubble", 1),
                entry("trial_chambers/hallway/rubble_chamber", 1),
                entry("trial_chambers/hallway/rubble_thin", 1),
                entry("trial_chambers/hallway/rubble_chamber_thin", 1)
        ));

        COLLECTION.put("trial_chambers/hallway", fallbackPool(
                "trial_chambers/hallway",
                HALLWAY_FALLBACK,
                entry("trial_chambers/hallway/corridor_connector_1", 1),
                entry("trial_chambers/hallway/upper_hallway_connector", 1),
                entry("trial_chambers/hallway/lower_hallway_connector", 1),
                entry("trial_chambers/hallway/rubble", 1),
                entry("trial_chambers/chamber/chamber_1", 150),
                entry("trial_chambers/chamber/chamber_2", 150),
                entry("trial_chambers/chamber/chamber_4", 150),
                entry("trial_chambers/chamber/chamber_8", 150),
                entry("trial_chambers/chamber/assembly", 150),
                entry("trial_chambers/chamber/eruption", 150),
                entry("trial_chambers/chamber/slanted", 150),
                entry("trial_chambers/chamber/pedestal", 150),
                entry("trial_chambers/hallway/rubble_chamber", 10),
                entry("trial_chambers/hallway/rubble_chamber_thin", 1),
                entry("trial_chambers/hallway/cache_1", 1),
                entry("trial_chambers/hallway/left_corner", 1),
                entry("trial_chambers/hallway/right_corner", 1),
                entry("trial_chambers/hallway/corner_staircase", 1),
                entry("trial_chambers/hallway/corner_staircase_down", 1),
                entry("trial_chambers/hallway/long_straight_staircase", 1),
                entry("trial_chambers/hallway/long_straight_staircase_down", 1),
                entry("trial_chambers/hallway/straight", 1),
                entry("trial_chambers/hallway/straight_staircase", 1),
                entry("trial_chambers/hallway/straight_staircase_down", 1),
                entry("trial_chambers/hallway/trapped_staircase", 1),
                entry("trial_chambers/hallway/encounter_1", 1),
                entry("trial_chambers/hallway/encounter_2", 1),
                entry("trial_chambers/hallway/encounter_3", 1),
                entry("trial_chambers/hallway/encounter_4", 1),
                entry("trial_chambers/hallway/encounter_5", 1)
        ));

        COLLECTION.put("trial_chambers/corridors/addon/lower", pool(
                "trial_chambers/corridors/addon/lower",
                entry("trial_chambers/corridor/addon/staircase", 1),
                entry("trial_chambers/corridor/addon/wall", 1),
                entry("trial_chambers/corridor/addon/ladder_to_middle", 1),
                entry("trial_chambers/corridor/addon/arrow_dispenser", 1),
                entry("trial_chambers/corridor/addon/bridge_lower", 2)
        ));

        COLLECTION.put("trial_chambers/corridors/addon/middle", pool(
                "trial_chambers/corridors/addon/middle",
                entry("trial_chambers/corridor/addon/open_walkway", 2),
                entry("trial_chambers/corridor/addon/walled_walkway", 1)
        ));

        COLLECTION.put("trial_chambers/corridors/addon/middle_upper", pool(
                "trial_chambers/corridors/addon/middle_upper",
                entry("trial_chambers/corridor/addon/open_walkway_upper", 2),
                entry("trial_chambers/corridor/addon/chandelier_upper", 1),
                entry("trial_chambers/corridor/addon/decoration_upper", 1),
                entry("trial_chambers/corridor/addon/head_upper", 1),
                entry("trial_chambers/corridor/addon/reward_upper", 1)
        ));

        COLLECTION.put("trial_chambers/atrium", pool(
                "trial_chambers/atrium",
                entry("trial_chambers/corridor/atrium/bogged_relief", 1),
                entry("trial_chambers/corridor/atrium/breeze_relief", 1),
                entry("trial_chambers/corridor/atrium/spiral_relief", 1),
                entry("trial_chambers/corridor/atrium/spider_relief", 1),
                entry("trial_chambers/corridor/atrium/grand_staircase_1", 1),
                entry("trial_chambers/corridor/atrium/grand_staircase_2", 1),
                entry("trial_chambers/corridor/atrium/grand_staircase_3", 1)
        ));

        COLLECTION.put("trial_chambers/decor", pool(
                "trial_chambers/decor",
                entry("trial_chambers/decor/empty_pot", 2),
                entry("trial_chambers/decor/dead_bush_pot", 2),
                entry("trial_chambers/decor/undecorated_pot", 10),
                entry("trial_chambers/decor/flow_pot", 1),
                entry("trial_chambers/decor/guster_pot", 1),
                entry("trial_chambers/decor/scrape_pot", 1),
                entry("trial_chambers/decor/candle_1", 1),
                entry("trial_chambers/decor/candle_2", 1),
                entry("trial_chambers/decor/candle_3", 1),
                entry("trial_chambers/decor/candle_4", 1),
                entry("trial_chambers/decor/barrel", 2)
        ));

        COLLECTION.put("trial_chambers/decor/disposal", pool(
                "trial_chambers/decor/disposal",
                entry("trial_chambers/decor/disposal", 1)
        ));

        COLLECTION.put("trial_chambers/decor/bed", pool(
                "trial_chambers/decor/bed",
                entry("trial_chambers/decor/white_bed", 3),
                entry("trial_chambers/decor/light_gray_bed", 3),
                entry("trial_chambers/decor/gray_bed", 3),
                entry("trial_chambers/decor/black_bed", 3),
                entry("trial_chambers/decor/brown_bed", 3),
                entry("trial_chambers/decor/red_bed", 3),
                entry("trial_chambers/decor/orange_bed", 3),
                entry("trial_chambers/decor/yellow_bed", 3),
                entry("trial_chambers/decor/lime_bed", 3),
                entry("trial_chambers/decor/green_bed", 3),
                entry("trial_chambers/decor/cyan_bed", 3),
                entry("trial_chambers/decor/light_blue_bed", 3),
                entry("trial_chambers/decor/blue_bed", 3),
                entry("trial_chambers/decor/purple_bed", 3),
                entry("trial_chambers/decor/magenta_bed", 3),
                entry("trial_chambers/decor/pink_bed", 1)
        ));

        COLLECTION.put("trial_chambers/entrance", pool(
                "trial_chambers/entrance",
                entry("trial_chambers/corridor/addon/display_1", 1),
                entry("trial_chambers/corridor/addon/display_2", 1),
                entry("trial_chambers/corridor/addon/display_3", 1)
        ));

        COLLECTION.put("trial_chambers/decor/chamber", pool(
                "trial_chambers/decor/chamber",
                entry("trial_chambers/decor/undecorated_pot", 1)
        ));

        COLLECTION.put("trial_chambers/reward/all", pool(
                "trial_chambers/reward/all",
                entry("trial_chambers/reward/vault", 1)
        ));

        COLLECTION.put("trial_chambers/reward/ominous_vault", pool(
                "trial_chambers/reward/ominous_vault",
                entry("trial_chambers/reward/ominous_vault", 1)
        ));

        COLLECTION.put("trial_chambers/reward/contents/default", pool(
                "trial_chambers/reward/contents/default",
                entry("trial_chambers/reward/vault", 1)
        ));

        COLLECTION.put("trial_chambers/chests/supply", pool(
                "trial_chambers/chests/supply",
                entry("trial_chambers/chests/connectors/supply", 1)
        ));

        COLLECTION.put("trial_chambers/chests/contents/supply", pool(
                "trial_chambers/chests/contents/supply",
                entry("trial_chambers/chests/supply", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/ranged", pool(
                "trial_chambers/spawner/ranged",
                entry("trial_chambers/spawner/connectors/ranged", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/slow_ranged", pool(
                "trial_chambers/spawner/slow_ranged",
                entry("trial_chambers/spawner/connectors/slow_ranged", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/melee", pool(
                "trial_chambers/spawner/melee",
                entry("trial_chambers/spawner/connectors/melee", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/small_melee", pool(
                "trial_chambers/spawner/small_melee",
                entry("trial_chambers/spawner/connectors/small_melee", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/breeze", pool(
                "trial_chambers/spawner/breeze",
                entry("trial_chambers/spawner/connectors/breeze", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/all", pool(
                "trial_chambers/spawner/all",
                entry("trial_chambers/spawner/connectors/ranged", 1),
                entry("trial_chambers/spawner/connectors/melee", 1),
                entry("trial_chambers/spawner/connectors/small_melee", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/contents/breeze", pool(
                "trial_chambers/spawner/contents/breeze",
                entry("trial_chambers/spawner/breeze/breeze", 1)
        ));

        COLLECTION.put("trial_chambers/dispensers/chamber", pool(
                "trial_chambers/dispensers/chamber",
                entry("trial_chambers/dispensers/chamber", 1),
                entry("trial_chambers/dispensers/wall_dispenser", 1),
                entry("trial_chambers/dispensers/floor_dispenser", 1)
        ));

        // Approximation of Mojang's pool alias bindings.
        COLLECTION.put("trial_chambers/spawner/contents/ranged", pool(
                "trial_chambers/spawner/contents/ranged",
                entry("trial_chambers/spawner/ranged/skeleton", 1),
                entry("trial_chambers/spawner/ranged/stray", 1),
                entry("trial_chambers/spawner/ranged/poison_skeleton", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/contents/slow_ranged", pool(
                "trial_chambers/spawner/contents/slow_ranged",
                entry("trial_chambers/spawner/slow_ranged/skeleton", 1),
                entry("trial_chambers/spawner/slow_ranged/stray", 1),
                entry("trial_chambers/spawner/slow_ranged/poison_skeleton", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/contents/melee", pool(
                "trial_chambers/spawner/contents/melee",
                entry("trial_chambers/spawner/melee/zombie", 1),
                entry("trial_chambers/spawner/melee/husk", 1),
                entry("trial_chambers/spawner/melee/spider", 1)
        ));

        COLLECTION.put("trial_chambers/spawner/contents/small_melee", pool(
                "trial_chambers/spawner/contents/small_melee",
                entry("trial_chambers/spawner/small_melee/slime", 1),
                entry("trial_chambers/spawner/small_melee/cave_spider", 1),
                entry("trial_chambers/spawner/small_melee/silverfish", 1),
                entry("trial_chambers/spawner/small_melee/baby_zombie", 1)
        ));

        putSingleStructurePool("trial_chambers/spawner/ranged/skeleton");
        putSingleStructurePool("trial_chambers/spawner/ranged/stray");
        putSingleStructurePool("trial_chambers/spawner/ranged/poison_skeleton");
        putSingleStructurePool("trial_chambers/spawner/slow_ranged/skeleton");
        putSingleStructurePool("trial_chambers/spawner/slow_ranged/stray");
        putSingleStructurePool("trial_chambers/spawner/slow_ranged/poison_skeleton");
        putSingleStructurePool("trial_chambers/spawner/melee/zombie");
        putSingleStructurePool("trial_chambers/spawner/melee/husk");
        putSingleStructurePool("trial_chambers/spawner/melee/spider");
        putSingleStructurePool("trial_chambers/spawner/small_melee/slime");
        putSingleStructurePool("trial_chambers/spawner/small_melee/cave_spider");
        putSingleStructurePool("trial_chambers/spawner/small_melee/silverfish");
        putSingleStructurePool("trial_chambers/spawner/small_melee/baby_zombie");
    }

    private static void putSingleStructurePool(String name) {
        COLLECTION.put(name, pool(name, entry(name, 1)));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool fallbackPool(String name, String fallback, StructurePool.Entry... entries) {
        return new StructurePool(name, fallback, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }

    @Override
    public String getName() {
        return "trial_chamber";
    }

    protected static final int MIN_DISTANCE = 12;
    protected static final int MAX_DISTANCE = 34;

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        int chunkX = location.getChunkX();
        int chunkZ = location.getChunkZ();
        Level level = location.getLevel();
        RandomSourceProvider random = new Xoroshiro128(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));

        if (!((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ)) {
            return false;
        }
        return true;
    }

    protected static class CorridorChestPopulator extends RandomizableContainer {
        public CorridorChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_AXE, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.HONEYCOMB, 0, 2, 8, 1))
                    .register(new ItemEntry(Item.STONE_AXE, 2))
                    .register(new ItemEntry(Item.STONE_PICKAXE, 2))
                    .register(new ItemEntry(Item.ENDER_PEARL, 0, 1, 2, 2))
                    .register(new ItemEntry(Block.BAMBOO_HANGING_SIGN, 0, 1, 4, 2))
                    .register(new ItemEntry(Block.BAMBOO_PLANKS, 0, 3, 6, 2))
                    .register(new ItemEntry(Block.SCAFFOLDING, 0, 2, 10, 2))
                    .register(new ItemEntry(Block.TORCH, 0, 3, 6, 2))
                    .register(new ItemEntry(Block.TUFF, 0, 8, 20, 3));

            this.pools.put(pool1.build(), new RollEntry(1, 3, pool1.getTotalWeight()));
        }
    }

    protected static class EntranceChestPopulator extends RandomizableContainer {
        public EntranceChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.TRIAL_KEY, 1))
                    .register(new ItemEntry(Item.STICK, 0, 2, 5, 5))
                    .register(new ItemEntry(Item.WOODEN_AXE, 10))
                    .register(new ItemEntry(Item.HONEYCOMB, 0, 2, 8, 10))
                    .register(new ItemEntry(Item.ARROW, 0, 5, 10, 10));

            this.pools.put(pool1.build(), new RollEntry(2, 3, pool1.getTotalWeight()));
        }
    }

    protected static class IntersectionChestPopulator extends RandomizableContainer {
        public IntersectionChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Block.DIAMOND_BLOCK, 1))
                    .register(new ItemEntry(Block.EMERALD_BLOCK, 0, 1, 3, 5))
                    .register(new ItemEntry(Item.DIAMOND_AXE, 5))
                    .register(new ItemEntry(Item.DIAMOND_PICKAXE, 5))
                    .register(new ItemEntry(Item.DIAMOND, 0, 1, 2, 10))
                    .register(new ItemEntry(Block.CAKE, 0, 1, 4, 20))
                    .register(new ItemEntry(Item.AMETHYST_SHARD, 0, 8, 20, 20))
                    .register(new ItemEntry(Block.IRON_BLOCK, 0, 1, 2, 20));

            this.pools.put(pool1.build(), new RollEntry(1, 3, pool1.getTotalWeight()));
        }
    }

    protected static class IntersectionBarrelPopulator extends RandomizableContainer {
        public IntersectionBarrelPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND_AXE, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_PICKAXE, 1))
                    .register(new ItemEntry(Item.DIAMOND, 0, 1, 3, 1))
                    .register(new ItemEntry(Item.COMPASS, 1))
                    .register(new ItemEntry(Item.BUCKET, 0, 1, 2, 1))
                    .register(new ItemEntry(Item.GOLDEN_AXE, 4))
                    .register(new ItemEntry(Item.GOLDEN_PICKAXE, 4))
                    .register(new ItemEntry(Block.BAMBOO_PLANKS, 0, 5, 15, 10))
                    .register(new ItemEntry(Item.BAKED_POTATO, 0, 6, 10, 10));

            this.pools.put(pool1.build(), new RollEntry(1, 3, pool1.getTotalWeight()));
        }
    }

    protected static class SupplyChestPopulator extends RandomizableContainer {
        public SupplyChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.ARROW, 0, 4, 14, 2))
                    .register(new ItemEntry(Item.ARROW, 0, 4, 8, 1))
                    .register(new ItemEntry(Item.ARROW, 0, 4, 8, 1))
                    .register(new ItemEntry(Item.BAKED_POTATO, 0, 2, 4, 2))
                    .register(new ItemEntry(Item.GLOW_BERRIES, 0, 2, 10, 2))
                    .register(new ItemEntry(Block.ACACIA_PLANKS, 0, 3, 6, 1))
                    .register(new ItemEntry(Block.MOSS_BLOCK, 0, 2, 5, 1))
                    .register(new ItemEntry(Item.BONE_MEAL, 0, 2, 5, 1))
                    .register(new ItemEntry(Block.TUFF, 0, 5, 10, 1))
                    .register(new ItemEntry(Block.TORCH, 0, 3, 6, 1))
                    .register(new ItemEntry(Item.POTION, 0, 2, 2, 1))
                    .register(new ItemEntry(Item.POTION, 0, 2, 2, 1))
                    .register(new ItemEntry(Item.STONE_PICKAXE, 2))
                    .register(new ItemEntry(Item.MILK_BUCKET, 1));

            this.pools.put(pool1.build(), new RollEntry(3, 5, pool1.getTotalWeight()));
        }
    }

    protected static class CorridorDispenserPopulator extends RandomizableContainer {
        public CorridorDispenserPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.ARROW, 0, 4, 8, 1));

            this.pools.put(pool1.build(), new RollEntry(1, 1, pool1.getTotalWeight()));
        }
    }

    protected static class WaterDispenserPopulator extends RandomizableContainer {
        public WaterDispenserPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.WATER_BUCKET, 1));

            this.pools.put(pool1.build(), new RollEntry(1, 1, pool1.getTotalWeight()));
        }
    }

    protected static class ChamberDispenserPopulator extends RandomizableContainer {
        public ChamberDispenserPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.WATER_BUCKET, 4))
                    .register(new ItemEntry(Item.ARROW, 0, 4, 8, 4))
                    .register(new ItemEntry(Item.SNOWBALL, 0, 4, 8, 6))
                    .register(new ItemEntry(Item.EGG, 0, 4, 8, 2))
                    .register(new ItemEntry(Item.FIRE_CHARGE, 0, 4, 8, 6))
                    .register(new ItemEntry(Item.SPLASH_POTION, PotionType.SLOWNESS.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.SPLASH_POTION, PotionType.POISON.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.SPLASH_POTION, PotionType.WEAKNESS.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.LINGERING_POTION, PotionType.SLOWNESS.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.LINGERING_POTION, PotionType.POISON.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.LINGERING_POTION, PotionType.WEAKNESS.id(), 2, 5, 1))
                    .register(new ItemEntry(Item.LINGERING_POTION, PotionType.HEALING.id(), 2, 5, 1));

            this.pools.put(pool1.build(), new RollEntry(1, 1, pool1.getTotalWeight()));
        }
    }

    protected static class DecoratedPotPopulator extends RandomizableContainer {
        public DecoratedPotPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.EMERALD, 0, 1, 3, 125))
                    .register(new ItemEntry(Item.ARROW, 0, 2, 8, 100))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 1, 2, 100))
                    .register(new ItemEntry(Item.TRIAL_KEY, 10))
                    .register(new ItemEntry(Item.MUSIC_DISC_CREATOR_MUSIC_BOX, 5))
                    .register(new ItemEntry(Item.DIAMOND, 0, 1, 2, 5))
                    .register(new ItemEntry(Block.EMERALD_BLOCK, 5))
                    .register(new ItemEntry(Block.DIAMOND_BLOCK, 1));

            this.pools.put(pool1.build(), new RollEntry(1, 1, pool1.getTotalWeight()));
        }

        public void create(BlockEntityDecoratedPot blockEntity, RandomSourceProvider random) {
            try {
                this.pools.forEach((pool, roll) -> {
                    for (int i = roll.getMin() == -1 ? roll.getMax() : NukkitMath.randomRange(random, roll.getMin(), roll.getMax()); i > 0; --i) {
                        int result = random.nextBoundedInt(roll.getTotalWeight());
                        for (ItemEntry entry : pool) {
                            result -= entry.getWeight();
                            if (result < 0) {
                                Item item = Item.get(entry.getId(), entry.getMeta(), NukkitMath.randomRange(random, entry.getMinCount(), entry.getMaxCount()));
                                applyRandomEnchantment(item, entry.getEnchantments(), random);
                                blockEntity.setItem(item);
                                blockEntity.namedTag.putList("sherds", createSherds(random));
                                blockEntity.spawnToAll();
                                return;
                            }
                        }
                    }
                });
            } catch (Exception ignored) {}
        }

        private ListTag<StringTag> createSherds(RandomSourceProvider random) {
            ListTag<StringTag> sherds = new ListTag<>();
            for (int i = 0; i < 4; i++) {
                sherds.add(new StringTag(ItemID.BRICK));
            }

            int roll = random.nextBoundedInt(13);
            if (roll < 10) {
                return sherds;
            }

            String sherdId = switch (roll) {
                case 10 -> ItemID.FLOW_POTTERY_SHERD;
                case 11 -> ItemID.GUSTER_POTTERY_SHERD;
                default -> ItemID.SCRAPE_POTTERY_SHERD;
            };
            sherds.add(random.nextBoundedInt(4), new StringTag(sherdId));
            return sherds;
        }
    }
}
