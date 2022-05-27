package cn.nukkit.dialog.response;

import cn.nukkit.Server;
import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.network.protocol.NPCRequestPacket;
import lombok.Getter;

@Getter
public class FormResponseDialog {

    private long entityRuntimeId;
    private String commandString;
    private ElementDialogButton clickedButton;
    private String sceneName;

    public FormResponseDialog(NPCRequestPacket packet, FormWindowDialog dialog) {
        this.entityRuntimeId = packet.getRequestedEntityRuntimeId();
        this.commandString = packet.getCommandString();
        this.clickedButton = dialog.getButtons().get(packet.getActionType());
        this.sceneName = packet.getSceneName();
    }
}
