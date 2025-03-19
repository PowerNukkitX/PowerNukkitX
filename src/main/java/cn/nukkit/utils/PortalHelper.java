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
import java.util.function.BiPredicate;

import static cn.nukkit.level.Level.DIMENSION_NETHER;
import static cn.nukkit.level.Level.DIMENSION_OVERWORLD;
import static cn.nukkit.level.Level.DIMENSION_THE_END;

@Slf4j
public final class PortalHelper implements BlockID {
    public static void spawnPortal(Position pos) {
        Level lvl = pos.level; //TODO: This will generate part of the time, seems to be only when the chunk is populated
        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        Block air = Block.get(AIR);
        Block obsidian = Block.get(OBSIDIAN);
        Block netherPortal = Block.get(PORTAL);
        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++) {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlock(x + xx, y + yy, z + zz, air, false, true);
                }
            }
        }

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

        for (int i = 0; i < 3; i++) {
            y++;
            lvl.setBlock(x, y, z, obsidian, false, true);
            lvl.setBlock(x + 1, y, z, netherPortal, false, true);
            lvl.setBlock(x + 2, y, z, netherPortal, false, true);
            lvl.setBlock(x + 3, y, z, obsidian, false, true);
        }

        y++;
        lvl.setBlock(x, y, z, obsidian, false, true);
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        lvl.setBlock(x + 3, y, z, obsidian, false, true);
    }

    public static Position getNearestValidPortal(Position currentPos) {
        AxisAlignedBB axisAlignedBB = new SimpleAxisAlignedBB(
                new Vector3(currentPos.getFloorX() - 128.0, currentPos.level.getDimensionData().getMinHeight(), currentPos.getFloorZ() - 128.0),
                new Vector3(currentPos.getFloorX() + 128.0, currentPos.level.getDimensionData().getMaxHeight(), currentPos.getFloorZ() + 128.0));
        BiPredicate<BlockVector3, BlockState> condition = (pos, state) -> Objects.equals(state.getIdentifier(), BlockID.PORTAL);
        List<Block> blocks = currentPos.level.scanBlocks(axisAlignedBB, condition);

        if (blocks.isEmpty()) {
            return null;
        }

        final Vector2 currentPosV2 = new Vector2(currentPos.getFloorX(), currentPos.getFloorZ());
        final double by = currentPos.getFloorY();
        Comparator<Block> euclideanDistance = Comparator.comparingDouble(block -> currentPosV2.distanceSquared(block.getFloorX(), block.getFloorZ()));
        Comparator<Block> heightDistance = Comparator.comparingDouble(block -> {
            double ey = by - block.y;
            return ey * ey;
        });

        return blocks.stream()
                .filter(block -> !block.down().getId().equals(BlockID.PORTAL))
                .min(euclideanDistance.thenComparing(heightDistance))
                .orElse(null);
    }

    public static Position convertPosBetweenNetherAndOverworld(Position current) {
        DimensionData dimensionData;
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            dimensionData = DimensionEnum.NETHER.getDimensionData();
            Level netherLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_NETHER);
            if(netherLevel == null) return null;
            return new Position(current.getFloorX() >> 3, NukkitMath.clamp(current.getFloorY(), dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1, current.getFloorZ() >> 3, netherLevel);
        } else if (current.level.getDimension() == Level.DIMENSION_NETHER) {
            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
            Level overworldLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_OVERWORLD);
            if(overworldLevel == null) return null;
            int x = current.getFloorX() << 3;
            int z = current.getFloorZ() << 3;
            int y = overworldLevel.getHighestBlockAt(x, z);
            for(int i = overworldLevel.getMinHeight(); i < y; i++) {
                if(overworldLevel.getBlock(x, i, z) instanceof BlockPortal) y = i;
                break;
            }
            return new Position(x, NukkitMath.clamp(y, dimensionData.getMinHeight(), dimensionData.getMaxHeight()) + 1, z, overworldLevel);
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position convertPosBetweenEndAndOverworld(Position current) {
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            Level endLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_THE_END);
            if(endLevel == null) return null;
            return new Location(100, 50, 0, endLevel);
        } else if (current.level.getDimension() == DIMENSION_THE_END) {
            Level overworldLevel = current.getLevel().getDimensionDestinationLevel(DIMENSION_OVERWORLD);
            if(overworldLevel == null) return null;
            return overworldLevel.getSafeSpawn();
        } else {
            throw new IllegalArgumentException("Neither overworld nor end given!");
        }
    }
}
