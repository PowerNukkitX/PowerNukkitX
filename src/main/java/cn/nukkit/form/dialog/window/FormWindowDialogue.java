package cn.nukkit.form.dialog.window;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.passive.EntityNPCEntity;
import cn.nukkit.form.dialog.element.ElementDialogButton;
import cn.nukkit.form.dialog.handler.FormDialogueHandler;
import cn.nukkit.form.dialog.response.FormResponseDialogue;
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

    public boolean wasClosed() {
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
//        String actionJson = "[{\"button_name\":\"TestButton\",\"text\":\"Test123\",\"data\":[{\"cmd_line\":\"Test123\",\"cmd_ver\":17}],\"mode\":0,\"type\":1}]";
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
//                    .putString(Entity.DATA_NPC_SKIN_DATA, "")
                    .putString(Entity.DATA_INTERACTIVE_TAG, actionJson)
                    .putString(Entity.DATA_INTERACTIVE_TAG, this.content);
            player.dataPacket(addEntityPacket);
            this.entityId = addEntityPacket.entityUniqueId;
        } else {
            this.entityNPCEntity.setNameTag(this.title);
            this.entityNPCEntity.getDataProperties().putByte(Entity.DATA_HAS_NPC_COMPONENT, 1);
//            entityNPCEntity.getDataProperties().putString(Entity.DATA_NPC_SKIN_DATA, "");
            this.entityNPCEntity.getDataProperties().putString(Entity.DATA_NPC_ACTIONS, actionJson);
            this.entityNPCEntity.getDataProperties().putString(Entity.DATA_INTERACTIVE_TAG, this.content);
            this.entityId = entityNPCEntity.getId();
            this.entityNPCEntity.despawnFrom(player);
            this.entityNPCEntity.spawnTo(player);
        }
        System.out.println(actionJson); // TODO: remove
        NPCDialoguePacket packet = new NPCDialoguePacket();
        packet.setRuntimeEntityId(this.entityId);
        packet.setAction(NPCDialoguePacket.NPCDialogAction.OPEN);
        packet.setDialogue(this.content);
        packet.setSceneName(this.title);
        packet.setNpcName(this.title);
        packet.setActionJson(actionJson);
        System.out.println(GSON.toJson(packet)); // TODO: removestop
        player.dataPacket(packet);
    }
}
