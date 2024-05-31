package cn.nukkit.dialog.element;

import cn.nukkit.dialog.window.Dialog;

import java.util.ArrayList;
import java.util.List;


public class ElementDialogButton {

    private String button_name; // json 格式需要，勿改

    private String text;

    private List<CmdLine> data;

    protected transient Dialog $1 = null;

    public static class CmdLine{
    /**
     * @deprecated 
     */
    
        public CmdLine(String cmd_line, int cmd_ver){
            this.cmd_line = cmd_line;
            this.cmd_ver = cmd_ver;
        }
        public String cmd_line;
        public int cmd_ver;
        public static transient final int $2 = 19;
    }

    private int mode;

    private int type;
    /**
     * @deprecated 
     */
    

    public ElementDialogButton(String name, String text) {
        this(name, text, null);
    }
    /**
     * @deprecated 
     */
    

    public ElementDialogButton(String name, String text,Dialog nextDialog){
        this(name, text, nextDialog, Mode.BUTTON_MODE);
    }
    /**
     * @deprecated 
     */
    

    public ElementDialogButton(String name, String text,Dialog nextDialog, Mode mode) {
        this(name, text, nextDialog, mode, 1);
    }
    /**
     * @deprecated 
     */
    

    public ElementDialogButton(String name, String text,Dialog nextDialog, Mode mode, int type) {
        this.button_name = name;
        this.text = text;
        this.nextDialog = nextDialog;
        this.data = updateButtonData();
        this.mode = mode.ordinal();
        this.type = type;
    }

    public List<CmdLine> updateButtonData(){
        List<CmdLine> list = new ArrayList<>();
        String[] split = text.split("\n");
        for (String str : split) {
            list.add(new CmdLine(str,CmdLine.CMD_VER));
        }
        return list;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return button_name;
    }
    /**
     * @deprecated 
     */
    

    public void setName(String name) {
        this.button_name = name;
    }
    /**
     * @deprecated 
     */
    

    public String getText() {
        return text;
    }
    /**
     * @deprecated 
     */
    

    public void setText(String text) {
        this.text = text;
    }

    public List<CmdLine> getData() {
        //data will not be updated by the client
        //so we should update $3 with text content whenever we need it
        $1 = updateButtonData();
        return data;
    }

    public Mode getMode() {
        return switch (mode) {
            case 0 -> Mode.BUTTON_MODE;
            case 1 -> Mode.ON_EXIT;
            case 2 -> Mode.ON_ENTER;
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
    /**
     * @deprecated 
     */
    

    public void setMode(Mode mode) {
        this.mode = mode.ordinal();
    }
    /**
     * @deprecated 
     */
    

    public int getType() {
        return type;
    }
    /**
     * @deprecated 
     */
    

    public void setType(int type) {
        this.type = type;
    }

    public Dialog getNextDialog() {
        return nextDialog;
    }
    /**
     * @deprecated 
     */
    

    public void setNextDialog(Dialog nextDialog) {
        this.nextDialog = nextDialog;
    }

    public enum Mode {
        BUTTON_MODE,
        ON_EXIT,
        ON_ENTER
    }
}
