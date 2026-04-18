package cn.nukkit.network.process.handler;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityPhysical;
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
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PacketHandlerRegistry;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;

/**
 * @author Kaooot
 */
public class PlayerAuthInputHandler implements PacketHandler<PlayerAuthInputPacket> {

    @Override
    public void handle(PlayerAuthInputPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();

        if (!packet.getPlayerActions().isEmpty()) {
            for (PlayerBlockActionData action : packet.getPlayerActions()) {
                //hack Since version 1.19.70, the Creative Mode Sword client no longer sends PREDITIC_DESTROY_BLOCK, but still sends START_DESTROY_BLOCK, filtering out
                if (player.getInventory().getItemInMainHand().isSword() && player.isCreative() && action.getAction() == PlayerActionType.START_DESTROY_BLOCK) {
                    continue;
                }
                Vector3i blockPos = action.getBlockPosition();
                BlockFace blockFace = BlockFace.fromIndex(action.getFace());

                Vector3i lastBreakPos = player.getLastBlockAction() == null ? null : player.getLastBlockAction().getBlockPosition();
                if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() || lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                    player.onBlockBreakAbort(Vector3.fromNetwork(lastBreakPos.toFloat()));
                    player.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                }

                switch (action.getAction()) {
                    case START_DESTROY_BLOCK, CONTINUE_DESTROY_BLOCK ->
                            player.onBlockBreakStart(Vector3.fromNetwork(blockPos.toFloat()), blockFace);
                    case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK ->
                            player.onBlockBreakAbort(Vector3.fromNetwork(blockPos.toFloat()));
                    case PREDICT_DESTROY_BLOCK -> {
                        player.onBlockBreakAbort(Vector3.fromNetwork(blockPos.toFloat()));
                        player.onBlockBreakComplete(BlockVector3.fromNetwork(blockPos), blockFace);
                    }
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
        if ((vehicle = player.getRiding()) != null && (vehicle.hasWASDControls())) {
            if (!check(clientLoc, player)) return;
            if (vehicle.onRiderInput(player, packet)) return;
        }

        player.offerMovementTask(clientLoc);
    }

    private static boolean check(Location clientLoc, Player player) {
        var distance = clientLoc.distanceSquared(player);
        var updatePosition = (float) Math.sqrt(distance) > 0.1f;
        var updateRotation = (float) Math.abs(player.getPitch() - clientLoc.pitch) > 1
                || (float) Math.abs(player.getYaw() - clientLoc.yaw) > 1
                || (float) Math.abs(player.getHeadYaw() - clientLoc.headYaw) > 1;
        return updatePosition || updateRotation;
    }
}