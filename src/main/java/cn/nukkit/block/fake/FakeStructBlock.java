package cn.nukkit.block.fake;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.IStructBlock;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class FakeStructBlock extends SingleFakeBlock {

    public FakeStructBlock() {
        super(Block.STRUCTURE_BLOCK, BlockEntity.STRUCTURE_BLOCK);
    }

    @Override
    public void create(Player player) {
        var pos = player.getPosition().floor().asBlockVector3();
        create(pos, pos, player);
    }

    public void create(BlockVector3 targetStart, BlockVector3 targetEnd, Player player) {
        List<Vector3> positions = this.getPositions(player);
        this.lastPositions = positions;

        positions.forEach(position -> {
            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.blockRuntimeId = BlockStateRegistry.getRuntimeId(Block.STRUCTURE_BLOCK);
            updateBlockPacket.flags = UpdateBlockPacket.FLAG_NETWORK;
            updateBlockPacket.x = position.getFloorX();
            updateBlockPacket.y = position.getFloorY();
            updateBlockPacket.z = position.getFloorZ();
            player.dataPacket(updateBlockPacket);

            BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
            blockEntityDataPacket.x = position.getFloorX();
            blockEntityDataPacket.y = position.getFloorY();
            blockEntityDataPacket.z = position.getFloorZ();
            try {
                blockEntityDataPacket.namedTag = NBTIO.write(this.getBlockEntityDataAt(position, targetStart, targetEnd), ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            player.dataPacket(blockEntityDataPacket);
        });
    }

    @Override
    public void remove(Player player) {
        this.lastPositions.forEach(position -> {
            UpdateBlockPacket packet = new UpdateBlockPacket();
            BlockStateRegistry.getRuntimeId(player.getLevel().getBlock(position).getCurrentState());
            packet.flags = UpdateBlockPacket.FLAG_NETWORK;
            packet.x = position.getFloorX();
            packet.y = position.getFloorY();
            packet.z = position.getFloorZ();
            player.dataPacket(packet);
        });
    }

    private CompoundTag getBlockEntityDataAt(Vector3 base, BlockVector3 targetStart, BlockVector3 targetEnd) {
        int offsetX = targetStart.x - base.getFloorX();
        int offsetY = targetStart.y - base.getFloorY();
        int offsetZ = targetStart.z - base.getFloorZ();
        int sizeX, sizeY, sizeZ;
        if (targetEnd.x - targetStart.x < 0) {
            offsetX += targetEnd.x - targetStart.x;
            sizeX = targetStart.x - targetEnd.x + 1;
        } else {
            sizeX = targetEnd.x - targetStart.x + 1;
        }
        if (targetEnd.y - targetStart.y < 0) {
            offsetY += targetEnd.y - targetStart.y;
            sizeY = targetStart.y - targetEnd.y + 1;
        } else {
            sizeY = targetEnd.y - targetStart.y + 1;
        }
        if (targetEnd.z - targetStart.z < 0) {
            offsetZ += targetEnd.z - targetStart.z;
            sizeZ = targetStart.z - targetEnd.z + 1;
        } else {
            sizeZ = targetEnd.z - targetStart.z + 1;
        }
        return new CompoundTag()
                .putString("id", tileId)
                .putInt("x", base.getFloorX())
                .putInt("y", base.getFloorY())
                .putInt("z", base.getFloorZ())
                .putBoolean(IStructBlock.TAG_SHOW_BOUNDING_BOX, true)
                .putInt(IStructBlock.TAG_X_STRUCTURE_OFFSET, offsetX)
                .putInt(IStructBlock.TAG_Y_STRUCTURE_OFFSET, offsetY)
                .putInt(IStructBlock.TAG_Z_STRUCTURE_OFFSET, offsetZ)
                .putInt(IStructBlock.TAG_X_STRUCTURE_SIZE, sizeX)
                .putInt(IStructBlock.TAG_Y_STRUCTURE_SIZE, sizeY)
                .putInt(IStructBlock.TAG_Z_STRUCTURE_SIZE, sizeZ);
    }
}
