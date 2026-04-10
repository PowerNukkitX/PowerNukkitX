package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.custom.ElementDropdown;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.element.custom.ElementSlider;
import cn.nukkit.form.element.custom.ElementStepSlider;
import cn.nukkit.form.element.custom.ElementToggle;
import cn.nukkit.form.response.CustomResponse;
import cn.nukkit.form.response.ElementResponse;
import cn.nukkit.form.response.Response;
import cn.nukkit.form.window.CustomForm;
import cn.nukkit.form.window.Form;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;

/**
 * @author Kaooot
 */
@Slf4j
public class ModalFormResponseHandler implements PacketHandler<ModalFormResponsePacket> {

    @Override
    public void handle(ModalFormResponsePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (!playerHandle.packetRateLimiter.tryFormResponse()) {
            PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(
                    playerHandle.player, PlayerHackDetectedEvent.HackType.MODAL_SPAM);
            playerHandle.player.getServer().getPluginManager().callEvent(event);
            if (event.isKick()) {
                playerHandle.player.getSession().close("Exceeding modal spam rate-limit");
            }
            return;
        }

        if (packet.getJsonResponse().length() > 1024 || packet.getFormCancelReason().isEmpty()) {
            player.close("§cPacket handling error");
            return;
        }

        if (playerHandle.getFormWindows().containsKey(packet.getFormID())) {
            Form<?> window = playerHandle.getFormWindows().remove(packet.getFormID());

            Response response = window.respond(player, packet.getJsonResponse().trim(), packet.getFormCancelReason().get());

            PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, packet.getFormID(), window, response);
            player.getServer().getPluginManager().callEvent(event);
        } else if (playerHandle.getServerSettings().containsKey(packet.getFormID())) {
            Form<?> window = playerHandle.getServerSettings().get(packet.getFormID());

            Response response = window.respond(player, packet.getJsonResponse().trim(), packet.getFormCancelReason().get());

            PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(player, packet.getFormID(), window, response);
            player.getServer().getPluginManager().callEvent(event);

            // Apply responses as default settings
            if (!event.isCancelled() && window instanceof CustomForm customForm && response != null) {
                ((CustomResponse) response).getResponses().forEach((i, res) -> {
                    ElementCustom e = customForm.elements().get(i);
                    switch (e) {
                        case ElementDropdown dropdown -> dropdown.defaultOption(((ElementResponse) res)
                                .elementId());
                        case ElementInput input -> input.defaultText(String.valueOf(res));
                        case ElementSlider slider -> slider.defaultValue((Float) res);
                        case ElementToggle toggle -> toggle.defaultValue((Boolean) res);
                        case ElementStepSlider stepSlider -> stepSlider.defaultStep(((ElementResponse) res)
                                .elementId());
                        default -> log.warn("Illegal element {} within ServerSettings", e);
                    }
                });
            }
        } else log.warn("{} sent unknown form id {}", player.getName(), packet.getFormID());
    }
}