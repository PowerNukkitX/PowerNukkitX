package cn.nukkit.network.process.processor;

import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.AuthInputAction;
import cn.nukkit.network.protocol.types.PlayerActionType;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.player.AdventureSettings;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class PlayerAuthInputProcessor extends DataPacketProcessor<PlayerAuthInputPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerAuthInputPacket pk) {
        Player player = playerHandle.player;
        if (!player.locallyInitialized) return;
        if (!pk.getBlockActionData().isEmpty()) {
            for (PlayerBlockActionData action : pk.getBlockActionData().values()) {
                // hack 自从1.19.70开始，创造模式剑客户端不会发送PREDICT_DESTROY_BLOCK，但仍然发送START_DESTROY_BLOCK，过滤掉
                if (player.getInventory().getItemInHand().isSword()
                        && player.isCreative()
                        && action.getAction() == PlayerActionType.START_DESTROY_BLOCK) {
                    continue;
                }

                BlockVector3 blockPos = action.getPosition();
                BlockFace blockFace = BlockFace.fromIndex(action.getFacing());
                if (playerHandle.getLastBlockAction() != null
                        && playerHandle.getLastBlockAction().getAction() == PlayerActionType.PREDICT_DESTROY_BLOCK
                        && action.getAction() == PlayerActionType.CONTINUE_DESTROY_BLOCK) {
                    playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                }

                BlockVector3 lastBreakPos = playerHandle.getLastBlockAction() == null
                        ? null
                        : playerHandle.getLastBlockAction().getPosition();
                if (lastBreakPos != null
                        && (lastBreakPos.getX() != blockPos.getX()
                                || lastBreakPos.getY() != blockPos.getY()
                                || lastBreakPos.getZ() != blockPos.getZ())) {
                    playerHandle.onBlockBreakAbort(lastBreakPos.asVector3(), BlockFace.DOWN);
                    playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                }

                switch (action.getAction()) {
                    case START_DESTROY_BLOCK:
                        playerHandle.onBlockBreakStart(blockPos.asVector3(), blockFace);
                        break;
                    case ABORT_DESTROY_BLOCK:
                    case STOP_DESTROY_BLOCK:
                        playerHandle.onBlockBreakAbort(blockPos.asVector3(), blockFace);
                        break;
                    case CONTINUE_DESTROY_BLOCK: // 破坏完一个方块后接着破坏下一个方块
                        break;
                    case PREDICT_DESTROY_BLOCK:
                        if (player.isBreakingBlock()) {
                            playerHandle.onBlockBreakAbort(blockPos.asVector3(), blockFace);
                            playerHandle.onBlockBreakComplete(blockPos, blockFace);
                        } else {
                            playerHandle.onBlockBreakAbort(blockPos.asVector3(), blockFace);
                        }
                        break;
                }
                playerHandle.setLastBlockAction(action);
            }
        }

        if (pk.getInputData().contains(AuthInputAction.START_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, true);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(true);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.STOP_SPRINTING)) {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, false);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSprinting(false);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.START_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, true);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(true);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.STOP_SNEAKING)) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, false);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSneaking(false);
            }
        }
        if (player.getAdventureSettings().get(AdventureSettings.Type.FLYING)) {
            if (pk.getInputData().contains(AuthInputAction.SNEAKING)) {
                player.setFlySneaking(true);
            } else player.setFlySneaking(false);
        }
        if (pk.getInputData().contains(AuthInputAction.START_JUMPING)) {
            PlayerJumpEvent event = new PlayerJumpEvent(player);
            event.call();
        }
        if (pk.getInputData().contains(AuthInputAction.START_SWIMMING)) {
            PlayerToggleSwimEvent event = new PlayerToggleSwimEvent(player, true);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(true);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.STOP_SWIMMING)) {
            PlayerToggleSwimEvent event = new PlayerToggleSwimEvent(player, false);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setSwimming(false);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.START_GLIDING)) {
            PlayerToggleGlideEvent event = new PlayerToggleGlideEvent(player, true);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(true);
            }
        }
        if (pk.getInputData().contains(AuthInputAction.STOP_GLIDING)) {
            PlayerToggleGlideEvent event = new PlayerToggleGlideEvent(player, false);
            event.call();
            if (event.isCancelled()) {
                player.sendData(player);
            } else {
                player.setGliding(false);
            }
        }
        Vector3 clientPosition = pk.getPosition().asVector3().subtract(0, playerHandle.getBaseOffset(), 0);
        float yaw = pk.getYaw() % 360;
        float pitch = pk.getPitch() % 360;
        float headYaw = pk.getHeadYaw() % 360;
        if (headYaw < 0) {
            headYaw += 360;
        }
        if (yaw < 0) {
            yaw += 360;
        }
        Location clientLoc = Location.fromObject(clientPosition, player.getLevel(), yaw, pitch, headYaw);
        // Proper player.isPassenger() check may be needed
        if (playerHandle.player.riding instanceof EntityMinecartAbstract entityMinecartAbstract) {
            entityMinecartAbstract.setCurrentSpeed(pk.getMotion().y());
        } else if (playerHandle.player.riding instanceof EntityHorse entityHorse) {
            // 为了保证玩家和马位置同步，骑马时不使用移动队列处理
            var distance = clientLoc.distanceSquared(player);
            var updatePosition = (float) Math.sqrt(distance) > 0.1f;
            var updateRotation = (float) Math.abs(player.pitch() - clientLoc.pitch()) > 1
                    || (float) Math.abs(player.yaw() - clientLoc.yaw()) > 1
                    || (float) Math.abs(player.headYaw() - clientLoc.headYaw()) > 1;
            if (updatePosition || updateRotation) {
                entityHorse.onPlayerInput(clientLoc);
                playerHandle.handleMovement(clientLoc);
            }
            return;
        }
        playerHandle.offerMovementTask(clientLoc);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_AUTH_INPUT_PACKET);
    }
}
