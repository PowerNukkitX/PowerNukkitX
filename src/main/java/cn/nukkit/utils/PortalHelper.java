package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
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

import static cn.nukkit.level.Level.DIMENSION_OVERWORLD;

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
        Level defaultNetherLevel = Server.getInstance().getDefaultNetherLevel();
        if (defaultNetherLevel == null) return null;
        DimensionData dimensionData;
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            dimensionData = DimensionEnum.NETHER.getDimensionData();
            return new Position(current.getFloorX() >> 3, NukkitMath.clamp(current.getFloorY(), dimensionData.getMinHeight(), dimensionData.getMaxHeight()), current.getFloorZ() >> 3, defaultNetherLevel);
        } else if (current.level.getDimension() == Level.DIMENSION_NETHER) {
            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
            return new Position(current.getFloorX() << 3, NukkitMath.clamp(current.getFloorY(), dimensionData.getMinHeight(), dimensionData.getMaxHeight()), current.getFloorZ() << 3, Server.getInstance().getDefaultLevel());
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position moveToTheEnd(Position current) {
        Level defaultEndLevel = Server.getInstance().getDefaultEndLevel();
        if (defaultEndLevel == null) return null;
        if (current.level.getDimension() == DIMENSION_OVERWORLD) {
            return new Position(100, 49, 0, defaultEndLevel);
        } else if (current.level.getDimension() == Level.DIMENSION_THE_END) {
            return Server.getInstance().getDefaultLevel().getSpawnLocation();
        } else {
            throw new IllegalArgumentException("Neither overworld nor the end given!");
        }
    }
}
