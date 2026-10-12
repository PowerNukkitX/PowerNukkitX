package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.placement.AggregatePlacementFeature;
import org.powernukkitx.level.generator.feature.placement.ConditionalPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.GeneratorPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.SequencePlacementFeature;
import org.powernukkitx.level.generator.feature.placement.vegetation.LeafLitterPlacementFeature;
import org.powernukkitx.level.generator.object.BeeNestGenerator;
import org.powernukkitx.level.generator.object.ObjectJungleBush;
import org.powernukkitx.level.generator.object.ObjectRoofedTreeWorldgen;
import org.powernukkitx.level.generator.object.ObjectMegaConiferTree;
import org.powernukkitx.level.generator.object.ObjectMegaJungleTree;
import org.powernukkitx.level.generator.object.ObjectPineTree;
import org.powernukkitx.level.generator.object.ObjectSavannaTreeWorldgen;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;

import java.util.List;

/**
 * Shared configured placement graphs for vanilla tree families.
 *
 * @author Curse
 */
public final class TreePlacementFeatures {
    private static final String FOREST_GENERATION = "forest_generation";

    private static final PlacementFeature PLAIN_OAK = ClassicTreePlacementFeature.oak(false);
    private static final PlacementFeature OAK_WITH_VINES = ClassicTreePlacementFeature.oak(true);
    private static final PlacementFeature FALLEN_OAK = FallenTreePlacementFeature.oak();

    private static final PlacementFeature PLAIN_BIRCH = ClassicTreePlacementFeature.birch();
    private static final PlacementFeature FALLEN_BIRCH = FallenTreePlacementFeature.birch();
    private static final PlacementFeature SUPER_BIRCH_TREE = ClassicTreePlacementFeature.superBirch();
    private static final PlacementFeature FALLEN_SUPER_BIRCH = FallenTreePlacementFeature.superBirch();

    private static final PlacementFeature PLAIN_SPRUCE = ClassicTreePlacementFeature.spruce(false);
    private static final PlacementFeature SPRUCE_WITH_VINES = ClassicTreePlacementFeature.spruce(true);
    private static final PlacementFeature FALLEN_SPRUCE = FallenTreePlacementFeature.spruce();
    private static final PlacementFeature PINE = new GeneratorPlacementFeature(
            context -> new ObjectPineTree()
    );
    private static final PlacementFeature MEGA_SPRUCE = new GeneratorPlacementFeature(
            context -> new ObjectMegaConiferTree(13, 18)
    );
    private static final PlacementFeature MEGA_PINE = new GeneratorPlacementFeature(
            context -> new ObjectMegaConiferTree(3, 8)
    );

    private static final PlacementFeature FALLEN_JUNGLE = FallenTreePlacementFeature.jungle();
    private static final PlacementFeature JUNGLE_TREE_WITH_COCOA = new SequencePlacementFeature(
            List.of(
                    new JungleTreePlacementFeature(),
                    new JungleCocoaPlacementFeature()
            )
    );
    private static final PlacementFeature JUNGLE_BUSH = new GeneratorPlacementFeature(
            context -> new ObjectJungleBush()
    );
    private static final PlacementFeature MEGA_JUNGLE = new GeneratorPlacementFeature(
            context -> new ObjectMegaJungleTree()
    );

    private static final PlacementFeature SAVANNA = new GeneratorPlacementFeature(
            context -> new ObjectSavannaTreeWorldgen()
    );

    private static final PlacementFeature FANCY_OAK = new FancyOakPlacementFeature();

    private static final PlacementFeature PLAIN_ROOFED = new GeneratorPlacementFeature(
            context -> new ObjectRoofedTreeWorldgen(false)
    );
    private static final PlacementFeature ROOFED_WITH_VINES = new GeneratorPlacementFeature(
            context -> new ObjectRoofedTreeWorldgen(true)
    );

