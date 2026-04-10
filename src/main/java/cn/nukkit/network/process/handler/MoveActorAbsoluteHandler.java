package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;

/**
 * @author Kaooot
 */
public class MoveActorAbsoluteHandler implements PacketHandler<MoveActorAbsolutePacket> {

    @Override
    public void handle(MoveActorAbsolutePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.isAlive() || !player.spawned || player.getRiding() == null) {
            return;
        }
        if (!playerHandle.packetRateLimiter.tryMovement()) {
            return;
        }
        Entity movedEntity = player.getLevel().getEntity(packet.getMoveData().getActorRuntimeID());
        if (!(movedEntity instanceof EntityBoat)) {
            return;
        }

        player.temporalVector.setComponents(packet.getMoveData().getPos().getX(), packet.getMoveData().getPos().getY() - ((EntityBoat) movedEntity).getBaseOffset(), packet.getMoveData().getPos().getZ());
        if (!movedEntity.equals(player.getRiding()) || !movedEntity.isControlling(player)
                || player.temporalVector.distanceSquared(movedEntity) > 10 * 10) {
            movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw);
            return;
        }

        Location from = movedEntity.getLocation();
        movedEntity.setPositionAndRotation(player.temporalVector, packet.getMoveData().getRotation().getZ(), 0);
        Location to = movedEntity.getLocation();
        if (!from.equals(to)) {
            player.getServer().getPluginManager().callEvent(new VehicleMoveEvent(player, from, to));
        }
    }
}