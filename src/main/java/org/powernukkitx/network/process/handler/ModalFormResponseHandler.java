package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerFormRespondedEvent;
import org.powernukkitx.event.player.PlayerHackDetectedEvent;
import org.powernukkitx.event.player.PlayerSettingsRespondedEvent;
import org.powernukkitx.form.element.custom.ElementCustom;
import org.powernukkitx.form.element.custom.ElementDropdown;
import org.powernukkitx.form.element.custom.ElementInput;
import org.powernukkitx.form.element.custom.ElementSlider;
import org.powernukkitx.form.element.custom.ElementStepSlider;
import org.powernukkitx.form.element.custom.ElementToggle;
import org.powernukkitx.form.response.CustomResponse;
import org.powernukkitx.form.response.ElementResponse;
import org.powernukkitx.form.response.Response;
import org.powernukkitx.form.window.CustomForm;
import org.powernukkitx.form.window.Form;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.ModalFormCancelReason;
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

        String jsonResponse = packet.getJsonResponse();
        ModalFormCancelReason cancelReason = packet.getFormCancelReason().orElse(null);

        if (jsonResponse != null && jsonResponse.length() > 1024) {
            player.close("§cPacket handling error");
            return;
        }

        String formData = jsonResponse == null ? "" : jsonResponse.trim();

        try {
            if (playerHandle.getFormWindows().containsKey(packet.getFormID())) {
                Form<?> window = playerHandle.getFormWindows().remove(packet.getFormID());

                Response response = window.respond(player, formData, cancelReason);

                PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, packet.getFormID(), window, response);
                player.getServer().getPluginManager().callEvent(event);
            } else if (playerHandle.getServerSettings().containsKey(packet.getFormID())) {
                Form<?> window = playerHandle.getServerSettings().get(packet.getFormID());

                Response response = window.respond(player, formData, cancelReason);

                PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(player, packet.getFormID(), window, response);
                player.getServer().getPluginManager().callEvent(event);

                // Apply responses as default settings
                if (!event.isCancelled() && window instanceof CustomForm customForm && response != null) {
                    ((CustomResponse) response).getResponses().forEach((i, res) -> {
                        ElementCustom e = customForm.elements().get(i);
                        if (e == null){
                            log.warn("{} sent unknown element index {} within ServerSettings", player.getName(), i);
                            return;
                        }
                        try {
                            switch (e) {
                                case ElementDropdown dropdown -> {
                                    int option = ((ElementResponse) res).elementId();
                                    if (option < 0 || option >= dropdown.options().size()) {
                                        log.warn("{} sent out-of-bounds dropdown option {} for element {}",
                                            player.getName(), option, i);
                                        return;
                                    }
                                    dropdown.defaultOption(option);
                                }
                                case ElementInput input -> input.defaultText(String.valueOf(res));
                                case ElementSlider slider -> {
                                    float value = (Float) res;
                                    if (value < slider.min() || value > slider.max()) {
                                        log.warn("{} sent out-of-bounds slider value {} for element {}", player.getName(), value, i);
                                        return;
                                    }
                                    slider.defaultValue(value);
                                }
                                case ElementToggle toggle -> toggle.defaultValue((Boolean) res);
                                case ElementStepSlider stepSlider -> {
                                    int step = ((ElementResponse) res).elementId();
                                    if (step < 0 || step >= stepSlider.steps().size()) {
                                        log.warn("{} sent out-of-bounds step index {} for element {}",
                                            player.getName(), step, i);
                                        return;
                                    }
                                    stepSlider.defaultStep(step);
                                }
                                default -> log.warn("Illegal element {} within ServerSettings", e);
                            }
                        } catch (ClassCastException ex){
                            log.warn("{} sent mismatched response type for element {} within ServerSettings", player.getName(), i, ex);
                        }
                    });
                }
            }
        } catch(Exception e){
            log.warn("{}: failed to process ModalFormResponsePacket for form {}", player.getName(), packet.getFormID(), e);
        }
    }
}
