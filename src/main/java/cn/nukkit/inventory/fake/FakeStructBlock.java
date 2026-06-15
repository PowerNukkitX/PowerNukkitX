package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.IStructBlock;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RuntimeBlockDefinition;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.BlockActorDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;

import java.util.HashSet;


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
        createAndGetLastPositions(player).add(this.getOffset(player));
        lastPositions.get(player).forEach(position -> {
            final Vector3i vector3i = Vector3i.from(position.getFloorX(), position.getFloorY(), position.getFloorZ());
            final UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.getFlags().add(UpdateBlockPacket.Flag.NETWORK);
            updateBlockPacket.setBlockPosition(vector3i);
            updateBlockPacket.setDefinition(new RuntimeBlockDefinition(this.block.getRuntimeId()));
            player.sendPacket(updateBlockPacket);

            final BlockActorDataPacket blockActorDataPacket = new BlockActorDataPacket();
            blockActorDataPacket.setBlockPosition(vector3i);
            blockActorDataPacket.setActorDataTags(this.getBlockEntityDataAt(position, targetStart, targetEnd));
            player.sendPacket(blockActorDataPacket);
        });
    }

    @Override
    public void remove(Player player) {
        this.lastPositions.getOrDefault(player, new HashSet<>()).forEach(position -> {
            final Vector3i vector3i = Vector3i.from(position.getFloorX(), position.getFloorY(), position.getFloorZ());
            final UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.getFlags().add(UpdateBlockPacket.Flag.NETWORK);
            updateBlockPacket.setBlockPosition(vector3i);
            updateBlockPacket.setDefinition(new RuntimeBlockDefinition(player.getLevel().getBlock(position).getRuntimeId()));
            player.sendPacket(updateBlockPacket);
        });
        this.lastPositions.clear();
    }

    private NbtMap getBlockEntityDataAt(Vector3 base, BlockVector3 targetStart, BlockVector3 targetEnd) {
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
        return NbtMap.builder()
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
                .putInt(IStructBlock.TAG_Z_STRUCTURE_SIZE, sizeZ)
                .build();
    }
}
