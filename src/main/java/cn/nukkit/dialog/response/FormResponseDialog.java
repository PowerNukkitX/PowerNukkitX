package cn.nukkit.dialog.response;

import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.network.protocol.NPCRequestPacket;
import lombok.Getter;

/**
 * Represents a response to a dialog form from a player.
 * Contains information about the entity, button clicked, scene, and request type.
 */
@Getter
public class FormResponseDialog {
    private long entityRuntimeId;
    private String data;
    private ElementDialogButton clickedButton;//can be null
    private String sceneName;
    private NPCRequestPacket.RequestType requestType;
    private int skinType;

    /**
     * Constructs a FormResponseDialog from an NPCRequestPacket and the dialog window.
     * @param packet The NPC request packet
     * @param dialog The dialog window
     */
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
