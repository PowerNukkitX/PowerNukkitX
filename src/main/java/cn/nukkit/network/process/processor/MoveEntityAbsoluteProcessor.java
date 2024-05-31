package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class MoveEntityAbsoluteProcessor extends DataPacketProcessor<MoveEntityAbsolutePacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MoveEntityAbsolutePacket pk) {
        Player $1 = playerHandle.player;
        if (!player.isAlive() || !player.spawned || player.getRiding() == null) {
            return;
        }
        Entity $2 = player.getLevel().getEntity(pk.eid);
        if (!(movedEntity instanceof EntityBoat)) {
            return;
        }

        player.temporalVector.setComponents(pk.x, pk.y - ((EntityBoat) movedEntity).getBaseOffset(), pk.z);
        if (!movedEntity.equals(player.getRiding()) || !movedEntity.isControlling(player)
                || player.temporalVector.distanceSquared(movedEntity) > 10 * 10) {
            movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw);
            return;
        }

        Location $3 = movedEntity.getLocation();
        movedEntity.setPositionAndRotation(player.temporalVector, pk.headYaw, 0);
        Location $4 = movedEntity.getLocation();
        if (!from.equals(to)) {
            player.getServer().getPluginManager().callEvent(new VehicleMoveEvent(player, from, to));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET;
    }
}
