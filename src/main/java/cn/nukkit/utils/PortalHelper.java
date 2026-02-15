package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPortal;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import static cn.nukkit.level.Level.DIMENSION_NETHER;
import static cn.nukkit.level.Level.DIMENSION_OVERWORLD;
import static cn.nukkit.level.Level.DIMENSION_THE_END;

/**
 * Utility class for portal-related operations (Nether/End portals) in the Nukkit server.
 * Provides methods to spawn portals, find the nearest portal, and convert positions between dimensions.
 */
@Slf4j
public final class PortalHelper implements BlockID {
    private static final int PORTAL_SEARCH_RADIUS = 128;
    private static final int PORTAL_HEIGHT = 3;
    private static final int PORTAL_WIDTH = 2;

    /**
     * Spawns a Nether portal at the given position.
     * The portal is built with obsidian and air blocks, and the portal blocks are placed inside.
     *
     * @param pos The position where the portal should be spawned (bottom center of the frame).
     */
    public static void spawnPortal(Position pos) {
        if (pos == null || pos.level == null) {
            log.warn("Cannot spawn portal: position or level is null");
            return;
        }
        Level lvl = pos.level;
        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        Block air = Block.get(AIR);
        Block obsidian = Block.get(OBSIDIAN);
        Block netherPortal = Block.get(PORTAL);
        // Clear the portal area
        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++) {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlock(x + xx, y + yy, z + zz, air, false, true);
                }
            }
        }
        // Build the frame (bottom)
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        z++;
        lvl.setBlock(x, y, z, obsidian, false, true);
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        lvl.setBlock(x + 3, y, z, obsidian, false, true);
        z++;
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        z--;
        // Build the sides and portal blocks
        for (int i = 0; i < 3; i++) {
            y++;
            lvl.setBlock(x, y, z, obsidian, false, true);
            lvl.setBlock(x + 1, y, z, netherPortal, false, true);
            lvl.setBlock(x + 2, y, z, netherPortal, false, true);
            lvl.setBlock(x + 3, y, z, obsidian, false, true);
        }
        // Build the top
        y++;
        lvl.setBlock(x, y, z, obsidian, false, true);
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        lvl.setBlock(x + 3, y, z, obsidian, false, true);
    }

    /**
     * Finds the nearest valid portal block within a search radius around the given position.
     *
     * @param currentPos The position from which to search.
     * @return An Optional containing the nearest portal block position, or empty if none found.
     */
    public static Optional<Position> getNearestValidPortal(Position currentPos) {
        if (currentPos == null || currentPos.level == null) {
            log.warn("Cannot search for portal: position or level is null");
            return Optional.empty();
        }
        AxisAlignedBB searchBox = new SimpleAxisAlignedBB(
                new Vector3(currentPos.getFloorX() - PORTAL_SEARCH_RADIUS, currentPos.level.getDimensionData().getMinHeight(), currentPos.getFloorZ() - PORTAL_SEARCH_RADIUS),
                new Vector3(currentPos.getFloorX() + PORTAL_SEARCH_RADIUS, currentPos.level.getDimensionData().getMaxHeight(), currentPos.getFloorZ() + PORTAL_SEARCH_RADIUS));
        BiPredicate<BlockVector3, BlockState> isPortal = (pos, state) -> Objects.equals(state.getIdentifier(), BlockID.PORTAL);
        List<Block> blocks = currentPos.level.scanBlocks(searchBox, isPortal);
        if (blocks.isEmpty()) {
            return Optional.empty();
        }
        final Vector2 currentPosV2 = new Vector2(currentPos.getFloorX(), currentPos.getFloorZ());
        final double baseY = currentPos.getFloorY();
        Comparator<Block> byDistance = Comparator.comparingDouble(block -> currentPosV2.distanceSquared(block.getFloorX(), block.getFloorZ()));
        Comparator<Block> byHeight = Comparator.comparingDouble(block -> Math.pow(baseY - block.y, 2));
        return blocks.stream()
                .filter(block -> !block.down().getId().equals(BlockID.PORTAL))
                .min(byDistance.thenComparing(byHeight))
                .map(block -> new Position(block.x, block.y, block.z, block.level));
    }

    /**
     * Converts a position between the Nether and the Overworld, preserving relative coordinates.
     *
     * @param current The current position (in Nether or Overworld).
     * @return The converted position in the other dimension, or null if conversion is not possible.
     * @throws IllegalArgumentException if the position is not in Nether or Overworld.
     */
    public static Position convertPosBetweenNetherAndOverworld(Position current) {
        if (current == null || current.level == null) {
            log.warn("Cannot convert position: position or level is null");
            return null;
        }
        DimensionData dimensionData;
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            dimensionData = DimensionEnum.NETHER.getDimensionData();
            Level netherLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_NETHER);
            if (netherLevel == null) return null;
            return new Position(current.getFloorX() >> 3, NukkitMath.clamp(current.getFloorY(), dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1, current.getFloorZ() >> 3, netherLevel);
        } else if (current.level.getDimension() == Level.DIMENSION_NETHER) {
            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
            Level overworldLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_OVERWORLD);
            if (overworldLevel == null) return null;
            int x = current.getFloorX() << 3;
            int z = current.getFloorZ() << 3;
            int y = overworldLevel.getHighestBlockAt(x, z);
            for (int i = overworldLevel.getMinHeight(); i < y; i++) {
                if (overworldLevel.getBlock(x, i, z) instanceof BlockPortal) {
                    y = i;
                    break;
                }
            }
            return new Position(x, NukkitMath.clamp(y, dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1, z, overworldLevel);
        } else {
            throw new IllegalArgumentException("Position must be in Nether or Overworld!");
        }
    }

    /**
     * Converts a position between the End and the Overworld.
     *
     * @param current The current position (in End or Overworld).
     * @return The converted position in the other dimension, or null if conversion is not possible.
     * @throws IllegalArgumentException if the position is not in End or Overworld.
     */
    public static Position convertPosBetweenEndAndOverworld(Position current) {
        if (current == null || current.level == null) {
            log.warn("Cannot convert position: position or level is null");
            return null;
        }
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            Level endLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_THE_END);
            if (endLevel == null) return null;
            return new Location(100, 50, 0, endLevel);
        } else if (current.level.getDimension() == DIMENSION_THE_END) {
            Level overworldLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_OVERWORLD);
            if (overworldLevel == null) return null;
            return overworldLevel.getSafeSpawn();
        } else {
            throw new IllegalArgumentException("Position must be in End or Overworld!");
        }
    }
}
