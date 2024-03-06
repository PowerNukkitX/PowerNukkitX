package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.IStructBlock;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;


public class FakeStructBlock extends SingleFakeBlock {

    public FakeStructBlock() {
        super(new BlockStructureBlock(), BlockEntity.STRUCTURE_BLOCK);
    }

    @Override
    public Vector3 getOffset(Player player) {
        int floorX = player.getFloorX();
        int floorZ = player.getFloorZ();
        return new Vector3(floorX, player.getLevel().getMinHeight() + 1, floorZ);
    }

    @Override
    public void create(Player player) {
        var pos = player.getPosition().floor().asBlockVector3();
        create(pos, pos, player);
    }

    public void create(BlockVector3 targetStart, BlockVector3 targetEnd, Player player) {
        this.lastPositions.add(this.getOffset(player));

        lastPositions.forEach(position -> {
            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.blockRuntimeId = block.getRuntimeId();
            updateBlockPacket.flags = UpdateBlockPacket.FLAG_NETWORK;
            updateBlockPacket.x = position.getFloorX();
            updateBlockPacket.y = position.getFloorY();
            updateBlockPacket.z = position.getFloorZ();
            player.dataPacket(updateBlockPacket);

            BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
            blockEntityDataPacket.x = position.getFloorX();
            blockEntityDataPacket.y = position.getFloorY();
            blockEntityDataPacket.z = position.getFloorZ();
            blockEntityDataPacket.namedTag = this.getBlockEntityDataAt(position, targetStart, targetEnd);

            player.dataPacket(blockEntityDataPacket);
        });
    }

    @Override
    public void remove(Player player) {
        this.lastPositions.forEach(position -> {
            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.blockRuntimeId = player.getLevel().getBlock(position).getRuntimeId();
            packet.flags = UpdateBlockPacket.FLAG_NETWORK;
            packet.x = position.getFloorX();
            packet.y = position.getFloorY();
            packet.z = position.getFloorZ();
            player.dataPacket(packet);
        });
        this.lastPositions.clear();
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
