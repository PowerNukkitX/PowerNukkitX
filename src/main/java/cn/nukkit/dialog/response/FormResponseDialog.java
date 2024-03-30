package cn.nukkit.dialog.response;

import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.network.protocol.NPCRequestPacket;
import lombok.Getter;


@Getter
public class FormResponseDialog {
    private long entityRuntimeId;
    private String data;
    private ElementDialogButton clickedButton;//can be null
    private String sceneName;
    private NPCRequestPacket.RequestType requestType;
    private int skinType;

    public FormResponseDialog(NPCRequestPacket packet, FormWindowDialog dialog) {
        this.entityRuntimeId = packet.entityRuntimeId;
        this.data = packet.data;
        try {
            this.clickedButton = dialog.getButtons().get(packet.skinType);
        }catch (IndexOutOfBoundsException e){
            this.clickedButton = null;
        }
        this.sceneName = packet.sceneName;
        this.requestType = packet.requestType;
        this.skinType = packet.skinType;
    }
}
