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
            case PlayerActionType.CREATIVE_DESTROY_BLOCK -> {
                // Used by client to get book from lecterns and items from item frame in creative mode since 1.20.70
                Block blockLectern = playerHandle.player.getLevel().getBlock(pos);
                if (blockLectern instanceof BlockLectern blockLecternI && blockLectern.distance(playerHandle.player) <= 6) {
                    blockLecternI.dropBook(playerHandle.player);
                }
                if (blockLectern instanceof BlockFrame blockFrame && blockFrame.getBlockEntity() != null) {
                    blockFrame.getBlockEntity().dropItem(playerHandle.player);
                }
            }
            case PlayerActionType.STOP_SLEEPING -> player.stopSleep();
            case PlayerActionType.RESPAWN -> {
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    return;
                }
                playerHandle.respawn();
            }
            case PlayerActionType.CHANGE_DIMENSION_ACK ->
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.PositionMode.NORMAL);
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
        }
    }
}
