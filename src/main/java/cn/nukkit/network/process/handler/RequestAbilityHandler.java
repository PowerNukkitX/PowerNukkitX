package cn.nukkit.network.process.handler;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.packet.RequestAbilityPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class RequestAbilityHandler implements PacketHandler<RequestAbilityPacket> {

    @Override
    public void handle(RequestAbilityPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        AbilitiesIndex ability = packet.getAbility();
        if (ability != AbilitiesIndex.FLYING) {
            log.info("[{}] has tried to trigger {} ability {}", player.getName(), ability, packet.isBoolValue() ? "on" : "off");
            return;
        }

        if (!packet.isBoolValue() && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
            PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
            Server.getInstance().getPluginManager().callEvent(detectedEvent);
            if (detectedEvent.isKick())
                player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
            return;
        }

        PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, packet.isBoolValue());
        player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
        if (playerToggleFlightEvent.isCancelled()) {
            player.getAdventureSettings().update();
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
        }
    }
}