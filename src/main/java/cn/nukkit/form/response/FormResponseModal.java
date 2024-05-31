package cn.nukkit.form.response;

public class FormResponseModal extends FormResponse {

    private final int clickedButtonId;
    private final String clickedButtonText;
    /**
     * @deprecated 
     */
    

    public FormResponseModal(int clickedButtonId, String clickedButtonText) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButtonText = clickedButtonText;
    }
    /**
     * @deprecated 
     */
    

    public int getClickedButtonId() {
        return clickedButtonId;
    }
    /**
     * @deprecated 
     */
    

    public String getClickedButtonText() {
        return clickedButtonText;
    }

}
