package org.powernukkitx.level.generator.object.structures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockEndPortalFrame;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrongholdPortalTest {

    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void generatedPortalRoomsCanBeActivated() {
        int offset = 0;
        for (BlockFace orientation : BlockFace.Plane.HORIZONTAL) {
            StrongholdPieces.PortalRoom room = StrongholdPieces.PortalRoom.createPiece(
                    new ArrayList<>(), offset, 64, offset, orientation, 0);
            assertNotNull(room);

            BoundingBox box = room.getBoundingBox();
            BlockManager manager = new BlockManager(level);
            room.postProcess(manager, new Xoroshiro128(1), box, box.x0 >> 4, box.z0 >> 4);
            manager.applyWithoutUpdate();

            List<BlockEndPortalFrame> frames = findPortalFrames(box);
            assertEquals(12, frames.size());
            for (BlockEndPortalFrame frame : frames) {
                assertEquals(expectedFace(frame, frames), frame.getBlockFace(),
                        () -> "Unexpected frame direction at " + frame.getLocation());
                frame.setEndPortalEye(true);
                level.setBlock(frame, frame, true, true);
            }

            frames.getFirst().createPortal();
            assertEquals(9, countPortalBlocks(box));
            offset += 64;
        }
    }

    private List<BlockEndPortalFrame> findPortalFrames(BoundingBox box) {
        List<BlockEndPortalFrame> frames = new ArrayList<>();
        for (int x = box.x0; x <= box.x1; x++) {
            for (int z = box.z0; z <= box.z1; z++) {
                Block block = level.getBlock(x, box.y0 + 3, z);
                if (block instanceof BlockEndPortalFrame frame) {
                    frames.add(frame);
                }
            }
        }
        return frames;
    }

    private BlockFace expectedFace(BlockEndPortalFrame frame, List<BlockEndPortalFrame> frames) {
        int minX = frames.stream().mapToInt(Block::getFloorX).min().orElseThrow();
        int minZ = frames.stream().mapToInt(Block::getFloorZ).min().orElseThrow();
        int maxX = frames.stream().mapToInt(Block::getFloorX).max().orElseThrow();
        int centerZ = minZ + 2;
        if (frame.getFloorX() == minX) return BlockFace.EAST;
        if (frame.getFloorX() == maxX) return BlockFace.WEST;
        if (frame.getFloorZ() < centerZ) return BlockFace.SOUTH;
        return BlockFace.NORTH;
    }

    private int countPortalBlocks(BoundingBox box) {
        int count = 0;
        for (int x = box.x0; x <= box.x1; x++) {
            for (int z = box.z0; z <= box.z1; z++) {
                if (level.getBlockIdAt(x, box.y0 + 3, z).equals(Block.END_PORTAL)) {
                    count++;
                }
            }
        }
        return count;
    }
}
