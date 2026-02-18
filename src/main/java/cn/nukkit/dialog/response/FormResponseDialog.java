package cn.nukkit.dialog.response;

import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import org.cloudburstmc.protocol.bedrock.data.NpcRequestType;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;
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
    private NpcRequestType requestType;
    private int skinType;

    /**
     * Constructs a FormResponseDialog from an NPCRequestPacket and the dialog window.
     * @param packet The NPC request packet
     * @param dialog The dialog window
     */
    public FormResponseDialog(NpcRequestPacket packet, FormWindowDialog dialog) {
        this.entityRuntimeId = packet.getRuntimeEntityId();
        this.data = packet.getCommand();
        try {
            this.clickedButton = dialog.getButtons().get(packet.getActionType());
        }catch (IndexOutOfBoundsException e){
            this.clickedButton = null;
        }
        this.sceneName = packet.getSceneName();
        this.requestType = packet.getRequestType();
        this.skinType = packet.getActionType();
    }
}
