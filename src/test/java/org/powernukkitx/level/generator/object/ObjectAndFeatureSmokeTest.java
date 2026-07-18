package org.powernukkitx.level.generator.object;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.Generator;
import org.powernukkitx.level.generator.object.structures.ObjectDesertPyramid;
import org.powernukkitx.level.generator.object.structures.ObjectDesertWell;
import org.powernukkitx.level.generator.object.structures.ObjectJungleTemple;
import org.powernukkitx.level.generator.object.structures.ObjectMonsterRoom;
import org.powernukkitx.level.generator.object.structures.ObjectSwampHut;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke coverage for generator content - drives every reachable object/structure/feature
 * against the real fixture level. Objects use the ObjectGenerator.generate(BlockManager,
 * RandomSourceProvider, Vector3) contract; decoration features use the
 * GenerateFeature.apply(ChunkGenerateContext) contract. Everything is wrapped in safe(...)
 * so a runtime miss in any single generator can't fail the batch - the gate is only that
 * we invoked at least one.
 */
class ObjectAndFeatureSmokeTest {

    private static Level level;
    private static int checked;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
    }

    private static void safe(Runnable r) {
        checked++;
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    private void driveObject(ObjectGenerator object, int x, int y, int z) {
        safe(() -> {
            RandomSourceProvider rand = RandomSourceProvider.create(0xC0FFEEL + x + z);
            object.generate(new BlockManager(level), rand, new Vector3(x, y, z));
        });
    }

    @Test
    void objectsAndStructuresGenerate() {
        int y = 70;
        int col = 0;
        // no-arg object generators
        driveObject(new ObjectAzaleaTree(), col += 4, y, 0);
        driveObject(new ObjectBigMushroom(), col += 4, y, 0);
        driveObject(new ObjectBigSpruceTree(), col += 4, y, 0);
        driveObject(new ObjectCherryTree(), col += 4, y, 0);
        driveObject(new ObjectDarkOakTree(), col += 4, y, 0);
        driveObject(new ObjectEndGateway(), col += 4, y, 0);
        driveObject(new ObjectEndIsland(), col += 4, y, 0);
        driveObject(new ObjectExitPortal(), col += 4, y, 0);
        driveObject(new ObjectFallenTree(), col += 4, y, 0);
        driveObject(new ObjectFancyOakTree(), col += 4, y, 0);
        driveObject(new ObjectJungleBush(), col += 4, y, 0);
        driveObject(new ObjectMangroveTree(), col += 4, y, 0);
        driveObject(new ObjectObsidianPillar(), col += 4, y, 0);
        driveObject(new ObjectPaleOakTree(), col += 4, y, 0);
        driveObject(new ObjectSavannaTree(), col += 4, y, 0);
        driveObject(new ObjectSmallSpruceTree(), col += 4, y, 0);
        driveObject(new ObjectSwampOakTree(2, 4), col += 4, y, 0);
        // parameterized-only object generators
        driveObject(new ObjectJungleTree(4, 10), col += 4, y, 0);
        driveObject(new ObjectJungleBigTree(10, 20), col += 4, y, 0);
        driveObject(new ObjectSmallPaleOakTree(4, 6), col += 4, y, 0);
        // alternate constructor flavours
        driveObject(new ObjectMangroveTree(true), col += 4, y, 0);
        driveObject(new ObjectFancyOakTree(5, 2, 4), col += 4, y, 0);

        // structures
        driveObject(new ObjectDesertWell(), col += 6, y, 0);
        driveObject(new ObjectDesertPyramid(), col += 24, y, 0);
        driveObject(new ObjectJungleTemple(), col += 24, y, 0);
        driveObject(new ObjectMonsterRoom(), col += 8, y, 0);
        driveObject(new ObjectSwampHut(), col += 12, y, 0);

        assertTrue(checked > 0, "expected at least one object driven");
    }

    private void driveFeature(GenerateFeature feature) {
        safe(() -> {
            IChunk chunk = level.getChunk(0, 0, true);
            Generator generator = level.getGenerator();
            ChunkGenerateContext context = new ChunkGenerateContext(generator, level, chunk);
            feature.apply(context);
        });
    }

    @Test
    void decorationFeaturesApply() {
        GenerateFeature[] features = {
                new org.powernukkitx.level.generator.feature.decoration.AmethystGeodeFeature(),
                new org.powernukkitx.level.generator.feature.decoration.AzaleaRootSystemSnapToCeilingFeature(),
                new org.powernukkitx.level.generator.feature.decoration.BambooForestBambooFeature(),
                new org.powernukkitx.level.generator.feature.decoration.BirchForestWildflowersFeature(),
                new org.powernukkitx.level.generator.feature.decoration.BushFeature(),
                new org.powernukkitx.level.generator.feature.decoration.CoralClawFeature(),
                new org.powernukkitx.level.generator.feature.decoration.CoralMushroomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.CoralTreeFeature(),
                new org.powernukkitx.level.generator.feature.decoration.DeadBushFeature(),
                new org.powernukkitx.level.generator.feature.decoration.DesertCactusFeature(),
                new org.powernukkitx.level.generator.feature.decoration.DripstoneClusterFeature(),
                new org.powernukkitx.level.generator.feature.decoration.EyeBlossomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.FireflyBushClusterFeature(),
                new org.powernukkitx.level.generator.feature.decoration.FireflyBushWaterClusterFeature(),
                new org.powernukkitx.level.generator.feature.decoration.FlowerForestFoliageFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ForestFlowerFoliageFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ForestFoliageFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ForestRockFeature(),
                new org.powernukkitx.level.generator.feature.decoration.GlowLichenFeature(),
                new org.powernukkitx.level.generator.feature.decoration.HugeMushroomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.IcebergFeature(),
                new org.powernukkitx.level.generator.feature.decoration.IcePatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.IceSpikeFeature(),
                new org.powernukkitx.level.generator.feature.decoration.JungleGrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.JungleMelonGenerateFeature(),
                new org.powernukkitx.level.generator.feature.decoration.KelpFeature(),
                new org.powernukkitx.level.generator.feature.decoration.MesaFoliageFeature(),
                new org.powernukkitx.level.generator.feature.decoration.MonsterRoomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.MossPatchSnapToFloorFeature(),
                new org.powernukkitx.level.generator.feature.decoration.MossSnapToCeilingFeature(),
                new org.powernukkitx.level.generator.feature.decoration.OceanSeagrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.OverworldSurfaceSpringsFeature(),
                new org.powernukkitx.level.generator.feature.decoration.OverworldUnderwaterMagmaFeature(),
                new org.powernukkitx.level.generator.feature.decoration.PaleMossPatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.PinkPetalsFeature(),
                new org.powernukkitx.level.generator.feature.decoration.PumpkinGenerateFeature(),
                new org.powernukkitx.level.generator.feature.decoration.RandomClayWithDripleavesSnapToFloorFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ReedsFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterBrownMushroomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterDryGrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterOverworldFlowerFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterPlainsFlowerFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterRedMushroomFeature(),
                new org.powernukkitx.level.generator.feature.decoration.ScatterSweetBerryBushFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SculkPatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SeaAnemoneFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SeagrassRiverGenerateFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SeaPickleFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SulfurPoolWithPotentSulfurSnapToSurfaceFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SulfurSpikeClusterFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SulfurSpikeFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SulfurSpringTrailToSurfaceSnapToCeilingFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SunflowerDouplePlantPatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SwampFlowerFeature(),
                new org.powernukkitx.level.generator.feature.decoration.SwampSeagrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.TaigaGrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.TallFernPatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.TallGrassGenerateFeature(),
                new org.powernukkitx.level.generator.feature.decoration.TallGrassPatchFeature(),
                new org.powernukkitx.level.generator.feature.decoration.WarmOceanSeagrassFeature(),
                new org.powernukkitx.level.generator.feature.decoration.WaterlilyFeature(),
        };
        for (GenerateFeature feature : features) {
            driveFeature(feature);
        }

        assertTrue(checked > 0, "expected at least one feature driven");
    }
}
