package cn.nukkit.form.window;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Forms return an integer corresponding to the index of the button the player pressed. If the player closed the form (with the cross at the top right, or by hitting escape for example), null is returned.
 */
public class FormWindowSimple extends FormWindow {

    @SuppressWarnings("unused")
    private final String $1 = "form"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private String $3 = "";
    private List<ElementButton> buttons;

    private FormResponseSimple $4 = null;
    /**
     * @deprecated 
     */
    

    public FormWindowSimple(String title, String content) {
        this(title, content, new ArrayList<>());
    }
    /**
     * @deprecated 
     */
    

    public FormWindowSimple(String title, String content, List<ElementButton> buttons) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
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

    public List<ElementButton> getButtons() {
        return buttons;
    }
    /**
     * @deprecated 
     */
    

    public void addButton(ElementButton button) {
        this.buttons.add(button);
    }

    @Override
    public FormResponseSimple getResponse() {
        return response;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setResponse(String data) {
        if (data.equals("null")) {
            this.closed = true;
            return;
        }
        int buttonID;
        try {
            buttonID = Integer.parseInt(data);
        } catch (Exception e) {
            return;
        }
        if (buttonID >= this.buttons.size()) {
            this.response = new FormResponseSimple(buttonID, null);
            return;
        }
        this.response = new FormResponseSimple(buttonID, buttons.get(buttonID));
    }

}
