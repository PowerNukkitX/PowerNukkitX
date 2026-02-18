package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.passive.EntityNpc;
import cn.nukkit.event.player.PlayerDialogRespondedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.NpcRequestType;
import org.cloudburstmc.protocol.bedrock.packet.NpcDialoguePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;
import org.jetbrains.annotations.NotNull;

public class NPCRequestProcessor extends DataPacketProcessor<NpcRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull NpcRequestPacket pk) {
        Player player = playerHandle.player;
        //若sceneName字段为空，则为玩家在编辑NPC，我们并不需要记录对话框，直接通过entityRuntimeId获取实体即可
        if (pk.getSceneName().isEmpty() && player.level.getEntity(pk.getRuntimeEntityId()) instanceof EntityNpc npcEntity) {
            FormWindowDialog dialog = npcEntity.getDialog();

            FormResponseDialog response = new FormResponseDialog(pk, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);
            return;
        }
        if (playerHandle.getDialogWindows().getIfPresent(pk.getSceneName()) != null) {
            //remove the window from the map only if the requestType is EXECUTE_CLOSING_COMMANDS
            FormWindowDialog dialog;
            if (pk.getRequestType() == NpcRequestType.EXECUTE_CLOSING_COMMANDS) {
                dialog = playerHandle.getDialogWindows().getIfPresent(pk.getSceneName());
                playerHandle.getDialogWindows().invalidate(pk.getSceneName());
            } else {
                dialog = playerHandle.getDialogWindows().getIfPresent(pk.getSceneName());
            }

            FormResponseDialog response = new FormResponseDialog(pk, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);

            //close dialog after clicked button (otherwise the client will not be able to close the window)
            if (response.getClickedButton() != null && pk.getRequestType() == NpcRequestType.EXECUTE_COMMAND_ACTION) {
                NpcDialoguePacket closeWindowPacket = new NpcDialoguePacket();
                closeWindowPacket.setUniqueEntityId(pk.getRuntimeEntityId());
                closeWindowPacket.setSceneName(response.getSceneName());
                closeWindowPacket.setAction(NpcDialoguePacket.Action.CLOSE);
                player.dataPacket(closeWindowPacket);
            }
            if (response.getClickedButton() != null && response.getRequestType() == NpcRequestType.EXECUTE_COMMAND_ACTION && response.getClickedButton().getNextDialog() != null) {
                response.getClickedButton().getNextDialog().send(player);
            }
        }
    }
    @Override
    public Class<NpcRequestPacket> getPacketClass() {
        return NpcRequestPacket.class;
    }
}
