package cn.nukkit.level.generator.object.structures.jigsaw.trialchambers;

import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;

public class TrialChambersStructure extends JigsawStructure {

    private static final String START = "trial_chambers/chamber/end";
    private static final String HALLWAY_FALLBACK = "trial_chambers/hallway/fallback";
    private static final StructurePoolCollection COLLECTION;

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

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        helper.applySubChunkUpdate();
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
}
