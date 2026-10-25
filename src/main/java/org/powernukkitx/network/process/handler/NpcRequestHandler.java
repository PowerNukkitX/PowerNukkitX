package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.dialog.handler.FormDialogHandler;
import org.powernukkitx.dialog.response.FormResponseDialog;
import org.powernukkitx.dialog.window.FormWindowDialog;
import org.powernukkitx.entity.passive.EntityNpc;
import org.powernukkitx.event.player.PlayerDialogRespondedEvent;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.NpcDialoguePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;

/**
 * @author Kaooot
 */
public class NpcRequestHandler implements PacketHandler<NpcRequestPacket> {

    @Override
    public void handle(NpcRequestPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (playerHandle == null || playerHandle.player == null) {
            return;
        }
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        String sceneName = packet.getSceneName();
        if (sceneName != null && sceneName.length() > 256) {
            return;
        }

        if (sceneName.isEmpty() && player.level.getEntity(packet.getNpcRuntimeID()) instanceof EntityNpc npcEntity) {
            if (!player.canInteract(npcEntity, 12)) {
                return;
            }
            FormWindowDialog dialog = npcEntity.getDialog();
            if (dialog == null) {
                return;
            }

            FormResponseDialog response = new FormResponseDialog(packet, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);
            return;
        }
        FormWindowDialog dialog = playerHandle.getDialogWindows().getIfPresent(sceneName);
        if (dialog != null) {
            //remove the window from the map only if the requestType is EXECUTE_CLOSING_COMMANDS
            if (packet.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
                playerHandle.getDialogWindows().invalidate(sceneName);
            }

            FormResponseDialog response = new FormResponseDialog(packet, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);

            //close dialog after clicked button (otherwise the client will not be able to close the window)
            if (response.getClickedButton() != null && packet.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_ACTION) {
                final NpcDialoguePacket npcDialoguePacket = new NpcDialoguePacket();
                npcDialoguePacket.setNpcId(packet.getNpcRuntimeID());
                npcDialoguePacket.setSceneName(response.getSceneName());
                npcDialoguePacket.setActionType(NpcDialoguePacket.Action.CLOSE);
                player.sendPacket(npcDialoguePacket);
            }
            if (response.getClickedButton() != null && response.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_ACTION && response.getClickedButton().getNextDialog() != null) {
                response.getClickedButton().getNextDialog().send(player);
            }
        }
    }
}