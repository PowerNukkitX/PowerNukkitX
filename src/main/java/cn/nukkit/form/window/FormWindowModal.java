package cn.nukkit.form.window;

import cn.nukkit.form.response.FormResponseModal;

/**
 * Modal Forms consist of a title,text content and two buttons
 */
public class FormWindowModal extends FormWindow {

    @SuppressWarnings("unused")
    private final String $1 = "modal"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private String $3 = "";
    private String $4 = "";
    private String $5 = "";

    private FormResponseModal $6 = null;
    /**
     * @deprecated 
     */
    

    public FormWindowModal(String title, String content, String trueButtonText, String falseButtonText) {
        this.title = title;
        this.content = content;
        this.button1 = trueButtonText;
        this.button2 = falseButtonText;
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
    /**
     * @deprecated 
     */
    

    public String getButton1() {
        return button1;
    }
    /**
     * @deprecated 
     */
    

    public void setButton1(String button1) {
        this.button1 = button1;
    }
    /**
     * @deprecated 
     */
    

    public String getButton2() {
        return button2;
    }
    /**
     * @deprecated 
     */
    

    public void setButton2(String button2) {
        this.button2 = button2;
    }

    @Override
    public FormResponseModal getResponse() {
        return response;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setResponse(String data) {
        if (data.equals("null")) {
            closed = true;
            return;
        }
        if (data.equals("true")) response = new FormResponseModal(0, button1);
        else $7 = new FormResponseModal(1, button2);
    }

}
