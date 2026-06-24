package cn.nukkit.network.process.handler;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityPhysical;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerJumpEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleCrawlEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.event.player.PlayerToggleGlideEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.event.player.PlayerToggleSwimEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PacketHandlerRegistry;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class PlayerAuthInputHandler implements PacketHandler<PlayerAuthInputPacket> {

    @Override
    public void handle(PlayerAuthInputPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        Vector3f pos = Vector3f.fromNetwork(packet.getPosition());
        Vector3f rot = Vector3f.fromNetwork(packet.getPosition());
        if (!Float.isFinite(pos.getX()) || !Float.isFinite(pos.getY()) || !Float.isFinite(pos.getZ()) || !Float.isFinite(rot.getX()) || !Float.isFinite(rot.getY()) || !Float.isFinite(rot.getZ())) {
            log.debug("Player {} sent invalid movement values (NaN or Infinite)", player.getName());
            return;
        }

        // Block-break completion and the bundled inventory request are run on the main thread
        // (see Player#scheduleInbound) so they do not race the server-auth break tick.
        player.scheduleInbound(() -> handleBlockActionsAndItemStackRequest(packet, holder, server, player));

        if (packet.getInputData().contains(PlayerAuthInputData.START_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(true);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, false);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(false);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.START_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(true);
                player.setBlocking(true);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, false);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(false);
                player.setBlocking(false);
            }
        }
        if (player.getAdventureSettings().get(AdventureSettings.Type.FLYING)) {
            player.setFlySneaking(packet.getInputData().contains(PlayerAuthInputData.SNEAKING));
        }
        if (packet.getInputData().contains(PlayerAuthInputData.START_JUMPING)) {
            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
            player.getServer().getPluginManager().callEvent(playerJumpEvent);
        }
        if (packet.getInputData().contains(PlayerAuthInputData.START_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(true);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(false);
            }
        }

        if (packet.getInputData().contains(PlayerAuthInputData.START_CRAWLING)) {
            var playerToggleCrawlEvent = new PlayerToggleCrawlEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleCrawlEvent);
            if (playerToggleCrawlEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setCrawling(true);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_CRAWLING)) {
            var playerToggleCrawlEvent = new PlayerToggleCrawlEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleCrawlEvent);
            if (playerToggleCrawlEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setCrawling(false);
            }
        }

        if (packet.getInputData().contains(PlayerAuthInputData.START_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(true);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(false);
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.START_FLYING)) {
            if (!player.getAllowFlight()) {
                PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
                Server.getInstance().getPluginManager().callEvent(detectedEvent);
                if (detectedEvent.isKick())
                    player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                return;
            }
            PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
            if (playerToggleFlightEvent.isCancelled()) {
                player.getAdventureSettings().update();
            } else {
                player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.STOP_FLYING)) {
            PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
            if (playerToggleFlightEvent.isCancelled()) {
                player.getAdventureSettings().update();
            } else {
                player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
            }
        }
        if (packet.getInputData().contains(PlayerAuthInputData.JUMP_RELEASED_RAW)
                && player.getRiding() != null
                && (player.riding instanceof EntityPhysical ride)
                && ride.isAlive()
                && ((ride.rideCanJump() && !ride.isRideJumping()) || ride.rideHasVerticalMove())
        ) {
            ride.getRideJumping().set(player.getLevel().getTick());
        }

        Vector3 clientPosition = Vector3.fromNetwork(packet.getPosition()).subtract(0, player.getBaseOffset(), 0);
        float yaw = packet.getPlayerRotation().getY() % 360;
        float pitch = packet.getPlayerRotation().getX() % 360;
        float headYaw = packet.getPlayerRotation().getZ() % 360;
        if (headYaw < 0) {
            headYaw += 360;
        }
        if (yaw < 0) {
            yaw += 360;
        }
        Location clientLoc = Location.fromObject(clientPosition, player.level, yaw, pitch, headYaw);

        Entity vehicle = null;
        if ((vehicle = player.getRiding()) != null && vehicle.hasWASDControls()) {
            syncVehiclePositionFromRiderInput(player, vehicle, packet);
            if (vehicle.onRiderInput(player, packet)) return;
        }

        player.offerMovementTask(clientLoc);
    }

    /**
     * Processes the packet's block-break actions and bundled item stack request. Runs on the main
     * thread (scheduled via {@link Player#scheduleInbound}) so it is serialized with the
     * server-auth break tick instead of racing it on the network thread.
     */
    private static void handleBlockActionsAndItemStackRequest(PlayerAuthInputPacket packet, PlayerSessionHolder holder, Server server, Player player) {
        if (!packet.getPlayerActions().isEmpty() && !server.getSettings().miscSettings().overrideServerAuthBlockBreaking()) {
            for (PlayerBlockActionData action : packet.getPlayerActions()) {
                //hack Since version 1.19.70, the Creative Mode Sword client no longer sends PREDITIC_DESTROY_BLOCK, but still sends START_DESTROY_BLOCK, filtering out
                if (player.getInventory().getItemInMainHand().isSword() && player.isCreative() && action.getAction() == PlayerActionType.START_DESTROY_BLOCK) {
                    continue;
                }
                Vector3i blockPos = action.getBlockPosition();
                BlockFace blockFace = BlockFace.fromIndex(action.getFace());
                PlayerHandle playerHandle = new PlayerHandle(player);
                if (playerHandle.getLastBlockAction() != null && playerHandle.getLastBlockAction().getAction() == PlayerActionType.PREDICT_DESTROY_BLOCK &&
                        action.getAction() == PlayerActionType.CONTINUE_DESTROY_BLOCK) {
                    playerHandle.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                }

                PlayerBlockActionData lastAction = playerHandle.getLastBlockAction();
                BlockVector3 lastBreakPos = lastAction == null ? null : BlockVector3.fromNetwork(lastAction.getBlockPosition());
                if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() || lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                    //When a block is broken instantaneous, the client sometimes just sends a START_DESTROY_BLOCK, but never completes or aborts it. On the client side, the block is also broken.
                    double breakTime = player.getLevel().getBlock(lastBreakPos.asVector3()).calculateBreakTime(player.getInventory().getItemInMainHand(), player);
                    boolean canCompleteBreak = Long.sum(player.lastBreak, (long) (breakTime * 1000)) <= System.currentTimeMillis() + 50;
                    if(canCompleteBreak && lastAction.getAction() == PlayerActionType.START_DESTROY_BLOCK) {
                        player.onBlockBreakComplete(BlockVector3.fromNetwork(blockPos), blockFace);
                    } else {
                        playerHandle.onBlockBreakAbort(lastBreakPos.asVector3());
                    }
                    player.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                }

                switch (action.getAction()) {
                    case START_DESTROY_BLOCK -> playerHandle.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                    case ABORT_DESTROY_BLOCK -> playerHandle.onBlockBreakAbort(Vector3.fromNetwork(blockPos.toFloat()));
                    case PREDICT_DESTROY_BLOCK -> playerHandle.onBlockBreakComplete(BlockVector3.fromNetwork(blockPos), blockFace);

                }
                player.setLastBlockAction(action);
            }
        }

        // As of 1.18 this is now used for sending item stack requests such as when mining a block.
        if (packet.getItemStackRequest() != null) {
            final ItemStackRequestPacket itemStackRequestPacket = new ItemStackRequestPacket();
            itemStackRequestPacket.getRequests().add(packet.getItemStackRequest());
            PacketHandlerRegistry.getPacketHandler(ItemStackRequestPacket.class).handle(itemStackRequestPacket, holder, server);
        }
    }

    private static void syncVehiclePositionFromRiderInput(Player player, Entity vehicle, PlayerAuthInputPacket pk) {
        if (vehicle == null || !vehicle.isAlive()) return;
        if (pk.getClientPredictedVehicle() == 0) return;
        if (pk.getClientPredictedVehicle() != vehicle.getId()) return;

        Vector3f pos = Vector3f.fromNetwork(pk.getPosition());

        if (!Float.isFinite(pos.x) || !Float.isFinite(pos.y) || !Float.isFinite(pos.z)) {
            log.debug("Player {} sent invalid position values (NaN or Infinite)", player.getName());
            return;
        }

        Vector2f vehicleRotation = Vector2f.fromNetwork(pk.getVehicleRotation());

        if (!Float.isFinite(vehicleRotation.x) || !Float.isFinite(vehicleRotation.y)) {
            log.debug("Player {} sent invalid vehicle rotation values (NaN or Infinite)", player.getName());
            return;
        }
        Vector3 packetPosition = Vector3f.fromNetwork(pk.getPosition()).asVector3();
        Vector3 vehiclePosition = packetPosition;
        EntityBoat boat = vehicle instanceof EntityBoat entityBoat ? entityBoat : null;

        if (boat != null) {
            double boatY = packetPosition.y - boat.getBaseOffset();
            vehiclePosition = new Vector3(packetPosition.x, boatY, packetPosition.z);
        }

        double vehiclePitch = vehicle.getPitch();
        double vehicleYaw = vehicle.getYaw();

        if (pk.getVehicleRotation() != null) {
            vehiclePitch = pk.getVehicleRotation().getX() % 360;
            vehicleYaw = pk.getVehicleRotation().getY() % 360;
        } else {
            vehiclePitch = pk.getPlayerRotation().getX() % 360;
            vehicleYaw = pk.getPlayerRotation().getY() % 360;
        }

        if (vehicleYaw < 0) vehicleYaw += 360;
        if (vehiclePitch < 0) vehiclePitch += 360;

        double distanceSquared = vehiclePosition.distanceSquared(vehicle);

        if (distanceSquared > 0.0001d) {
            vehicle.setPosition(vehiclePosition);
        }

        vehicle.setRotation(vehicleYaw, vehiclePitch);
        vehicle.setHeadYaw(vehicleYaw);
        vehicle.updateMovement();

        if (boat != null) {
            boat.updatePassengers(false, false);
        } else {
            player.setPosition(packetPosition);
        }

        Vector3f rot = Vector3f.fromNetwork(pk.getPlayerRotation());
        player.setRotation(rot.getY(), rot.getX());
        player.setHeadYaw(rot.getZ());
    }
}