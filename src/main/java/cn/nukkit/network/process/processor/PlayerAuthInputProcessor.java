package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityHorse;
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
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.jetbrains.annotations.NotNull;

public class PlayerAuthInputProcessor extends DataPacketProcessor<PlayerAuthInputPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerAuthInputPacket pk) {
        Player player = playerHandle.player;
        if (!pk.getPlayerActions().isEmpty()) {
            for (PlayerBlockActionData action : pk.getPlayerActions()) {
                //hack Since version 1.19.70, the Creative Mode Sword client no longer sends PREDITIC_DESTROY_BLOCK, but still sends START_DESTROY_BLOCK, filtering out
                if (player.getInventory().getItemInHand().isSword() && player.isCreative() && action.getAction() == PlayerActionType.START_BREAK) {
                    continue;
                }
                var netPos = action.getBlockPosition();
                if (netPos == null) {
                    continue;
                }
                BlockVector3 blockPos = new BlockVector3(netPos.getX(), netPos.getY(), netPos.getZ());
                BlockFace blockFace = BlockFace.fromIndex(action.getFace());

                BlockVector3 lastBreakPos = playerHandle.getLastBlockAction() == null || playerHandle.getLastBlockAction().getBlockPosition() == null
                        ? null
                        : new BlockVector3(
                        playerHandle.getLastBlockAction().getBlockPosition().getX(),
                        playerHandle.getLastBlockAction().getBlockPosition().getY(),
                        playerHandle.getLastBlockAction().getBlockPosition().getZ());
                if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() || lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                    playerHandle.onBlockBreakAbort(lastBreakPos.asVector3());
                    playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                }

                switch (action.getAction()) {
                    case START_BREAK, BLOCK_CONTINUE_DESTROY, CONTINUE_BREAK -> playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                    case ABORT_BREAK, STOP_BREAK ->
                            playerHandle.onBlockBreakAbort(blockPos.asVector3());
                    case BLOCK_PREDICT_DESTROY -> {
                        playerHandle.onBlockBreakAbort(blockPos.asVector3());
                        playerHandle.onBlockBreakComplete(blockPos, blockFace);
                    }
                }
                playerHandle.setLastBlockAction(action);
            }
        }

        // As of 1.18 this is now used for sending item stack requests such as when mining a block.
        if (pk.getItemStackRequest() != null) {
            DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
            if (dataPacketManager != null) {
                ItemStackRequestPacket itemStackRequestPacket = new ItemStackRequestPacket();
                itemStackRequestPacket.getRequests().add(pk.getItemStackRequest());
                dataPacketManager.processPacket(playerHandle, itemStackRequestPacket);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.START_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(true);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, false);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(false);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.START_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(true);
                player.setBlocking(true);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_SNEAKING)) {
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
            player.setFlySneaking(pk.getInputData().contains(PlayerAuthInputData.SNEAKING));
        }
        if (pk.getInputData().contains(PlayerAuthInputData.START_JUMPING)) {
            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
            player.getServer().getPluginManager().callEvent(playerJumpEvent);
        }
        if (pk.getInputData().contains(PlayerAuthInputData.START_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(true);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(false);
            }
        }

        if (pk.getInputData().contains(PlayerAuthInputData.START_CRAWLING)) {
            var playerToggleCrawlEvent = new PlayerToggleCrawlEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleCrawlEvent);
            if (playerToggleCrawlEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setCrawling(true);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_CRAWLING)) {
            var playerToggleCrawlEvent = new PlayerToggleCrawlEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleCrawlEvent);
            if (playerToggleCrawlEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setCrawling(false);
            }
        }

        if (pk.getInputData().contains(PlayerAuthInputData.START_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(true);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(false);
            }
        }
        if (pk.getInputData().contains(PlayerAuthInputData.START_FLYING)) {
            if (!player.getAllowFlight()) {
                PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
                Server.getInstance().getPluginManager().callEvent(detectedEvent);
                if(detectedEvent.isKick())
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
        if (pk.getInputData().contains(PlayerAuthInputData.STOP_FLYING)) {
            PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
            if (playerToggleFlightEvent.isCancelled()) {
                player.getAdventureSettings().update();
            } else {
                player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
            }
        }
        if(pk.getInputData().contains(PlayerAuthInputData.JUMP_RELEASED_RAW)) {
            if(player.getRiding() != null) {
                if (playerHandle.player.riding instanceof EntityHorse horse && horse.isAlive() && !horse.isJumping()) {
                    horse.getJumping().set(player.getLevel().getTick());
                    horse.setDataFlag(EntityFlag.STANDING);
                }
            }
        }
        
        Vector3f rotation = pk.getRotation();
        Vector3 clientPosition = new Vector3(pk.getPosition().getX(), pk.getPosition().getY(), pk.getPosition().getZ()).subtract(0, playerHandle.getBaseOffset(), 0);
        float pitch = rotation.getX() % 360;
        float yaw = rotation.getY() % 360;
        float headYaw = rotation.getZ() % 360;
        if (headYaw < 0) {
            headYaw += 360;
        }
        if (yaw < 0) {
            yaw += 360;
        }
        Location clientLoc = Location.fromObject(clientPosition, player.level, yaw, pitch, headYaw);

        Entity vehicle = null;
        if((vehicle = player.getRiding()) != null && (vehicle.getDataFlag(EntityFlag.WASD_CONTROLLED) || vehicle.isRiderControl())) {
          if(!check(clientLoc, player)) return; 
          if(vehicle.onRiderInput(player, pk)) return;
        }

        playerHandle.offerMovementTask(clientLoc);
    }

    private static boolean check(Location clientLoc, Player player) {
        var distance = clientLoc.distanceSquared(player);
        var updatePosition = (float) Math.sqrt(distance) > 0.1f;
        var updateRotation = (float) Math.abs(player.getPitch() - clientLoc.pitch) > 1
                || (float) Math.abs(player.getYaw() - clientLoc.yaw) > 1
                || (float) Math.abs(player.getHeadYaw() - clientLoc.headYaw) > 1;
        return updatePosition || updateRotation;
    }
    @Override
    public Class<PlayerAuthInputPacket> getPacketClass() {
        return PlayerAuthInputPacket.class;
    }
}
