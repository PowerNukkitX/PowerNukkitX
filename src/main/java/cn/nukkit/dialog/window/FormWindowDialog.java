package cn.nukkit.dialog.window;

import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityNPCEntity;
import cn.nukkit.network.protocol.NPCRequestPacket;
import com.dfsek.terra.lib.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.List;

public class FormWindowDialog {

    protected static final Gson GSON = new Gson();

    private String title = "";

    private String content = "";

    private String skinData = "{\"picker_offsets\":{\"scale\":[1.70,1.70,1.70],\"translate\":[0,20,0]},\"portrait_offsets\":{\"scale\":[1.750,1.750,1.750],\"translate\":[-7,50,0]},\"skin_list\":[{\"variant\":0},{\"variant\":1},{\"variant\":2},{\"variant\":3},{\"variant\":4},{\"variant\":5},{\"variant\":6},{\"variant\":7},{\"variant\":8},{\"variant\":9},{\"variant\":10},{\"variant\":11},{\"variant\":12},{\"variant\":13},{\"variant\":14},{\"variant\":15},{\"variant\":16},{\"variant\":17},{\"variant\":18},{\"variant\":19},{\"variant\":20},{\"variant\":21},{\"variant\":22},{\"variant\":23},{\"variant\":24},{\"variant\":25},{\"variant\":26},{\"variant\":27},{\"variant\":28},{\"variant\":29},{\"variant\":30},{\"variant\":31},{\"variant\":32},{\"variant\":33},{\"variant\":34}]}";

    private List<ElementDialogButton> buttons;

    private FormResponseDialog response = null;

    private long entityId;

    private Entity bindEntity;

    protected final transient List<FormDialogHandler> handlers = new ObjectArrayList<>();

    protected transient boolean closeWhenClicked = true;

    public FormWindowDialog(String title, String content) {
        this(title, content, new ArrayList<>());
    }

    public FormWindowDialog(String title, String content, List<ElementDialogButton> buttons) {
        this(title, content, buttons, null);
    }

    public FormWindowDialog(String title, String content, List<ElementDialogButton> buttons, EntityNPCEntity bindEntity) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.bindEntity = bindEntity;
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

    public Entity getBindEntity() {
        return bindEntity;
    }

    public void setBindEntity(EntityNPCEntity bindEntity) {
        this.bindEntity = bindEntity;
    }

    public String getSkinData(){
        return this.skinData;
    }

    public void setSkinData(String data){
        this.skinData = data;
    }

    public void addHandler(FormDialogHandler handler) {
        this.handlers.add(handler);
    }

    public List<FormDialogHandler> getHandlers() {
        return handlers;
    }

    public void setResponse(NPCRequestPacket packet) {
        this.response = new FormResponseDialog(packet,this);
    }

    public FormResponseDialog getResponse() {
        return this.response;
    }

    public String getButtonJSONData() {
        return GSON.toJson(this.buttons);
    }

    public void setButtonJSONData(String json){
        this.setButtons(GSON.fromJson(json, new TypeToken<List<ElementDialogButton>>(){}.getType()));
    }

    public void setCloseWhenClicked(boolean closeWhenClicked){
        this.closeWhenClicked = closeWhenClicked;
    }

    public boolean closeWhenClicked(){
        return this.closeWhenClicked;
    }
}
