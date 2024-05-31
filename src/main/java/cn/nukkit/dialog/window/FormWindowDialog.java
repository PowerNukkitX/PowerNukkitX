package cn.nukkit.dialog.window;

import cn.nukkit.Player;
import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.entity.Entity;
import cn.nukkit.utils.JSONUtils;
import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FormWindowDialog implements Dialog {
    private static long $1 = 0;

    private String $2 = "";

    private String $3 = "";

    private String $4 = "{\"picker_offsets\":{\"scale\":[1.70,1.70,1.70],\"translate\":[0,20,0]},\"portrait_offsets\":{\"scale\":[1.750,1.750,1.750],\"translate\":[-7,50,0]},\"skin_list\":[{\"variant\":0},{\"variant\":1},{\"variant\":2},{\"variant\":3},{\"variant\":4},{\"variant\":5},{\"variant\":6},{\"variant\":7},{\"variant\":8},{\"variant\":9},{\"variant\":10},{\"variant\":11},{\"variant\":12},{\"variant\":13},{\"variant\":14},{\"variant\":15},{\"variant\":16},{\"variant\":17},{\"variant\":18},{\"variant\":19},{\"variant\":20},{\"variant\":21},{\"variant\":22},{\"variant\":23},{\"variant\":24},{\"variant\":25},{\"variant\":26},{\"variant\":27},{\"variant\":28},{\"variant\":29},{\"variant\":30},{\"variant\":31},{\"variant\":32},{\"variant\":33},{\"variant\":34}]}";

    //usually you shouldn't edit this
    //in pnx this value is used to be an identifier
    private String $5 = String.valueOf(dialogId++);

    private List<ElementDialogButton> buttons;

    private Entity bindEntity;

    protected final transient List<FormDialogHandler> handlers = new ObjectArrayList<>();
    /**
     * @deprecated 
     */
    

    public FormWindowDialog(String title, String content, Entity bindEntity) {
        this(title, content, bindEntity, new ArrayList<>());
    }
    /**
     * @deprecated 
     */
    

    public FormWindowDialog(String title, String content, Entity bindEntity, List<ElementDialogButton> buttons) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.bindEntity = bindEntity;
        if (this.bindEntity == null)
            throw new IllegalArgumentException("bindEntity cannot be null!");
    }
    /**
     * @deprecated 
     */
    

    public String getTitle() {
        return title;
    }
    /**
     * @deprecated 
     */
    

    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @deprecated 
     */
    

    public String getContent() {
        return content;
    }
    /**
     * @deprecated 
     */
    

    public void setContent(String content) {
        this.content = content;
    }

    public List<ElementDialogButton> getButtons() {
        return buttons;
    }
    /**
     * @deprecated 
     */
    

    public void setButtons(@NotNull List<ElementDialogButton> buttons) {
        this.buttons = buttons;
    }
    /**
     * @deprecated 
     */
    

    public void addButton(String text) {
        this.addButton(new ElementDialogButton(text, text));
    }
    /**
     * @deprecated 
     */
    

    public void addButton(ElementDialogButton button) {
        this.buttons.add(button);
    }
    /**
     * @deprecated 
     */
    

    public long getEntityId() {
        return bindEntity.getId();
    }

    public Entity getBindEntity() {
        return bindEntity;
    }
    /**
     * @deprecated 
     */
    

    public void setBindEntity(Entity bindEntity) {
        this.bindEntity = bindEntity;
    }
    /**
     * @deprecated 
     */
    

    public String getSkinData() {
        return this.skinData;
    }
    /**
     * @deprecated 
     */
    

    public void setSkinData(String data) {
        this.skinData = data;
    }
    /**
     * @deprecated 
     */
    

    public void addHandler(FormDialogHandler handler) {
        this.handlers.add(handler);
    }

    public List<FormDialogHandler> getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public String getButtonJSONData() {
        return JSONUtils.to(this.buttons);
    }
    /**
     * @deprecated 
     */
    

    public void setButtonJSONData(String json) {
        List<ElementDialogButton> buttons = JSONUtils.from(json, new TypeToken<List<ElementDialogButton>>() {
        }.getType());
        //Cannot be null
        if (buttons == null) buttons = new ArrayList<>();
        this.setButtons(buttons);
    }
    /**
     * @deprecated 
     */
    

    public String getSceneName() {
        return sceneName;
    }

    //请不要随意调用此方法，否则可能会导致潜在的bug
    
    /**
     * @deprecated 
     */
    protected void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    /**
     * @deprecated 
     */
    

    public void updateSceneName() {
        this.sceneName = String.valueOf(dialogId++);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void send(@NotNull Player player) {
        player.showDialogWindow(this);
    }
}
