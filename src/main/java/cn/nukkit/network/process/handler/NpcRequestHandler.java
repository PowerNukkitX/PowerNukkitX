package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.passive.EntityNpc;
import cn.nukkit.event.player.PlayerDialogRespondedEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.NpcDialoguePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;

/**
 * @author Kaooot
 */
public class NpcRequestHandler implements PacketHandler<NpcRequestPacket> {

    @Override
    public void handle(NpcRequestPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (packet.getSceneName().isEmpty() && player.level.getEntity(packet.getNpcRuntimeID()) instanceof EntityNpc npcEntity) {
            FormWindowDialog dialog = npcEntity.getDialog();

            FormResponseDialog response = new FormResponseDialog(packet, dialog);
            for (FormDialogHandler handler : dialog.getHandlers()) {
                handler.handle(player, response);
            }

            PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(player, dialog, response);
            player.getServer().getPluginManager().callEvent(event);
            return;
        }
        if (playerHandle.getDialogWindows().getIfPresent(packet.getSceneName()) != null) {
            //remove the window from the map only if the requestType is EXECUTE_CLOSING_COMMANDS
            FormWindowDialog dialog;
            if (packet.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
                dialog = playerHandle.getDialogWindows().getIfPresent(packet.getSceneName());
                playerHandle.getDialogWindows().invalidate(packet.getSceneName());
            } else {
                dialog = playerHandle.getDialogWindows().getIfPresent(packet.getSceneName());
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