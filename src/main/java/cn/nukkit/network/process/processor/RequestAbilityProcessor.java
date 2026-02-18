package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.packet.RequestAbilityPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class RequestAbilityProcessor extends DataPacketProcessor<RequestAbilityPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestAbilityPacket pk) {
        Player player = playerHandle.player;
        Ability ability = pk.getAbility();
        if (ability != Ability.FLYING) {
            log.info("[{}] has tried to trigger {} ability {}", player.getName(), ability, pk.isBoolValue() ? "on" : "off");
            return;
        }

        if (!pk.isBoolValue() && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
            PlayerHackDetectedEvent detectedEvent = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.FLIGHT);
            Server.getInstance().getPluginManager().callEvent(detectedEvent);
            if(detectedEvent.isKick())
                player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
            return;
        }

        PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, pk.isBoolValue());
        player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
        if (playerToggleFlightEvent.isCancelled()) {
            player.getAdventureSettings().update();
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
        }
    }

    @Override
    public Class<RequestAbilityPacket> getPacketClass() {
        return RequestAbilityPacket.class;
    }
}
