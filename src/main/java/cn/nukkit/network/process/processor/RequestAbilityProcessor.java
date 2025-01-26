package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.event.player.PlayerIllegalFlightEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestAbilityPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class RequestAbilityProcessor extends DataPacketProcessor<RequestAbilityPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestAbilityPacket pk) {
        Player player = playerHandle.player;
        PlayerAbility ability = pk.ability;
        if (ability != PlayerAbility.FLYING) {
            log.info("[" + player.getName() + "] has tried to trigger " + ability + " ability " + (pk.boolValue ? "on" : "off"));
            return;
        }

        if (!player.getServer().getAllowFlight() && pk.boolValue && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
            PlayerIllegalFlightEvent pife = new PlayerIllegalFlightEvent(player);
            player.getServer().getPluginManager().callEvent(pife);
            if (!pife.isKick())
                return;
            player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
            return;
        }

        PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, pk.boolValue);
        player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
        if (playerToggleFlightEvent.isCancelled()) {
            player.getAdventureSettings().update();
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.REQUEST_ABILITY_PACKET;
    }
}
