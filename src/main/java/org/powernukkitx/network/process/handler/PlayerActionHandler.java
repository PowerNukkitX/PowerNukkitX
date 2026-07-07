package org.powernukkitx.network.process.handler;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFrame;
import org.powernukkitx.block.BlockLectern;
import org.powernukkitx.event.player.PlayerHackDetectedEvent;
import org.powernukkitx.event.player.PlayerJumpEvent;
import org.powernukkitx.event.player.PlayerKickEvent;
import org.powernukkitx.event.player.PlayerToggleFlightEvent;
import org.powernukkitx.event.player.PlayerToggleGlideEvent;
import org.powernukkitx.event.player.PlayerToggleSneakEvent;
import org.powernukkitx.event.player.PlayerToggleSpinAttackEvent;
import org.powernukkitx.event.player.PlayerToggleSprintEvent;
import org.powernukkitx.event.player.PlayerToggleSwimEvent;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import java.util.Objects;

/**
 * @author Kaooot
 */
@Slf4j
public class PlayerActionHandler implements PacketHandler<PlayerActionPacket> {

    @Override
    public void handle(PlayerActionPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || (!player.isAlive() && packet.getAction() != PlayerActionType.RESPAWN && packet.getAction() != PlayerActionType.CHANGE_DIMENSION_ACK)) {
            return;
        }

        packet.setPlayerRuntimeID(player.getId());
        Vector3 pos = Vector3.fromNetwork(packet.getBlockPosition().toFloat());
        BlockFace face = BlockFace.fromIndex(packet.getFace());

        switch (packet.getAction()) {
            case PlayerActionType.START_DESTROY_BLOCK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakStart(pos, face);
            }
            case PlayerActionType.ABORT_DESTROY_BLOCK, PlayerActionType.STOP_DESTROY_BLOCK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakAbort(pos);
            }
            case PlayerActionType.CREATIVE_DESTROY_BLOCK -> {
                // Used by client to get book from lecterns and items from item frame in creative mode since 1.20.70
                Block blockLectern = playerHandle.player.getLevel().getBlock(pos);
                if (blockLectern instanceof BlockLectern blockLecternI && blockLectern.distance(playerHandle.player) <= 6) {
                    blockLecternI.dropBook(playerHandle.player);
                }
                if (blockLectern instanceof BlockFrame blockFrame && blockFrame.getBlockEntity() != null) {
                    blockFrame.getBlockEntity().dropItem(playerHandle.player);
                }
                if (player.getServer().getServerAuthoritativeMovement() > 0) break;//ServerAuthorInput not use player
                playerHandle.onBlockBreakComplete(BlockVector3.fromNetwork(packet.getBlockPosition()), face);
            }
            case PlayerActionType.CONTINUE_DESTROY_BLOCK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakContinue(pos, face);
            }
            case PlayerActionType.GET_UPDATED_BLOCK -> {
                //TODO
            }
            case PlayerActionType.DROP_ITEM -> {
                //TODO
            }
            case PlayerActionType.START_SLEEPING -> {

            }
            case PlayerActionType.STOP_SLEEPING -> player.stopSleep();
            case PlayerActionType.RESPAWN -> {
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    return;
                }
                playerHandle.respawn();
            }
            case PlayerActionType.START_JUMP -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                player.getServer().getPluginManager().callEvent(playerJumpEvent);
            }
            case PlayerActionType.START_SPRINTING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(true);
                }
            }
            case PlayerActionType.STOP_SPRINTING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                var playerToggleSprintEvent = new PlayerToggleSprintEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(false);
                }
            }
            case PlayerActionType.START_SNEAKING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(true);
                }
            }
            case PlayerActionType.STOP_SNEAKING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                var playerToggleSneakEvent = new PlayerToggleSneakEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(false);
                }
            }
            case PlayerActionType.CHANGE_DIMENSION_ACK ->
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.NORMAL);
            case PlayerActionType.START_GLIDING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(true);
                }
            }
            case PlayerActionType.STOP_GLIDING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(false);
                }
            }
            case PlayerActionType.DENY_DESTROY_BLOCK -> {
                //TODO
            }
            case PlayerActionType.START_SWIMMING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(player, true);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(true);
                }
            }
            case PlayerActionType.STOP_SWIMMING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                var ev = new PlayerToggleSwimEvent(player, false);
                player.getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(false);
                }
            }
            case PlayerActionType.START_SPIN_ATTACK -> {
                if (!Objects.equals(player.getInventory().getItemInMainHand().getId(), ItemID.TRIDENT)) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.RESPAWN);
                    break;
                }

                int riptideLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                if (riptideLevel < 1) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.RESPAWN);
                    break;
                }

                if (!(player.isTouchingWater() || (player.getLevel().isRaining() && player.getLevel().canBlockSeeSky(player)))) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.RESPAWN);
                    break;
                }

                PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

                if (playerToggleSpinAttackEvent.isCancelled()) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.RESPAWN);
                } else {
                    player.setSpinAttacking(true);

                    Sound riptideSound;
                    if (riptideLevel >= 3) {
                        riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_3;
                    } else if (riptideLevel == 2) {
                        riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_2;
                    } else {
                        riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_1;
                    }
                    player.level.addSound(player, riptideSound);
                }
            }
            case PlayerActionType.STOP_SPIN_ATTACK -> {
                var playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

                if (playerToggleSpinAttackEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSpinAttacking(false);
                }
            }
            case PlayerActionType.INTERACT_WITH_BLOCK -> {
                //TODO
            }
            case PlayerActionType.PREDICT_DESTROY_BLOCK -> {
                //TODO
            }
            case PlayerActionType.START_FLYING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                if (!player.getAllowFlight()) {
                    PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
                    Server.getInstance().getPluginManager().callEvent(detectedEvent);
                    if (detectedEvent.isKick())
                        player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                    break;
                }
                PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
                if (playerToggleFlightEvent.isCancelled()) {
                    player.getAdventureSettings().update();
                } else {
                    player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
                }
            }
            case PlayerActionType.STOP_FLYING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                var playerToggleFlightEvent = new PlayerToggleFlightEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
                if (playerToggleFlightEvent.isCancelled()) {
                    player.getAdventureSettings().update();
                } else {
                    player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
                }
            }
            case PlayerActionType.START_ITEM_USE_ON, PlayerActionType.STOP_ITEM_USE_ON -> {
                // TODO
            }
            default -> log.warn("{} sent invalid action id {}", player.getName(), packet.getAction());
        }
    }
}