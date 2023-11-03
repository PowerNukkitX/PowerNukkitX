package cn.nukkit.level.format.updater;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;

class FacingToCardinalUpdater implements Updater {
    private final ChunkSection section;

    public FacingToCardinalUpdater(ChunkSection section) {
        this.section = section;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        int blockId = state.getBlockId();
        if (blockId != BlockID.CHEST
                && blockId != BlockID.ENDER_CHEST
                && blockId != BlockID.TRAPPED_CHEST
                && blockId != BlockID.STONECUTTER_BLOCK) {
            return false;
        }

        return section.setBlockStateAtLayer(x, y, z, 0, state.withData(getNewData(state.getExactIntStorage())));
    }

    private int getNewData(int fromData) {
        switch (fromData) {
            case 0, 1, 2: //facing_direction=DOWN/UP/NORTH
                return 2; //cardinal_direction=NORTH
            case 3: //facing_direction=SOUTH
                return 0; //cardinal_direction=SOUTH
            case 4: //facing_direction=WEST
                return 1; //cardinal_direction=WEST
            case 5: //facing_direction=EAST
                return 3; //cardinal_direction=EAST
            default:
                return fromData;
        }
    }
}
