package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFrame;
import cn.nukkit.block.BlockLectern;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerJumpEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.event.player.PlayerToggleGlideEvent;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.player.PlayerToggleSpinAttackEvent;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.event.player.PlayerToggleSwimEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Slf4j
public class PlayerActionProcessor extends DataPacketProcessor<PlayerActionPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerActionPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || (!player.isAlive() && pk.action != PlayerActionPacket.ACTION_RESPAWN && pk.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK)) {
            return;
        }

        pk.entityId = player.getId();
        Vector3 pos = new Vector3(pk.x, pk.y, pk.z);
        BlockFace face = BlockFace.fromIndex(pk.face);

        switch (pk.action) {
            case PlayerActionPacket.ACTION_START_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakStart(pos, face);
            }
            case PlayerActionPacket.ACTION_ABORT_BREAK, PlayerActionPacket.ACTION_STOP_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakAbort(pos);
            }
            case PlayerActionPacket.ACTION_CREATIVE_PLAYER_DESTROY_BLOCK -> {
                // Used by client to get book from lecterns and items from item frame in creative mode since 1.20.70
                Block blockLectern = playerHandle.player.getLevel().getBlock(pos);
                if (blockLectern instanceof BlockLectern blockLecternI && blockLectern.distance(playerHandle.player) <= 6) {
                    blockLecternI.dropBook(playerHandle.player);
                }
                if (blockLectern instanceof BlockFrame blockFrame && blockFrame.getBlockEntity() != null) {
                    blockFrame.getBlockEntity().dropItem(playerHandle.player);
                }
                if (player.getServer().getServerAuthoritativeMovement() > 0) break;//ServerAuthorInput not use player
                playerHandle.onBlockBreakComplete(new BlockVector3(pk.x, pk.y, pk.z), face);
            }
            case PlayerActionPacket.ACTION_CONTINUE_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                playerHandle.onBlockBreakContinue(pos, face);
            }
            case PlayerActionPacket.ACTION_SET_ENCHANTMENT_SEED -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_DROP_ITEM -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_START_SLEEPING -> {

            }
            case PlayerActionPacket.ACTION_STOP_SLEEPING -> player.stopSleep();
            case PlayerActionPacket.ACTION_RESPAWN -> {
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    return;
                }
                playerHandle.respawn();
            }
            case PlayerActionPacket.ACTION_JUMP -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                player.getServer().getPluginManager().callEvent(playerJumpEvent);
            }
            case PlayerActionPacket.ACTION_START_SPRINT -> {
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
            case PlayerActionPacket.ACTION_STOP_SPRINT -> {
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
            case PlayerActionPacket.ACTION_START_SNEAK -> {
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
            case PlayerActionPacket.ACTION_STOP_SNEAK -> {
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
            case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK ->
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_NORMAL);
            case PlayerActionPacket.ACTION_START_GLIDE -> {
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
            case PlayerActionPacket.ACTION_STOP_GLIDE -> {
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
            case PlayerActionPacket.ACTION_BUILD_DENIED -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_START_SWIMMING -> {
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
            case PlayerActionPacket.ACTION_STOP_SWIMMING -> {
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
            case PlayerActionPacket.ACTION_START_SPIN_ATTACK -> {
                if (!Objects.equals(player.getInventory().getItemInHand().getId(), ItemID.TRIDENT)) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
                    break;
                }

                int riptideLevel = player.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                if (riptideLevel < 1) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
                    break;
                }

                if (!(player.isTouchingWater() || (player.getLevel().isRaining() && player.getLevel().canBlockSeeSky(player)))) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
                    break;
                }

                PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

                if (playerToggleSpinAttackEvent.isCancelled()) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
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
            case PlayerActionPacket.ACTION_STOP_SPIN_ATTACK -> {
                var playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

                if (playerToggleSpinAttackEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSpinAttacking(false);
                }
            }
            case PlayerActionPacket.ACTION_INTERACT_BLOCK -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_PREDICT_DESTROY_BLOCK -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_CONTINUE_DESTROY_BLOCK -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_START_FLYING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }

                if (!player.getAllowFlight()) {
                    PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
                    Server.getInstance().getPluginManager().callEvent(detectedEvent);
                    if(detectedEvent.isKick())
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
            case PlayerActionPacket.ACTION_STOP_FLYING -> {
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
            case PlayerActionPacket.ACTION_RECEIVED_SERVER_DATA -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_START_ITEM_USE_ON, PlayerActionPacket.ACTION_STOP_ITEM_USE_ON -> {
                // TODO
            }
            default -> log.warn("{} sent invalid action id {}", player.getName(), pk.action);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.PLAYER_ACTION_PACKET;
    }
}