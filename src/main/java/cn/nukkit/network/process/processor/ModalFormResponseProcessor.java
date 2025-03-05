package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.element.Element;
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
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ModalFormResponseProcessor extends DataPacketProcessor<ModalFormResponsePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ModalFormResponsePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        
        if(pk.data.length() > 1024) {
            player.close("§cPacket handling error");
            return;
        }
        
        if (playerHandle.getFormWindows().containsKey(pk.formId)) {
            Form<?> window = playerHandle.getFormWindows().remove(pk.formId);

            Response response = window.respond(player, pk.data.trim());

            PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, pk.formId, window, response);
            player.getServer().getPluginManager().callEvent(event);
        } else if (playerHandle.getServerSettings().containsKey(pk.formId)) {
            Form<?> window = playerHandle.getServerSettings().get(pk.formId);

            Response response = window.respond(player, pk.data.trim());

            PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(player, pk.formId, window, response);
            player.getServer().getPluginManager().callEvent(event);

            // Apply responses as default settings
            if (!event.isCancelled() && window instanceof CustomForm customForm && response != null) {
                ((CustomResponse) response).getResponses().forEach((i, res) -> {
                    Element e = customForm.elements().get(i);
                    switch (e) {
                        case ElementDropdown dropdown -> dropdown.defaultOption(((ElementResponse) res).elementId());
                        case ElementInput input -> input.defaultText(String.valueOf(res));
                        case ElementSlider slider -> slider.defaultValue((Float) res);
                        case ElementToggle toggle -> toggle.defaultValue((Boolean) res);
                        case ElementStepSlider stepSlider -> stepSlider.defaultStep(((ElementResponse) res).elementId());
                        default -> log.warn("Illegal element {} within ServerSettings", e);
                    }
                });
            }
        } else log.warn("{} sent unknown form id {}", player.getName(), pk.formId);

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }
}
