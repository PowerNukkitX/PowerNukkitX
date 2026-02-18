package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.jetbrains.annotations.NotNull;

public class MoveEntityAbsoluteProcessor extends DataPacketProcessor<MoveEntityAbsolutePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MoveEntityAbsolutePacket pk) {
        Player player = playerHandle.player;
        if (!player.isAlive() || !player.spawned || player.getRiding() == null) {
            return;
        }
        Entity movedEntity = player.getLevel().getEntity(pk.getRuntimeEntityId());
        if (!(movedEntity instanceof EntityBoat)) {
            return;
        }

        var position = pk.getPosition();
        player.temporalVector.setComponents(position.getX(), position.getY() - ((EntityBoat) movedEntity).getBaseOffset(), position.getZ());
        if (!movedEntity.equals(player.getRiding()) || !movedEntity.isControlling(player)
                || player.temporalVector.distanceSquared(movedEntity) > 10 * 10) {
            movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw);
            return;
        }

        Location from = movedEntity.getLocation();
        movedEntity.setPositionAndRotation(player.temporalVector, pk.getRotation().getY(), 0);
        Location to = movedEntity.getLocation();
        if (!from.equals(to)) {
            player.getServer().getPluginManager().callEvent(new VehicleMoveEvent(player, from, to));
        }
    }

    @Override
    public Class<MoveEntityAbsolutePacket> getPacketClass() {
        return MoveEntityAbsolutePacket.class;
    }
}
