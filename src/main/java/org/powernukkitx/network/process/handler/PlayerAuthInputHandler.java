package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;
import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityPhysical;
import org.powernukkitx.entity.item.EntityBoat;
import org.powernukkitx.event.player.*;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Location;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector2f;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PacketHandlerRegistry;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
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
        if (player == null) {
            return;
        }

        Vector3f pos = Vector3f.fromNetwork(packet.getPosition());
        Vector3f rot = Vector3f.fromNetwork(packet.getPlayerRotation());
        if (!Float.isFinite(pos.getX()) || !Float.isFinite(pos.getY()) || !Float.isFinite(pos.getZ()) || !Float.isFinite(rot.getX()) || !Float.isFinite(rot.getY()) || !Float.isFinite(rot.getZ())) {
            log.debug("Player {} sent invalid movement values (NaN or Infinite)", player.getName());
            return;
        }

        handleBlockActionsAndItemStackRequest(packet, holder, server, player);

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
        if (packet.getInputData().contains(PlayerAuthInputData.MISSED_SWING)) {
            player.getLevel().addLevelSoundEvent(player, SoundEvent.ATTACK_NO_DAMAGE, -1, "minecraft:player", false, false);
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
            boolean controllingRider = vehicle.getRider() == player;

            if (!controllingRider) {
                syncMountedPlayerRotationFromInput(player, packet);
            }

            syncVehiclePositionFromRiderInput(player, vehicle, packet);

            boolean handled = vehicle.onRiderInput(player, packet);

            vehicle.updatePassengers(false, false);

            if (controllingRider) {
                vehicle.broadcastMountedPassengerMovements();
            } else {
                player.broadcastMountedMovement();
            }

            if (handled) return;
        }

        player.offerMovementTask(clientLoc);
    }

    private static void handleBlockActionsAndItemStackRequest(PlayerAuthInputPacket packet, PlayerSessionHolder holder, Server server, Player player) {
        // the override toggle ensures that an external source implements server authoritative block breaking, defaults to false
        if (!packet.getPlayerBlockActions().isEmpty() && !server.getSettings().miscSettings().overrideServerAuthBlockBreaking()) {
            for (PlayerBlockActionData action : packet.getPlayerBlockActions()) {
                //hack Since version 1.19.70, the Creative Mode Sword client no longer sends PREDITIC_DESTROY_BLOCK, but still sends START_DESTROY_BLOCK, filtering out
                if (player.getInventory().getItemInMainHand().isSword() && player.isCreative() && action.getPlayerActionType() == PlayerActionType.START_DESTROY_BLOCK) {
                    // fire only sends START_DESTROY_BLOCK, so extinguish it before filtering the action out
                    Vector3 hitPos = Vector3.fromNetwork(action.getBlockPosition().toFloat());
                    BlockFace hitFace = BlockFace.fromIndex(action.getFacing());
                    Block target = player.getLevel().getBlock(hitPos);
                    Block fire = Player.getFireAt(target, hitFace);

                    if (fire != null) {
                        Item hand = player.getInventory().getItemInMainHand();
                        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, hand, fire, hitFace,
                            PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
                        server.getPluginManager().callEvent(interactEvent);
                        new PlayerHandle(player).setInteract();

                        if (interactEvent.isCancelled()) {
                            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{fire}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
                        } else {
                            player.extinguishFire(fire);
                        }
                    }
                    continue;
                }
                Vector3i blockPos = action.getBlockPosition();
                BlockFace blockFace = BlockFace.fromIndex(action.getFacing());
                PlayerHandle playerHandle = new PlayerHandle(player);
                if (playerHandle.getLastBlockAction() != null && playerHandle.getLastBlockAction().getPlayerActionType() == PlayerActionType.PREDICT_DESTROY_BLOCK &&
                        action.getPlayerActionType() == PlayerActionType.CONTINUE_DESTROY_BLOCK) {
                    playerHandle.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                }

                PlayerBlockActionData lastAction = playerHandle.getLastBlockAction();
                BlockVector3 lastBreakPos = lastAction == null ? null : BlockVector3.fromNetwork(lastAction.getBlockPosition());
                if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() || lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                    //When a block is broken instantaneous, the client sometimes just sends a START_DESTROY_BLOCK, but never completes or aborts it. On the client side, the block is also broken.
                    double breakTime = player.getLevel().getBlock(lastBreakPos.asVector3()).calculateBreakTime(player.getInventory().getItemInMainHand(), player);
                    boolean canCompleteBreak = Long.sum(player.lastBreak, (long) (breakTime * 1000)) <= System.currentTimeMillis() + 50;
                    if (canCompleteBreak && lastAction.getPlayerActionType() == PlayerActionType.START_DESTROY_BLOCK) {
                        player.onBlockBreakComplete(lastBreakPos, BlockFace.fromIndex(lastAction.getFacing()));
                    } else {
                        playerHandle.onBlockBreakAbort(lastBreakPos.asVector3());
                    }
                    player.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                }

                switch (action.getPlayerActionType()) {
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

    private static void syncMountedPlayerRotationFromInput(Player player, PlayerAuthInputPacket pk) {
        Vector3f rot = Vector3f.fromNetwork(pk.getPlayerRotation());

        if (!Float.isFinite(rot.x) || !Float.isFinite(rot.y) || !Float.isFinite(rot.z)) {
            log.debug("Player {} sent invalid rotation values (NaN or Infinite)", player.getName());
            return;
        }

        player.setRotation(rot.getY(), rot.getX());
        player.setHeadYaw(rot.getZ());
    }

    private static void syncVehiclePositionFromRiderInput(Player player, Entity vehicle, PlayerAuthInputPacket pk) {
        if (vehicle == null || !vehicle.isAlive()) return;

        Long clientPredictedVehicle = pk.getClientPredictedVehicle();
        if (clientPredictedVehicle == null || clientPredictedVehicle == 0L) return;
        if (clientPredictedVehicle.longValue() != vehicle.runtimeId()) return;

        Vector3f pos = Vector3f.fromNetwork(pk.getPosition());

        if (!Float.isFinite(pos.x) || !Float.isFinite(pos.y) || !Float.isFinite(pos.z)) {
            log.debug("Player {} sent invalid position values (NaN or Infinite)", player.getName());
            return;
        }

        Vector2f vehicleRotation = null;

        if (pk.getVehicleRotation() != null) {
            vehicleRotation = Vector2f.fromNetwork(pk.getVehicleRotation());

            if (!Float.isFinite(vehicleRotation.x) || !Float.isFinite(vehicleRotation.y)) {
                log.debug("Player {} sent invalid vehicle rotation values (NaN or Infinite)", player.getName());
                return;
            }
        }

        Vector3 packetPosition = pos.asVector3();
        Vector3 vehiclePosition = packetPosition;
        EntityBoat boat = vehicle instanceof EntityBoat entityBoat ? entityBoat : null;

        if (boat != null) {
            double boatY = packetPosition.y - boat.getBaseOffset();
            vehiclePosition = new Vector3(packetPosition.x, boatY, packetPosition.z);
        }

        double vehiclePitch;
        double vehicleYaw;

        if (vehicleRotation != null) {
            vehiclePitch = vehicleRotation.getX() % 360;
            vehicleYaw = vehicleRotation.getY() % 360;
        } else {
            vehiclePitch = pk.getPlayerRotation().getX() % 360;
            vehicleYaw = pk.getPlayerRotation().getY() % 360;
        }

        if (vehicleYaw < 0) vehicleYaw += 360;
        if (vehiclePitch < 0) vehiclePitch += 360;

        if (vehiclePosition.distanceSquared(vehicle) > 0.0001d) {
            vehicle.setPosition(vehiclePosition);
        }

        vehicle.setRotation(vehicleYaw, vehiclePitch);
        vehicle.setHeadYaw(vehicleYaw);
        vehicle.updateMovement();

        if (boat == null) {
            player.setPosition(packetPosition);
        }

        Vector3f rot = Vector3f.fromNetwork(pk.getPlayerRotation());
        player.setRotation(rot.getY(), rot.getX());
        player.setHeadYaw(rot.getZ());
    }
}
