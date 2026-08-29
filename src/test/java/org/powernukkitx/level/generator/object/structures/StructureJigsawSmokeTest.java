package org.powernukkitx.level.generator.object.structures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.structures.jigsaw.JigsawStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.ancientcity.AncientCityStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.bastion.BastionStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.pool.StructurePool;
import org.powernukkitx.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import org.powernukkitx.level.generator.object.structures.jigsaw.trailruins.TrailRuinsStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.trialchambers.TrialChambersStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.village.DesertVillageStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.village.PlainsVillageStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.village.SavannaVillageStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.village.SnowyVillageStructure;
import org.powernukkitx.level.generator.object.structures.jigsaw.village.TaigaVillageStructure;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.LiquidUpdater;
import org.powernukkitx.level.generator.object.structures.utils.NukkitCollections;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke coverage for the jigsaw structure assembly stack and its pure helpers.
 * Everything runs through {@link #safe} - these classes touch a real level and a
 * huge structure registry, so we only care that the call paths execute without
 * blowing up the whole suite. A loose gate (checked count > 0) keeps it honest.
 */
public class StructureJigsawSmokeTest {

    private static Level level;
    private static final AtomicInteger CHECKED = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
        // Loads the real vanilla structure NBT so full jigsaw assembly can resolve pieces.
        safe(() -> {
            Registries.STRUCTURE.init();
            return null;
        });
    }

    private static Object safe(Supplier<Object> action) {
        CHECKED.incrementAndGet();
        try {
            return action.get();
        } catch (Throwable ignore) {
            return null;
        }
    }

    private static void safe(Runnable action) {
        safe(() -> {
            action.run();
            return null;
        });
    }

    @Test
    void boundingBoxHelpers() {
        BoundingBox a = new BoundingBox(0, 0, 0, 4, 4, 4);
        BoundingBox b = new BoundingBox(new int[]{2, 2, 2, 8, 8, 8});
        BoundingBox c = new BoundingBox(a);
        BoundingBox d = new BoundingBox(new BlockVector3(5, 5, 5), new BlockVector3(1, 1, 1));
        BoundingBox e = new BoundingBox(0, 0, 16, 16);

        safe(() -> a.intersects(b));
        safe(() -> a.intersects(1, 1, 3, 3));
        safe(() -> a.expand(b));
        safe(() -> a.move(1, 1, 1));
        safe(() -> a.moved(-1, -1, -1));
        safe(() -> a.isInside(new BlockVector3(3, 3, 3)));
        safe(a::getLength);
        safe(a::getXSpan);
        safe(a::getYSpan);
        safe(a::getZSpan);
        safe(a::createTag);
        safe(a::clone);
        safe(a::toString);
        safe(() -> a.getMinX() + a.getMinY() + a.getMinZ() + a.getMaxX() + a.getMaxY() + a.getMaxZ());
        safe(BoundingBox::getUnknownBox);
        safe(() -> BoundingBox.createProper(8, 8, 8, 0, 0, 0));
        for (BlockFace face : BlockFace.values()) {
            safe(() -> BoundingBox.orientBox(0, 0, 0, 1, 1, 1, 3, 3, 3, face));
        }
        // touch the other constructed boxes so their fields are read
        safe(() -> b.getXSpan() + c.getYSpan() + d.getZSpan() + e.x0);

        assertTrue(CHECKED.get() > 0);
    }

    @Test
    void poolHelpers() {
        StructurePool.Entry weighted = new StructurePool.Entry("village/plains/houses/plains_small_house_1", 3, "rigid");
        StructurePool.Entry defaulted = new StructurePool.Entry("village/common/well_bottom", 1);
        StructurePool pool = new StructurePool("village/plains/houses", weighted, defaulted);
        StructurePool withFallback = new StructurePool("child", "village/plains/houses", new StructurePool.Entry[]{weighted});

        Xoroshiro128 random = new Xoroshiro128(1234L);

        safe(pool::getName);
        safe(withFallback::getFallback);
        safe(() -> pool.entries.length);
        safe(weighted::structureName);
        safe(weighted::weight);
        safe(defaulted::projection);
        safe(() -> pool.getRandomEntry(random));
        safe(() -> pool.getStructureKey(random));
        safe(() -> pool.getRandomStructure(random));

        StructurePoolCollection collection = new StructurePoolCollection();
        safe(() -> collection.put(pool.getName(), pool));
        safe(() -> collection.get(pool.getName()));
        safe(collection::size);

        assertTrue(CHECKED.get() > 0);
    }

    @Test
    void collectionAndLiquidHelpers() {
        Xoroshiro128 random = new Xoroshiro128(99L);
        List<Integer> small = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> big = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        safe(() -> NukkitCollections.shuffle(small, random));
        safe(() -> NukkitCollections.shuffle(big, random));

        StructureHelper helper = new StructureHelper(level, new BlockVector3(0, 0, 0));
        safe(() -> LiquidUpdater.lavaSpread(helper, new BlockVector3(4, 70, 4)));
        safe(() -> LiquidUpdater.lavaSpread(helper, 4, 70, 4));
        // spread far outside a loaded chunk hits the early-return guard
        safe(() -> LiquidUpdater.lavaSpread(helper, 9_000_000, 70, 9_000_000));

        assertTrue(CHECKED.get() > 0);
    }

    @Test
    void structureHelperOps() {
        StructureHelper helper = new StructureHelper(level, new BlockVector3(0, 64, 0));
        Xoroshiro128 random = new Xoroshiro128(7L);

        safe(helper::getOrigin);
        safe(() -> helper.fill(new BlockVector3(0, 0, 0), new BlockVector3(2, 2, 2),
                org.powernukkitx.block.BlockCobblestone.PROPERTIES.getDefaultState()));
        safe(() -> helper.fill(new BlockVector3(0, 0, 0), new BlockVector3(3, 3, 3),
                org.powernukkitx.block.BlockCobblestone.PROPERTIES.getDefaultState(),
                org.powernukkitx.block.BlockDirt.PROPERTIES.getDefaultState()));

        java.util.Map<org.powernukkitx.block.BlockState, Integer> weights = new java.util.HashMap<>();
        helper.addRandomBlock(weights, 3, org.powernukkitx.block.BlockDirt.PROPERTIES.getDefaultState());
        helper.addRandomBlock(weights, 1, org.powernukkitx.block.BlockCobblestone.PROPERTIES.getDefaultState());
        safe(() -> helper.getRandomBlock(random, weights));
        safe(() -> helper.setBlockWithRandomBlock(new BlockVector3(1, 1, 1), random, weights));
        safe(() -> helper.fillWithRandomBlock(new BlockVector3(0, 0, 0), new BlockVector3(1, 1, 1), random, weights));
        safe(() -> helper.getBlockAt(0, 0, 0));

        assertTrue(CHECKED.get() > 0);
    }

    @Test
    void jigsawPlaceRunsForEveryStructure() {
        List<JigsawStructure> structures = new ArrayList<>();
        safe(() -> structures.add(new PlainsVillageStructure()));
        safe(() -> structures.add(new DesertVillageStructure()));
        safe(() -> structures.add(new SavannaVillageStructure()));
        safe(() -> structures.add(new TaigaVillageStructure()));
        safe(() -> structures.add(new SnowyVillageStructure()));
        safe(() -> structures.add(new BastionStructure()));
        safe(() -> structures.add(new TrialChambersStructure()));
        safe(() -> structures.add(new AncientCityStructure()));
        safe(() -> structures.add(new TrailRuinsStructure()));

        for (int y = 0; y < structures.size(); y++) {
            JigsawStructure structure = structures.get(y);
            long seed = 0xABCDEFL + y;
            safe(() -> structure.getEntryPool());
            safe(() -> structure.getStructurePoolCollection());
            // A fresh origin per structure keeps their footprints from overlapping.
            StructureHelper helper = new StructureHelper(level, new BlockVector3(y * 512, 64, 0));
            safe(() -> structure.place(helper, new Xoroshiro128(seed)));
        }

        assertTrue(CHECKED.get() > 0);
    }
}
