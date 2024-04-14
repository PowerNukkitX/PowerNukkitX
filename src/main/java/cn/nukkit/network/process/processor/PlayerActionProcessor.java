package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFrame;
import cn.nukkit.block.BlockLectern;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
            case PlayerActionPacket.ACTION_START_BREAK -> playerHandle.onBlockBreakStart(pos, face);
            case PlayerActionPacket.ACTION_ABORT_BREAK, PlayerActionPacket.ACTION_STOP_BREAK ->
                    playerHandle.onBlockBreakAbort(pos);
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
            case PlayerActionPacket.ACTION_CONTINUE_BREAK -> playerHandle.onBlockBreakContinue(pos, face);
            case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_DROP_ITEM -> {
                //TODO
            }
            case PlayerActionPacket.ACTION_STOP_SLEEPING -> player.stopSleep();
            case PlayerActionPacket.ACTION_RESPAWN -> {
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    return;
                }
                playerHandle.respawn();
            }
            case PlayerActionPacket.ACTION_JUMP -> {
                PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                player.getServer().getPluginManager().callEvent(playerJumpEvent);
            }
            case PlayerActionPacket.ACTION_START_SPRINT -> {
                PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(true);
                }
            }
            case PlayerActionPacket.ACTION_STOP_SPRINT -> {
                var playerToggleSprintEvent = new PlayerToggleSprintEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(false);
                }
            }
            case PlayerActionPacket.ACTION_START_SNEAK -> {
                PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(true);
                }
            }
            case PlayerActionPacket.ACTION_STOP_SNEAK -> {
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
                PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(true);
                }
            }
            case PlayerActionPacket.ACTION_STOP_GLIDE -> {
                var playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(false);
                }
            }
            case PlayerActionPacket.ACTION_START_SWIMMING -> {
                PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(player, true);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(true);
                }
            }
            case PlayerActionPacket.ACTION_STOP_SWIMMING -> {
                var ptse = new PlayerToggleSwimEvent(player, false);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
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
            case PlayerActionPacket.ACTION_START_FLYING -> {
                if (!player.getServer().getAllowFlight() && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
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
                var playerToggleFlightEvent = new PlayerToggleFlightEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
                if (playerToggleFlightEvent.isCancelled()) {
                    player.getAdventureSettings().update();
                } else {
                    player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
                }
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.PLAYER_ACTION_PACKET;
    }
}