    private static final PlacementFeature STANDING_OAK = new AggregatePlacementFeature(
            List.of(
                    optional(12, OAK_WITH_VINES),
                    PLAIN_OAK
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature OAK = new AggregatePlacementFeature(
            List.of(
                    optional(80, FALLEN_OAK),
                    STANDING_OAK
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature BIRCH = new AggregatePlacementFeature(
            List.of(
                    optional(80, FALLEN_BIRCH),
                    PLAIN_BIRCH
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature STANDING_SPRUCE = new AggregatePlacementFeature(
            List.of(
                    optional(12, SPRUCE_WITH_VINES),
                    PLAIN_SPRUCE
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature SPRUCE = new AggregatePlacementFeature(
            List.of(
                    optional(80, FALLEN_SPRUCE),
                    STANDING_SPRUCE
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature JUNGLE = new AggregatePlacementFeature(
            List.of(
                    optional(80, FALLEN_JUNGLE),
                    JUNGLE_TREE_WITH_COCOA
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature ROOFED = new AggregatePlacementFeature(
            List.of(
                    optional(6, ROOFED_WITH_VINES),
                    PLAIN_ROOFED
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature OPTIONAL_BEEHIVE = context -> {
        float chance = beeNestChance(context);
        if (chance <= 0 || context.getRandom().nextFloat() >= chance) {
            return false;
        }

        return BeeNestGenerator.place(
                context.getRoot(),
                context.getRandom(),
                new Vector3(context.getX(), context.getY(), context.getZ())
        );
    };

    private static final PlacementFeature BEEHIVE = context -> BeeNestGenerator.place(
            context.getRoot(),
            context.getRandom(),
            new Vector3(context.getX(), context.getY(), context.getZ())
    );

    private static final PlacementFeature SUPER_BIRCH_WITH_OPTIONAL_BEEHIVE = new AggregatePlacementFeature(
            List.of(SUPER_BIRCH_TREE, OPTIONAL_BEEHIVE),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature SUPER_BIRCH = new AggregatePlacementFeature(
            List.of(
                    optional(80, FALLEN_SUPER_BIRCH),
                    SUPER_BIRCH_WITH_OPTIONAL_BEEHIVE
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_SUCCESS
    );

    private static final PlacementFeature OAK_WITH_OPTIONAL_BEEHIVE = new AggregatePlacementFeature(
            List.of(PLAIN_OAK, OPTIONAL_BEEHIVE),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature FANCY_OAK_WITH_OPTIONAL_BEEHIVE = new AggregatePlacementFeature(
            List.of(FANCY_OAK, OPTIONAL_BEEHIVE),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature FANCY_OAK_WITH_BEEHIVE = new AggregatePlacementFeature(
            List.of(FANCY_OAK, BEEHIVE),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature SUPER_BIRCH_WITH_BEEHIVE = new AggregatePlacementFeature(
            List.of(SUPER_BIRCH_TREE, BEEHIVE),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature OAK_WITH_LEAF_LITTER = withLeafLitter(OAK);
    private static final PlacementFeature BIRCH_WITH_LEAF_LITTER = withLeafLitter(BIRCH);
    private static final PlacementFeature FANCY_OAK_WITH_LEAF_LITTER_AND_OPTIONAL_BEEHIVE =
            withLeafLitter(FANCY_OAK_WITH_OPTIONAL_BEEHIVE);

    private TreePlacementFeatures() {
    }

    /**
     * Returns the configured oak selector.
     */
    public static PlacementFeature oak() {
        return OAK;
    }

    /**
     * Returns the configured birch selector.
     */
    public static PlacementFeature birch() {
        return BIRCH;
    }

    /**
     * Returns the configured super-birch selector.
     */
    public static PlacementFeature superBirch() {
        return SUPER_BIRCH;
    }

    /**
     * Returns the configured spruce selector.
     */
    public static PlacementFeature spruce() {
        return SPRUCE;
    }

    /**
     * Returns the configured pine feature.
     */
    public static PlacementFeature pine() {
        return PINE;
    }

    /**
     * Returns the configured mega-spruce feature.
     */
    public static PlacementFeature megaSpruce() {
        return MEGA_SPRUCE;
    }

    /**
     * Returns the configured mega-pine feature.
     */
    public static PlacementFeature megaPine() {
        return MEGA_PINE;
    }

    /**
     * Returns the configured savanna feature.
     */
    public static PlacementFeature savanna() {
        return SAVANNA;
    }

    /**
     * Returns the configured jungle selector.
     */
    public static PlacementFeature jungle() {
        return JUNGLE;
    }

    /**
     * Returns the configured jungle-bush feature.
     */
    public static PlacementFeature jungleBush() {
        return JUNGLE_BUSH;
    }

    /**
     * Returns the configured mega-jungle feature.
     */
    public static PlacementFeature megaJungle() {
        return MEGA_JUNGLE;
    }

    /**
     * Returns the configured fancy-oak feature.
     */
    public static PlacementFeature fancyOak() {
        return FANCY_OAK;
    }

    /**
     * Returns plain oak with an optional beehive.
     */
    public static PlacementFeature oakWithOptionalBeehive() {
        return OAK_WITH_OPTIONAL_BEEHIVE;
    }

    /**
     * Returns fancy oak with an optional beehive.
     */
    public static PlacementFeature fancyOakWithOptionalBeehive() {
        return FANCY_OAK_WITH_OPTIONAL_BEEHIVE;
    }

    /**
     * Returns fancy oak with a beehive.
     */
    public static PlacementFeature fancyOakWithBeehive() {
        return FANCY_OAK_WITH_BEEHIVE;
    }

    /**
     * Returns super birch with a beehive.
     */
    public static PlacementFeature superBirchWithBeehive() {
        return SUPER_BIRCH_WITH_BEEHIVE;
    }

    /**
     * Returns the configured roofed-tree selector.
     */
    public static PlacementFeature roofed() {
        return ROOFED;
    }

    /**
     * Returns the current oak selector with local leaf litter.
     */
    public static PlacementFeature oakWithLeafLitter() {
        return OAK_WITH_LEAF_LITTER;
    }

    /**
     * Returns the current birch selector with local leaf litter.
     */
    public static PlacementFeature birchWithLeafLitter() {
        return BIRCH_WITH_LEAF_LITTER;
    }

    /**
     * Returns fancy oak with optional beehive and local leaf litter.
     */
    public static PlacementFeature fancyOakWithLeafLitterAndOptionalBeehive() {
        return FANCY_OAK_WITH_LEAF_LITTER_AND_OPTIONAL_BEEHIVE;
    }

    static boolean isNormalForestCandidate(FeaturePlacementContext context) {
        return (hasTag(context, BiomeTags.FOREST) || hasTag(context, FOREST_GENERATION))
                && !hasTag(context, BiomeTags.BIRCH)
                && !hasTag(context, BiomeTags.ROOFED)
                && !hasTag(context, BiomeTags.EXTREME_HILLS)
                && !hasTag(context, BiomeTags.TAIGA);
    }

    static boolean isBirchForestCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.FOREST)
                && hasTag(context, BiomeTags.BIRCH)
                && !hasTag(context, BiomeTags.MUTATED);
    }

    static boolean isMutatedBirchForestCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.FOREST)
                && hasTag(context, BiomeTags.BIRCH)
                && hasTag(context, BiomeTags.MUTATED);
    }

    static boolean isExtremeHillsCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.EXTREME_HILLS);
    }

    static boolean isSavannaCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.SAVANNA) && !hasTag(context, BiomeTags.MUTATED);
    }

    static boolean isMutatedSavannaCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.SAVANNA) && hasTag(context, BiomeTags.MUTATED);
    }

    static boolean isTaigaCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.TAIGA) && !hasTag(context, BiomeTags.MEGA);
    }

    static boolean isMegaTaigaCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.TAIGA)
                && hasTag(context, BiomeTags.MEGA)
                && !hasTag(context, BiomeTags.MUTATED);
    }

    static boolean isMutatedRedwoodTaigaCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.TAIGA)
                && hasTag(context, BiomeTags.MEGA)
                && hasTag(context, BiomeTags.MUTATED)
                && !hasTag(context, BiomeTags.HILLS);
    }

    static boolean isBambooJungleCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.BAMBOO)
                && hasTag(context, BiomeTags.JUNGLE);
    }

    static boolean isJungleCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.JUNGLE)
                && !hasTag(context, BiomeTags.BAMBOO)
                && !hasTag(context, BiomeTags.EDGE);
    }

