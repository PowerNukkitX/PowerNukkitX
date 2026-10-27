package org.powernukkitx.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.BlockID;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The ids below are the legacy ones that carry their variant in the item meta, which
 * {@code internalAdjust} resolves to a modern block. Each meta has to reach its own block: the
 * variant decides what the player ends up holding, not just how it is named.
 */
class LegacyMetaItemTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    /** One legacy id together with the block each of its metas stands for, meta 0 first. */
    private record LegacyVariants(String itemId, List<String> blockIds) {
        @Override
        public String toString() {
            return this.itemId;
        }
    }

    private static Stream<LegacyVariants> legacyVariants() {
        return Stream.of(
            new LegacyVariants(ItemID.WOOL, List.of(
                BlockID.WHITE_WOOL, BlockID.ORANGE_WOOL, BlockID.MAGENTA_WOOL, BlockID.LIGHT_BLUE_WOOL,
                BlockID.YELLOW_WOOL, BlockID.LIME_WOOL, BlockID.PINK_WOOL, BlockID.GRAY_WOOL,
                BlockID.LIGHT_GRAY_WOOL, BlockID.CYAN_WOOL, BlockID.PURPLE_WOOL, BlockID.BLUE_WOOL,
                BlockID.BROWN_WOOL, BlockID.GREEN_WOOL, BlockID.RED_WOOL, BlockID.BLACK_WOOL)),
            new LegacyVariants(ItemID.STAINED_GLASS, List.of(
                BlockID.WHITE_STAINED_GLASS, BlockID.ORANGE_STAINED_GLASS, BlockID.MAGENTA_STAINED_GLASS,
                BlockID.LIGHT_BLUE_STAINED_GLASS, BlockID.YELLOW_STAINED_GLASS, BlockID.LIME_STAINED_GLASS,
                BlockID.PINK_STAINED_GLASS, BlockID.GRAY_STAINED_GLASS, BlockID.LIGHT_GRAY_STAINED_GLASS,
                BlockID.CYAN_STAINED_GLASS, BlockID.PURPLE_STAINED_GLASS, BlockID.BLUE_STAINED_GLASS,
                BlockID.BROWN_STAINED_GLASS, BlockID.GREEN_STAINED_GLASS, BlockID.RED_STAINED_GLASS,
                BlockID.BLACK_STAINED_GLASS)),
            new LegacyVariants(ItemID.STAINED_GLASS_PANE, List.of(
                BlockID.WHITE_STAINED_GLASS_PANE, BlockID.ORANGE_STAINED_GLASS_PANE,
                BlockID.MAGENTA_STAINED_GLASS_PANE, BlockID.LIGHT_BLUE_STAINED_GLASS_PANE,
                BlockID.YELLOW_STAINED_GLASS_PANE, BlockID.LIME_STAINED_GLASS_PANE,
                BlockID.PINK_STAINED_GLASS_PANE, BlockID.GRAY_STAINED_GLASS_PANE,
                BlockID.LIGHT_GRAY_STAINED_GLASS_PANE, BlockID.CYAN_STAINED_GLASS_PANE,
                BlockID.PURPLE_STAINED_GLASS_PANE, BlockID.BLUE_STAINED_GLASS_PANE,
                BlockID.BROWN_STAINED_GLASS_PANE, BlockID.GREEN_STAINED_GLASS_PANE,
                BlockID.RED_STAINED_GLASS_PANE, BlockID.BLACK_STAINED_GLASS_PANE)),
            new LegacyVariants(ItemID.STAINED_HARDENED_CLAY, List.of(
                BlockID.WHITE_TERRACOTTA, BlockID.ORANGE_TERRACOTTA, BlockID.MAGENTA_TERRACOTTA,
                BlockID.LIGHT_BLUE_TERRACOTTA, BlockID.YELLOW_TERRACOTTA, BlockID.LIME_TERRACOTTA,
                BlockID.PINK_TERRACOTTA, BlockID.GRAY_TERRACOTTA, BlockID.LIGHT_GRAY_TERRACOTTA,
                BlockID.CYAN_TERRACOTTA, BlockID.PURPLE_TERRACOTTA, BlockID.BLUE_TERRACOTTA,
                BlockID.BROWN_TERRACOTTA, BlockID.GREEN_TERRACOTTA, BlockID.RED_TERRACOTTA,
                BlockID.BLACK_TERRACOTTA)),
            new LegacyVariants(ItemID.PLANKS, List.of(
                BlockID.OAK_PLANKS, BlockID.SPRUCE_PLANKS, BlockID.BIRCH_PLANKS,
                BlockID.JUNGLE_PLANKS, BlockID.ACACIA_PLANKS, BlockID.DARK_OAK_PLANKS)),
            new LegacyVariants(ItemID.LOG, List.of(
                BlockID.OAK_LOG, BlockID.SPRUCE_LOG, BlockID.BIRCH_LOG, BlockID.JUNGLE_LOG)),
            new LegacyVariants(ItemID.LOG2, List.of(
                BlockID.ACACIA_LOG, BlockID.DARK_OAK_LOG)));
    }

    @ParameterizedTest
    @MethodSource("legacyVariants")
    void eachMetaResolvesToItsOwnBlock(LegacyVariants variants) {
        for (int meta = 0; meta < variants.blockIds().size(); meta++) {
            Item item = Item.get(variants.itemId(), meta, 1);
            assertEquals(variants.blockIds().get(meta), item.getBlockId(),
                () -> variants.itemId() + " resolved the wrong block");
        }
    }

    /**
     * The mapping above would still pass with a single wrong entry, so the variants are also
     * checked to stay distinct: a switch falling through sends every meta to the last case.
     */
    @ParameterizedTest
    @MethodSource("legacyVariants")
    void metasDoNotCollapseOntoOneBlock(LegacyVariants variants) {
        List<String> resolved = new ArrayList<>();
        for (int meta = 0; meta < variants.blockIds().size(); meta++) {
            resolved.add(Item.get(variants.itemId(), meta, 1).getBlockId());
        }

        Set<String> distinct = new LinkedHashSet<>(resolved);
        assertEquals(variants.blockIds().size(), distinct.size(),
            () -> variants.itemId() + " collapsed its metas onto " + distinct);
    }

    @Test
    void theFirstAndLastVariantAreNotTheSameBlock() {
        assertNotEquals(Item.get(ItemID.WOOL, 15, 1).getBlockId(), Item.get(ItemID.WOOL, 0, 1).getBlockId());
        assertEquals(BlockID.WHITE_WOOL, Item.get(ItemID.WOOL, 0, 1).getBlockId());
        assertEquals(BlockID.OAK_PLANKS, Item.get(ItemID.PLANKS, 0, 1).getBlockId());
        assertEquals(BlockID.OAK_LOG, Item.get(ItemID.LOG, 0, 1).getBlockId());
    }
}
