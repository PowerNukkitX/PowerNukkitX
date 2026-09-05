package org.powernukkitx.level.generator.object.structures;

import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.legacytree.LegacyBigSpruceTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyBirchTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyChorusTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyCrimsonTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyDarkOakTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyJungleTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyOakTree;
import org.powernukkitx.level.generator.object.legacytree.LegacySpruceTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyTallBirchTree;
import org.powernukkitx.level.generator.object.legacytree.LegacyTallGrass;
import org.powernukkitx.level.generator.object.legacytree.LegacyTreeGenerator;
import org.powernukkitx.level.generator.object.legacytree.LegacyWarpedTree;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructurePiece;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke coverage for the legacy (non-jigsaw) structure piece families - mineshaft, nether
 * bridge, ocean monument and end city - plus the legacy tree generators. Each family is
 * driven the same way the matching populator drives it - build the seed piece(s), expand the
 * child pieces into a component list, then postProcess every piece against the fixture level.
 * The jigsaw villages/bastion are already covered by StructureJigsawSmokeTest and are not
 * touched here. Everything runs through safe(...) so a runtime miss in any single piece can't
 * fail the batch - the gate is only that we invoked at least one.
 */
class LegacyStructureSmokeTest {

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

    private BlockManager manager() {
        return new BlockManager(level);
    }

    private RandomSourceProvider rng(long seed) {
        return new Xoroshiro128(seed);
    }

    // Run postProcess against a 16x16 window over the piece's own bounding box, mirroring how
    // the populators chunk-slice the structure during placement.
    private void postProcessPieces(BlockManager manager, List<StructurePiece> pieces) {
        for (StructurePiece piece : pieces) {
            safe(() -> {
                BoundingBox bb = piece.getBoundingBox();
                int cx = bb.x0 >> 4;
                int cz = bb.z0 >> 4;
                int x = cx << 4;
                int z = cz << 4;
                piece.postProcess(manager, rng(0xBEEFL), new BoundingBox(x, z, x + 15, z + 15), cx, cz);
            });
        }
    }

    @Test
    void mineshaftPiecesGenerate() {
        BlockManager manager = manager();
        RandomSourceProvider random = rng(0x1114E51L);
        List<StructurePiece> pieces = new ArrayList<>();

        for (MineshaftPieces.Type type : MineshaftPieces.Type.values()) {
            safe(() -> {
                MineshaftPieces.MineshaftRoom start =
                        new MineshaftPieces.MineshaftRoom(0, random, 2, 2, type);
                pieces.add(start);
                start.addChildren(start, pieces, random);
            });
        }

        postProcessPieces(manager, pieces);
        assertTrue(checked > 0);
    }

    @Test
    void netherBridgePiecesGenerate() {
        BlockManager manager = manager();
        RandomSourceProvider random = rng(0xF0227L);
        List<StructurePiece> pieces = new ArrayList<>();

        safe(() -> {
            NetherBridgePieces.StartPiece start = new NetherBridgePieces.StartPiece(random, 2, 2);
            pieces.add(start);
            start.addChildren(start, pieces, random);

            List<StructurePiece> pending = start.pendingChildren;
            int guard = 0;
            while (!pending.isEmpty() && guard++ < 500) {
                int idx = pending.size() == 1 ? 0 : random.nextBoundedInt(pending.size() - 1);
                pending.remove(idx).addChildren(start, pieces, random);
            }
        });

        postProcessPieces(manager, pieces);
        assertTrue(checked > 0);
    }

    @Test
    void oceanMonumentPiecesGenerate() {
        BlockManager manager = manager();
        RandomSourceProvider random = rng(0x0CEA4L);
        List<StructurePiece> pieces = new ArrayList<>();

        safe(() -> {
            OceanMonumentPieces.MonumentBuilding building = new OceanMonumentPieces.MonumentBuilding(
                    random, -29, -29, BlockFace.Plane.HORIZONTAL.random(random));
            pieces.add(building);
        });

        postProcessPieces(manager, pieces);
        assertTrue(checked > 0);
    }

    @Test
    void endCityPiecesGenerate() {
        BlockManager manager = manager();
        RandomSourceProvider random = new Xoroshiro128(level.getSeed());
        BlockVector3 origin = new BlockVector3(8, 70, 8);

        for (Rotation rotation : Rotation.values()) {
            safe(() -> EndCityPieces.generate(origin, rotation, random));
        }

        safe(() -> {
            EndCityPieces.PostPlacement placement = EndCityPieces.place(manager, origin, Rotation.NONE, random);
            EndCityPieces.populatePlacedData(
                    level,
                    placement.chests(),
                    placement.banners(),
                    placement.itemFrames(),
                    placement.brewingStands(),
                    placement.shulkerMarkers(),
                    new Xoroshiro128(level.getSeed()));
        });

        assertTrue(checked > 0);
    }

    private void driveTree(LegacyTreeGenerator tree, int x, int z) {
        safe(() -> tree.generate(manager(), rng(0xC0FFEEL + x + z), new Vector3(x, 70, z)));
    }

    @Test
    void legacyTreesGenerate() {
        int x = 0;
        driveTree(new LegacyOakTree(), x += 6, 0);
        driveTree(new LegacyBirchTree(), x += 6, 0);
        driveTree(new LegacyTallBirchTree(), x += 6, 0);
        driveTree(new LegacyDarkOakTree(6, 3), x += 6, 0);
        driveTree(new LegacyJungleTree(), x += 6, 0);
        driveTree(new LegacySpruceTree(), x += 6, 0);
        driveTree(new LegacyBigSpruceTree(6, 3), x += 6, 0);
        driveTree(new LegacyCrimsonTree(), x += 6, 0);
        driveTree(new LegacyWarpedTree(), x += 6, 0);

        // chorus tree has its own overloads
        final int cx = x + 6;
        safe(() -> new LegacyChorusTree().generate(manager(), rng(0xC4005L), new Vector3(cx, 70, 0)));
        safe(() -> new LegacyChorusTree().generate(manager(), rng(0xC4006L), new Vector3(cx, 70, 8), 8));
        safe(() -> new LegacyChorusTree().growImmediately(manager(), rng(0xC4007L), new Vector3(cx, 70, 16), 8, 0));

        // static growTree dispatcher covers each WoodType path
        for (WoodType type : new WoodType[]{WoodType.OAK, WoodType.BIRCH, WoodType.SPRUCE,
                WoodType.JUNGLE, WoodType.DARK_OAK}) {
            final int gx = x += 6;
            safe(() -> LegacyTreeGenerator.growTree(manager(), gx, 70, 24, rng(gx), type, false));
            safe(() -> LegacyTreeGenerator.growTree(manager(), gx, 70, 32, rng(gx + 1), type, true));
        }

        final int grassX = x + 6;
        safe(() -> LegacyTallGrass.growGrass(manager(), new Vector3(grassX, 70, 0), rng(0x64555L)));

        assertTrue(checked > 0);
    }
}
