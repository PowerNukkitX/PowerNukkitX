package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.event.player.PlayerJumpEvent;
import cn.nukkit.event.player.PlayerKickEvent;
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
import cn.nukkit.network.protocol.ItemStackRequestPacket;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.AuthInputAction;
import cn.nukkit.network.protocol.types.PlayerActionType;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import org.jetbrains.annotations.NotNull;

public class PlayerAuthInputProcessor extends DataPacketProcessor<PlayerAuthInputPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerAuthInputPacket pk) {
        Player player = playerHandle.player;
        if (!pk.blockActionData.isEmpty()) {
            for (PlayerBlockActionData action : pk.blockActionData.values()) {
                //hack 自从1.19.70开始，创造模式剑客户端不会发送PREDICT_DESTROY_BLOCK，但仍然发送START_DESTROY_BLOCK，过滤掉
                if (player.getInventory().getItemInHand().isSword() && player.isCreative() && action.getAction() == PlayerActionType.START_DESTROY_BLOCK) {
                    continue;
                }
                BlockVector3 blockPos = action.getPosition();
                BlockFace blockFace = BlockFace.fromIndex(action.getFacing());

                BlockVector3 lastBreakPos = playerHandle.getLastBlockAction() == null ? null : playerHandle.getLastBlockAction().getPosition();
                if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() || lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                    playerHandle.onBlockBreakAbort(lastBreakPos.asVector3());
                    playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                }

                switch (action.getAction()) {
                    case START_DESTROY_BLOCK,CONTINUE_DESTROY_BLOCK -> playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                    case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK ->
                            playerHandle.onBlockBreakAbort(blockPos.asVector3());
                    case PREDICT_DESTROY_BLOCK-> {
                        playerHandle.onBlockBreakAbort(blockPos.asVector3());
                        playerHandle.onBlockBreakComplete(blockPos, blockFace);
                    }
                }
                playerHandle.setLastBlockAction(action);
            }
        }

        // As of 1.18 this is now used for sending item stack requests such as when mining a block.
        if (pk.itemStackRequest != null) {
            DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
            if (dataPacketManager != null) {
                ItemStackRequestPacket itemStackRequestPacket = new ItemStackRequestPacket();
                itemStackRequestPacket.requests.add(pk.itemStackRequest);
                dataPacketManager.processPacket(playerHandle, itemStackRequestPacket);
            }
        }

        if (pk.inputData.contains(AuthInputAction.START_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(true);
            }
        }
        if (pk.inputData.contains(AuthInputAction.STOP_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, false);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(false);
            }
        }
        if (pk.inputData.contains(AuthInputAction.START_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, true);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(true);
            }
        }
        if (pk.inputData.contains(AuthInputAction.STOP_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, false);
            player.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(false);
            }
        }
        if (player.getAdventureSettings().get(AdventureSettings.Type.FLYING)) {
            if (pk.inputData.contains(AuthInputAction.SNEAKING)) {
                player.setFlySneaking(true);
            } else player.setFlySneaking(false);
        }
        if (pk.inputData.contains(AuthInputAction.START_JUMPING)) {
            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
            player.getServer().getPluginManager().callEvent(playerJumpEvent);
        }
        if (pk.inputData.contains(AuthInputAction.START_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(true);
            }
        }
        if (pk.inputData.contains(AuthInputAction.STOP_SWIMMING)) {
            var playerSwimmingEvent = new PlayerToggleSwimEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerSwimmingEvent);
            if (playerSwimmingEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(false);
            }
        }
        if (pk.inputData.contains(AuthInputAction.START_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(true);
            }
        }
        if (pk.inputData.contains(AuthInputAction.STOP_GLIDING)) {
            var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
            if (playerToggleGlideEvent.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(false);
            }
        }
        if (pk.inputData.contains(AuthInputAction.START_FLYING)) {
            if (!player.getServer().getAllowFlight() && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
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
        if (pk.inputData.contains(AuthInputAction.STOP_FLYING)) {
            PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, false);
            player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
            if (playerToggleFlightEvent.isCancelled()) {
                player.getAdventureSettings().update();
            } else {
                player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
            }
        }
        Vector3 clientPosition = pk.position.asVector3().subtract(0, playerHandle.getBaseOffset(), 0);
        float yaw = pk.yaw % 360;
        float pitch = pk.pitch % 360;
        float headYaw = pk.headYaw % 360;
        if (headYaw < 0) {
            headYaw += 360;
        }
        if (yaw < 0) {
            yaw += 360;
        }
        Location clientLoc = Location.fromObject(clientPosition, player.level, yaw, pitch, headYaw);
        // Proper player.isPassenger() check may be needed
        if (player.riding instanceof EntityMinecartAbstract entityMinecartAbstract) {
            double inputY = pk.motion.getY();
            if (inputY >= -1.001 && inputY <= 1.001) {
                entityMinecartAbstract.setCurrentSpeed(inputY);
            }
        } else if (player.riding instanceof EntityBoat boat && pk.inputData.contains(AuthInputAction.IN_CLIENT_PREDICTED_IN_VEHICLE)) {
            if (player.riding.getId() == pk.predictedVehicle && player.riding.isControlling(player)) {
                if (check(clientLoc, player)) {
                    Location offsetLoc = clientLoc.add(0, playerHandle.getBaseOffset(), 0);
                    boat.onInput(offsetLoc);
                    playerHandle.handleMovement(offsetLoc);
                }
                return;
            }
        } else if (playerHandle.player.riding instanceof EntityHorse entityHorse) {
            if (check(clientLoc, player)) {
                Location playerLoc;
                if (entityHorse.hasOwner() && !entityHorse.getSaddle().isNull()) {
                    entityHorse.onInput(clientLoc.add(0, entityHorse.getHeight(), 0));
                    playerLoc = clientLoc.add(0, playerHandle.getBaseOffset() + entityHorse.getHeight(), 0);
                } else {
                    playerLoc = clientLoc.add(0, 0.8, 0);
                }
                playerHandle.handleMovement(playerLoc);
                return;
            }
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
    public int getPacketId() {
        return ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;
    }
}
