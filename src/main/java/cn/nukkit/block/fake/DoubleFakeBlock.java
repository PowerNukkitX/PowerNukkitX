package cn.nukkit.block.fake;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.List;


public class DoubleFakeBlock extends SingleFakeBlock {

    public DoubleFakeBlock(String blockId, String tileId) {
        super(blockId, tileId);
    }

    @Override
    public List<Vector3> getPositions(Player player) {
        Vector3 blockPosition = this.getOffset(player);
        if ((blockPosition.getFloorX() & 1) == 1) {
            return Arrays.asList(blockPosition, blockPosition.east());
        }
        return Arrays.asList(blockPosition, blockPosition.west());
    }

    @Override
    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        return super.getBlockEntityDataAt(position, title)
                .putInt("pairx", position.getFloorX() + ((position.getFloorX() & 1) == 1 ? 1 : -1))
                .putInt("pairz", position.getFloorZ());
    }
}