    static boolean isJungleEdgeCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.JUNGLE)
                && !hasTag(context, BiomeTags.BAMBOO)
                && hasTag(context, BiomeTags.EDGE);
    }

    static boolean isFlowerForestCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.FLOWER_FOREST);
    }

    static boolean isMeadowCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.MEADOW);
    }

    static boolean isIceCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.ICE);
    }

    static boolean isPlainsCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.PLAINS);
    }

    static boolean isMesaStoneCandidate(FeaturePlacementContext context) {
        return hasTag(context, BiomeTags.MESA) && hasTag(context, BiomeTags.STONE);
    }

    /**
     * Tests the current surface origin against the roofed-forest feature rule.
     */
    public static boolean isRoofedForestCandidate(FeaturePlacementContext context) {
        return hasTagAt(context, BiomeTags.ROOFED, context.getY())
                && hasTagAt(context, BiomeTags.FOREST, context.getY());
    }

    private static PlacementFeature optional(int denominator, PlacementFeature child) {
        return new ConditionalPlacementFeature(
                context -> context.getRandom().nextExclusiveInt(denominator) == 0,
                child
        );
    }

    private static PlacementFeature withLeafLitter(PlacementFeature tree) {
        return new AggregatePlacementFeature(
                List.of(tree, LeafLitterPlacementFeature.scatter()),
                AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
        );
    }

    private static float beeNestChance(FeaturePlacementContext context) {
        if (!hasTag(context, BiomeTags.BEE_HABITAT)) {
            return 0;
        }
        if (hasTag(context, BiomeTags.PLAINS) || hasTag(context, BiomeTags.SUNFLOWER_PLAINS)) {
            return 0.05f;
        }
        if (hasTag(context, BiomeTags.FLOWER_FOREST)) {
            return 0.03f;
        }
        if (hasTag(context, BiomeTags.BIRCH) || hasTag(context, BiomeTags.FOREST)) {
            return 0.00035f;
        }
        return 0;
    }

    private static boolean hasTag(FeaturePlacementContext context, String tag) {
        int y = Math.max(context.getGenerationContext().getLevel().getMinHeight(), context.getY() - 1);
        return hasTagAt(context, tag, y);
    }

    private static boolean hasTagAt(FeaturePlacementContext context, String tag, int y) {
        int biomeId = context.getGenerationContext().getLevel().getBiomeId(context.getX(), y, context.getZ());
        return Registries.BIOME.containsTag(tag, biomeId);
    }
}
