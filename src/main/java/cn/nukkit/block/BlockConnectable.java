package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;


public interface BlockConnectable {

    Block getSideAtLayer(int layer, BlockFace face);

    boolean canConnect(Block block);

    default boolean isStraight() {
        Set<BlockFace> connections = getConnections();
        if (connections.size() != 2) {
            return false;
        }

        Iterator<BlockFace> iterator = connections.iterator();
        BlockFace a = iterator.next();
        BlockFace b = iterator.next();
        return a.getOpposite() == b;
    }

    default Set<BlockFace> getConnections() {
        EnumSet<BlockFace> connections = EnumSet.noneOf(BlockFace.class);
        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            if (isConnected(blockFace)) {
                connections.add(blockFace);
            }
        }
        return connections;
    }

    default boolean isConnected(BlockFace face) {
        return canConnect(getSideAtLayer(0, face));
    }
}
