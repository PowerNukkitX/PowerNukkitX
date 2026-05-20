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
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kaooot
 */
@Slf4j
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
        Vector3f pos = packet.getMoveData().getPos();
        Vector3f rot = packet.getMoveData().getRotation();
        if (!Double.isFinite(pos.getX()) || !Double.isFinite(pos.getY()) || !Double.isFinite(pos.getZ()) || !Double.isFinite(rot.getX()) || !Double.isFinite(rot.getY()) || !Double.isFinite(rot.getZ())) {
            log.debug("Player {} sent invalid movement values (NaN or Infinite)", playerHandle.getUsername());
            return;
        }
        Entity movedEntity = player.getLevel().getEntity(packet.getMoveData().getActorRuntimeID());
        if (!(movedEntity instanceof EntityBoat)) {
            return;
        }

        player.temporalVector.setComponents(pos.getX(), pos.getY() - ((EntityBoat) movedEntity).getBaseOffset(), packet.getMoveData().getPos().getZ());
        if (!movedEntity.equals(player.getRiding()) || !movedEntity.isControlling(player)
                || player.temporalVector.distanceSquared(movedEntity) > 10 * 10) {
            movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw);
            return;
        }

        Location from = movedEntity.getLocation();
        movedEntity.setPositionAndRotation(player.temporalVector, rot.getZ(), 0);
        Location to = movedEntity.getLocation();
        if (!from.equals(to)) {
            player.getServer().getPluginManager().callEvent(new VehicleMoveEvent(player, from, to));
        }
    }
}