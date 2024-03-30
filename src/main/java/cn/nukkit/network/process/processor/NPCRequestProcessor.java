package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.passive.EntityNpc;
import cn.nukkit.event.player.PlayerDialogRespondedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.NPCDialoguePacket;
import cn.nukkit.network.protocol.NPCRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class NPCRequestProcessor extends DataPacketProcessor<NPCRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull NPCRequestPacket pk) {
        Player player = playerHandle.player;
        //若sceneName字段为空，则为玩家在编辑NPC，我们并不需要记录对话框，直接通过entityRuntimeId获取实体即可
        if (pk.sceneName.isEmpty() && player.level.getEntity(pk.entityRuntimeId) instanceof EntityNpc npcEntity) {
            FormWindowDialog dialog = npcEntity.getDialog();

            FormResponseDialog response = new FormResponseDialog(pk, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);
            return;
        }
        if (playerHandle.getDialogWindows().getIfPresent(pk.sceneName) != null) {
            //remove the window from the map only if the requestType is EXECUTE_CLOSING_COMMANDS
            FormWindowDialog dialog;
            if (pk.requestType == NPCRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
                dialog = playerHandle.getDialogWindows().getIfPresent(pk.sceneName);
                playerHandle.getDialogWindows().invalidate(pk.sceneName);
            } else {
                dialog = playerHandle.getDialogWindows().getIfPresent(pk.sceneName);
            }

            FormResponseDialog response = new FormResponseDialog(pk, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);

            //close dialog after clicked button (otherwise the client will not be able to close the window)
            if (response.getClickedButton() != null && pk.requestType == NPCRequestPacket.RequestType.EXECUTE_ACTION) {
                NPCDialoguePacket closeWindowPacket = new NPCDialoguePacket();
                closeWindowPacket.runtimeEntityId = pk.entityRuntimeId;
                closeWindowPacket.sceneName = response.getSceneName();
                closeWindowPacket.action = NPCDialoguePacket.NPCDialogAction.CLOSE;
                player.dataPacket(closeWindowPacket);
            }
            if (response.getClickedButton() != null && response.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_ACTION && response.getClickedButton().getNextDialog() != null) {
                response.getClickedButton().getNextDialog().send(player);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }
}
