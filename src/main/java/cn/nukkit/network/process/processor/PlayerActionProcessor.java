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
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Slf4j
public class PlayerActionProcessor extends DataPacketProcessor<PlayerActionPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerActionPacket pk) {
        Player player = playerHandle.player;
        PlayerActionType action = pk.getAction();
        if (!player.spawned || (!player.isAlive() && action != PlayerActionType.RESPAWN && action != PlayerActionType.DIMENSION_CHANGE_SUCCESS)) {
            return;
        }

        pk.setRuntimeEntityId(player.getId());
        var blockPosition = pk.getBlockPosition();
        Vector3 pos = new Vector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        BlockFace face = BlockFace.fromIndex(pk.getFace());

        switch (action) {
            case START_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                playerHandle.onBlockBreakStart(pos, face);
            }
            case ABORT_BREAK, STOP_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                playerHandle.onBlockBreakAbort(pos);
            }
            case DIMENSION_CHANGE_REQUEST_OR_CREATIVE_DESTROY_BLOCK -> {
                Block blockLectern = playerHandle.player.getLevel().getBlock(pos);
                if (blockLectern instanceof BlockLectern blockLecternI && blockLectern.distance(playerHandle.player) <= 6) {
                    blockLecternI.dropBook(playerHandle.player);
                }
                if (blockLectern instanceof BlockFrame blockFrame && blockFrame.getBlockEntity() != null) {
                    blockFrame.getBlockEntity().dropItem(playerHandle.player);
                }
                if (player.getServer().getServerAuthoritativeMovement() > 0) {
                    break;
                }
                playerHandle.onBlockBreakComplete(new BlockVector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()), face);
            }
            case CONTINUE_BREAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                playerHandle.onBlockBreakContinue(pos, face);
            }
            case STOP_SLEEP -> player.stopSleep();
            case RESPAWN -> {
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    return;
                }
                playerHandle.respawn();
            }
            case JUMP -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                player.getServer().getPluginManager().callEvent(playerJumpEvent);
            }
            case START_SPRINT -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(true);
                }
            }
            case STOP_SPRINT -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(false);
                }
            }
            case START_SNEAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(true);
                }
            }
            case STOP_SNEAK -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(false);
                }
            }
            case DIMENSION_CHANGE_SUCCESS -> player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.Mode.NORMAL);
            case START_GLIDE -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleGlideEvent event = new PlayerToggleGlideEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(true);
                }
            }
            case STOP_GLIDE -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleGlideEvent event = new PlayerToggleGlideEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(false);
                }
            }
            case START_SWIMMING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSwimEvent event = new PlayerToggleSwimEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(true);
                }
            }
            case STOP_SWIMMING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleSwimEvent event = new PlayerToggleSwimEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSwimming(false);
                }
            }
            case START_SPIN_ATTACK -> {
                if (!Objects.equals(player.getInventory().getItemInHand().getId(), ItemID.TRIDENT)) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.Mode.TELEPORT);
                    break;
                }
                int riptideLevel = player.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                if (riptideLevel < 1) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.Mode.TELEPORT);
                    break;
                }
                if (!(player.isTouchingWater() || (player.getLevel().isRaining() && player.getLevel().canBlockSeeSky(player)))) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.Mode.TELEPORT);
                    break;
                }
                PlayerToggleSpinAttackEvent event = new PlayerToggleSpinAttackEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendPosition(player, player.yaw, player.pitch, MovePlayerPacket.Mode.TELEPORT);
                } else {
                    player.setSpinAttacking(true);
                    Sound riptideSound = riptideLevel >= 3 ? Sound.ITEM_TRIDENT_RIPTIDE_3 : (riptideLevel == 2 ? Sound.ITEM_TRIDENT_RIPTIDE_2 : Sound.ITEM_TRIDENT_RIPTIDE_1);
                    player.level.addSound(player, riptideSound);
                }
            }
            case STOP_SPIN_ATTACK -> {
                PlayerToggleSpinAttackEvent event = new PlayerToggleSpinAttackEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSpinAttacking(false);
                }
            }
            case START_FLYING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                if (!player.getAllowFlight()) {
                    PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
                    Server.getInstance().getPluginManager().callEvent(detectedEvent);
                    if (detectedEvent.isKick()) {
                        player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                    }
                    break;
                }
                PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(player, true);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.getAdventureSettings().update();
                } else {
                    player.getAdventureSettings().set(AdventureSettings.Type.FLYING, event.isFlying());
                }
            }
            case STOP_FLYING -> {
                if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
                    return;
                }
                PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(player, false);
                player.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    player.getAdventureSettings().update();
                } else {
                    player.getAdventureSettings().set(AdventureSettings.Type.FLYING, event.isFlying());
                }
            }
            default -> {
            }
        }
    }

    @Override
    public Class<PlayerActionPacket> getPacketClass() {
        return PlayerActionPacket.class;
    }
}
