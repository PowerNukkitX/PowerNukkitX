package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class ModalFormResponseProcessor extends DataPacketProcessor<ModalFormResponsePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ModalFormResponsePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        if (playerHandle.getFormWindows().containsKey(pk.formId)) {
            FormWindow window = playerHandle.getFormWindows().remove(pk.formId);
            window.setResponse(pk.data.trim());

            for (FormResponseHandler handler : window.getHandlers()) {
                handler.handle(player, pk.formId);
            }

            PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, pk.formId, window);
            player.getServer().getPluginManager().callEvent(event);
        } else if (playerHandle.getServerSettings().containsKey(pk.formId)) {
            FormWindow window = playerHandle.getServerSettings().get(pk.formId);
            window.setResponse(pk.data.trim());

            for (FormResponseHandler handler : window.getHandlers()) {
                handler.handle(player, pk.formId);
            }

            PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(player, pk.formId, window);
            player.getServer().getPluginManager().callEvent(event);

            //Set back new settings if not been cancelled
            if (!event.isCancelled() && window instanceof FormWindowCustom formWindowCustom)
                formWindowCustom.setElementsFromResponse();
        }

    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }
}
