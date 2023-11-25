package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;

public class LogUpdater implements Updater {
    private final ChunkSection section;

    public LogUpdater(ChunkSection section) {
        this.section = section;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        int blockId = state.getBlockId();
        if (blockId != BlockID.LOG) {
            return false;
        }

        int newLogID = getNewID(state.getExactIntStorage(), blockId);
        if (newLogID == blockId) {
            return false;
        }
        return section.setBlock(x, y, z, newLogID);
    }

    private int getNewID(int fromData, int originID) {
        return switch (fromData) {
            case 4, 5 -> // old block states for acacia and dark oak logs
                    162; // new block id for acacia log and dark oak log
            default -> originID;
        };
    }
}
