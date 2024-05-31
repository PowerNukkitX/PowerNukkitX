package cn.nukkit.form.response;

public class FormResponseData {

    private final int elementID;
    private final String elementContent;
    /**
     * @deprecated 
     */
    

    public FormResponseData(int id, String content) {
        this.elementID = id;
        this.elementContent = content;
    }
    /**
     * @deprecated 
     */
    

    public int getElementID() {
        return elementID;
    }
    /**
     * @deprecated 
     */
    

    public String getElementContent() {
        return elementContent;
    }

}
