package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
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

    /**
     * Maximum horizontal distance (in blocks) around the current position to search for an existing portal.
     * <p>
     * A radius of 128 blocks roughly mirrors vanilla Minecraft's portal search behavior and provides a
     * balance between finding nearby portals and keeping the block scan performant on large worlds.
     */
    private static final int PORTAL_SEARCH_RADIUS = 128;

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

        Block obsidian = Block.get(OBSIDIAN);
        Block netherPortal = Block.get(PORTAL);
        for (int xx = -2; xx <= 4; xx++) {
            for (int yy = -1; yy <= 5; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    Block block = lvl.getBlock(x + xx, y + yy, z + zz);           
                    if (block.getId() != BlockID.BEDROCK) {
                        lvl.setBlock(x + xx, y + yy, z + zz, Block.get(AIR), false, true);
                    }
                }
            }
        }
        // Shift portal so target build location becomes center of cleared location
        x -= 1;
        z -= 1;
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
                .filter(block -> !Objects.equals(block.down().getId(), BlockID.PORTAL))
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
        // Allow for a Custom Nether Scale to be used
        int scale = current.level.getNetherScale();

        DimensionData dimensionData;
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            dimensionData = DimensionEnum.NETHER.getDimensionData();
            Level netherLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_NETHER);
            if (netherLevel == null)
                return null;
            
            // Converts coordinates using the configurable nether scale
            int x = current.getFloorX() / scale;
            int z = current.getFloorZ() / scale;

            /* Prevent portal overlap when Nether scale is 1:1.
            * Without this offset, portals may generate at identical coordinates
            * in both dimensions, causing immediate teleport loops. 
            */
            if (scale == 1) {
                x += 2;
                z += 2;
            }
            // Nether Roof begins at y=121 setting a hard limit for 115 allows for a 5 block space to 120, never reaching bedrock.
            int y = Math.min(netherLevel.getHighestBlockAt(x, z),115);

            // Search downward for a safe portal spawn location
            for (int i = y; i > netherLevel.getMinHeight() + 2; i--) {
                Block ground = netherLevel.getBlock(x, i - 1, z);
                boolean space = netherLevel.getBlock(x, i, z).isAir() &&
                        netherLevel.getBlock(x, i + 1, z).isAir() &&
                        netherLevel.getBlock(x, i + 2, z).isAir() &&
                        netherLevel.getBlock(x, i + 3, z).isAir() &&
                        netherLevel.getBlock(x, i + 4, z).isAir();
                if (space
                        && ground.isSolid()
                        && ground.getId() != BlockID.LAVA
                        && ground.getId() != BlockID.BEDROCK) {
                    y = i;
                    break;
                }
            }
            
            Position target = new Position(
                    x,
                    NukkitMath.clamp(y, dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1,
                    z,
                    netherLevel);
            
            // Use existing portal instead of spawning a new one
            Optional<Position> portal = getNearestValidPortal(target);
            if (portal.isPresent()) {
                return portal.get();
            }
            return target
        } else if (current.level.getDimension() == Level.DIMENSION_NETHER) {

            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
            Level overworldLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_OVERWORLD);
            if (overworldLevel == null)
                return null;
            // Converts coordinates using the configurable nether scale
            int x = current.getFloorX() * scale;
            int z = current.getFloorZ() * scale;

            int y = overworldLevel.getHighestBlockAt(x, z);

            // Search downward for safe portal spawn
            for (int i = y; i > overworldLevel.getMinHeight() + 2; i--) {
                Block ground = overworldLevel.getBlock(x, i - 1, z);
                boolean space = overworldLevel.getBlock(x, i, z).isAir() &&
                        overworldLevel.getBlock(x, i + 1, z).isAir() &&
                        overworldLevel.getBlock(x, i + 2, z).isAir() &&
                        overworldLevel.getBlock(x, i + 3, z).isAir() &&
                        overworldLevel.getBlock(x, i + 4, z).isAir();
                if (space
                        && ground.isSolid()
                        && ground.getId() != BlockID.WATER
                        && ground.getId() != BlockID.LAVA) {
                    y = i;
                    break;
                }
            }

            Position target = new Position(
                    x,
                    NukkitMath.clamp(y, dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1,
                    z,
                    overworldLevel);
            
            // Use existing portal instead of spawning a new one
            return portal.orElse(target)
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
