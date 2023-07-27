package cn.nukkit.network.process.processor;

import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import cn.nukkit.event.player.*;
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

public class PlayerActionProcessor extends DataPacketProcessor<PlayerActionPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerActionPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned
                || (!player.isAlive()
                        && pk.action != PlayerActionPacket.ACTION_RESPAWN
                        && pk.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK)) {
            return;
        }

        pk.entityId = player.getId();
        Vector3 pos = new Vector3(pk.x, pk.y, pk.z);
        BlockFace face = BlockFace.fromIndex(pk.face);

        switch (pk.action) {
            case PlayerActionPacket.ACTION_START_BREAK:
                playerHandle.onBlockBreakStart(pos, face);
                break;
            case PlayerActionPacket.ACTION_ABORT_BREAK:
            case PlayerActionPacket.ACTION_STOP_BREAK:
                playerHandle.onBlockBreakAbort(pos, face);
                break;
            case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                break; // TODO
            case PlayerActionPacket.ACTION_DROP_ITEM:
                break; // TODO
            case PlayerActionPacket.ACTION_STOP_SLEEPING:
                player.stopSleep();
                break;
            case PlayerActionPacket.ACTION_RESPAWN:
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    break;
                }

                playerHandle.respawn();
                break;
            case PlayerActionPacket.ACTION_JUMP:
                PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                player.getServer().getPluginManager().callEvent(playerJumpEvent);
                return;
            case PlayerActionPacket.ACTION_START_SPRINT:
                PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_SPRINT:
                playerToggleSprintEvent = new PlayerToggleSprintEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(false);
                }
                return;
            case PlayerActionPacket.ACTION_START_SNEAK:
                PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_SNEAK:
                playerToggleSneakEvent = new PlayerToggleSneakEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(false);
                }
                return;
            case PlayerActionPacket.ACTION_CREATIVE_PLAYER_DESTROY_BLOCK:
                if (player.getServer().getServerAuthoritativeMovement() > 0) break; // ServerAuthorInput not use player
                playerHandle.onBlockBreakComplete(new BlockVector3(pk.x, pk.y, pk.z), face);
                break;
            case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_NORMAL);
                break; // TODO
            case PlayerActionPacket.ACTION_START_GLIDE:
                PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_GLIDE:
                playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(false);
                }
                return;
            case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                playerHandle.onBlockBreakContinue(pos, face);
                break;
            case PlayerActionPacket.ACTION_START_SWIMMING:
                PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(player, true);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(true);
                }
                break;
            case PlayerActionPacket.ACTION_STOP_SWIMMING:
                ptse = new PlayerToggleSwimEvent(player, false);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(false);
                }
                break;
            case PlayerActionPacket.ACTION_START_SPIN_ATTACK:
                if (player.getInventory().getItemInHand().getId() != ItemID.TRIDENT) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
                    break;
                }

                int riptideLevel =
                        player.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                if (riptideLevel < 1) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.MODE_RESET);
                    break;
                }

                if (!(player.isTouchingWater()
                        || (player.getLevel().isRaining() && player.getLevel().canBlockSeeSky(player)))) {
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
                return;
            case PlayerActionPacket.ACTION_STOP_SPIN_ATTACK:
                playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

                if (playerToggleSpinAttackEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSpinAttacking(false);
                }
                break;
        }

        player.setUsingItem(false);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_ACTION_PACKET);
    }
}
