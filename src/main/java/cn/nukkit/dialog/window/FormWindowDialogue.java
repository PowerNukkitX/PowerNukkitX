package cn.nukkit.dialog.window;

import cn.nukkit.Player;
import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.handler.FormDialogueHandler;
import cn.nukkit.dialog.response.FormResponseDialogue;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.passive.EntityNPCEntity;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.NPCDialoguePacket;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.List;

public class FormWindowDialogue {

    protected static final Gson GSON = new Gson();

    protected transient boolean closed = false;

    private String title = "";

    private String content = "";

    private String skinData = "{\"picker_offsets\":{\"scale\":[1.70,1.70,1.70],\"translate\":[0,20,0]},\"portrait_offsets\":{\"scale\":[1.750,1.750,1.750],\"translate\":[-7,50,0]},\"skin_list\":[{\"variant\":0},{\"variant\":1},{\"variant\":2},{\"variant\":3},{\"variant\":4},{\"variant\":5},{\"variant\":6},{\"variant\":7},{\"variant\":8},{\"variant\":9},{\"variant\":10},{\"variant\":11},{\"variant\":12},{\"variant\":13},{\"variant\":14},{\"variant\":15},{\"variant\":16},{\"variant\":17},{\"variant\":18},{\"variant\":19},{\"variant\":20},{\"variant\":21},{\"variant\":22},{\"variant\":23},{\"variant\":24},{\"variant\":25},{\"variant\":26},{\"variant\":27},{\"variant\":28},{\"variant\":29},{\"variant\":30},{\"variant\":31},{\"variant\":32},{\"variant\":33},{\"variant\":34}]}";

    private List<ElementDialogButton> buttons;

    private FormResponseDialogue response = null;

    private long entityId;

    private EntityNPCEntity entityNPCEntity;

    protected final transient List<FormDialogueHandler> handlers = new ObjectArrayList<>();

    public FormWindowDialogue(String title, String content) {
        this(title, content, new ArrayList<>());
    }

    public FormWindowDialogue(String title, String content, List<ElementDialogButton> buttons) {
        this(title, content, buttons, null);
    }

    public FormWindowDialogue(String title, String content, List<ElementDialogButton> buttons, EntityNPCEntity entityNPCEntity) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.entityNPCEntity = entityNPCEntity;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ElementDialogButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<ElementDialogButton> buttons) {
        this.buttons = buttons;
    }

    public void addButton(String text) {
        this.addButton(new ElementDialogButton(text, text));
    }

    public void addButton(ElementDialogButton button) {
        this.buttons.add(button);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public EntityNPCEntity getEntityNPCEntity() {
        return entityNPCEntity;
    }

    public void setEntityNPCEntity(EntityNPCEntity entityNPCEntity) {
        this.entityNPCEntity = entityNPCEntity;
    }

    public String getSkinData(){
        return this.skinData;
    }

    public void setSkinData(String data){
        this.skinData = data;
    }

    public void addHandler(FormDialogueHandler handler) {
        this.handlers.add(handler);
    }

    public List<FormDialogueHandler> getHandlers() {
        return handlers;
    }

    public void setResponse(String data) {
        if (data.equals("null")) {
            this.closed = true;
        } else {
            int buttonId;
            try {
                buttonId = Integer.parseInt(data);
            } catch (Exception e) {
                return;
            }
            if (buttonId >= this.buttons.size()) {
                this.response = new FormResponseDialogue(buttonId, null);
            } else {
                this.response = new FormResponseDialogue(buttonId, this.buttons.get(buttonId));
            }
        }
    }

    public FormResponseDialogue getResponse() {
        return this.response;
    }

    public String getJSONData() {
        return GSON.toJson(this.buttons);
    }

    public void sendToPlayer(Player player) {
        String actionJson = getJSONData();
        if (this.entityNPCEntity == null) {
            AddEntityPacket addEntityPacket = new AddEntityPacket();
            addEntityPacket.entityUniqueId = Entity.entityCount++;
            addEntityPacket.entityRuntimeId = addEntityPacket.entityUniqueId;
            addEntityPacket.type = EntityNPCEntity.NETWORK_ID;
            addEntityPacket.id = AddEntityPacket.LEGACY_IDS.get(EntityNPCEntity.NETWORK_ID);
            addEntityPacket.x = (float) player.getX();
            addEntityPacket.y = (float) player.getY();
            addEntityPacket.z = (float) player.getZ();
            addEntityPacket.yaw = (float) player.getYaw();
            addEntityPacket.pitch = (float) player.getPitch();
            addEntityPacket.headYaw = (float) player.getHeadYaw();
            addEntityPacket.metadata = new EntityMetadata()
                    .putString(Entity.DATA_NAMETAG, this.title)
                    .putByte(Entity.DATA_HAS_NPC_COMPONENT, 1)
                    .putString(Entity.DATA_NPC_SKIN_DATA, this.skinData) // TODO: NPC Skin
                    .putString(Entity.DATA_INTERACTIVE_TAG, actionJson)
                    .putString(Entity.DATA_INTERACTIVE_TAG, this.content);
            player.dataPacket(addEntityPacket);
            this.entityId = addEntityPacket.entityUniqueId;
        } else {
            this.entityNPCEntity.setNameTag(this.title);
            this.entityNPCEntity.getDataProperties().putByte(Entity.DATA_HAS_NPC_COMPONENT, 1);
            entityNPCEntity.getDataProperties().putString(Entity.DATA_NPC_SKIN_DATA, this.skinData); // TODO: NPC Skin
            this.entityNPCEntity.getDataProperties().putString(Entity.DATA_NPC_ACTIONS, actionJson);
            this.entityNPCEntity.getDataProperties().putString(Entity.DATA_INTERACTIVE_TAG, this.content);
            this.entityId = entityNPCEntity.getId();
            this.entityNPCEntity.sendData(player);
        }
        NPCDialoguePacket packet = new NPCDialoguePacket();
        packet.setRuntimeEntityId(this.entityId);
        packet.setAction(NPCDialoguePacket.NPCDialogAction.OPEN);
        packet.setDialogue(this.content);
        packet.setSceneName(this.title);
        packet.setNpcName(this.title);
        packet.setActionJson(actionJson);
        player.dataPacket(packet);
    }
}
