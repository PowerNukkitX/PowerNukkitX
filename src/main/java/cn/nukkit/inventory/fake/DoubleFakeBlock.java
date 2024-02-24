package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.collect.Lists;

import java.util.List;


public class DoubleFakeBlock extends SingleFakeBlock {
    public DoubleFakeBlock(String blockId) {
        super(Block.get(blockId), "default");
    }

    public DoubleFakeBlock(String blockId, String tileId) {
        super(Block.get(blockId), tileId);
    }

    public DoubleFakeBlock(Block block, String tileId) {
        super(block, tileId);
    }

    @Override
    public List<Vector3> getPlacePositions(Player player) {
        Vector3 blockPosition = this.getOffset(player);
        if ((blockPosition.getFloorX() & 1) == 1) {
            return Lists.newArrayList(blockPosition, blockPosition.east());
        }
        return Lists.newArrayList(blockPosition, blockPosition.west());
    }

    @Override
    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
        return super.getBlockEntityDataAt(position, title)
                .putInt("pairx", position.getFloorX() + ((position.getFloorX() & 1) == 1 ? 1 : -1))
                .putInt("pairz", position.getFloorZ());
    }
}

