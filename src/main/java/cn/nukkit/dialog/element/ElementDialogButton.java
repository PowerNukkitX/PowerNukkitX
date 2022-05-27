package cn.nukkit.dialog.element;

import java.util.ArrayList;
import java.util.List;

public class ElementDialogButton {

    private String button_name; // json 格式需要，勿改

    private String text;

    private List<CmdLine> data;

    public static class CmdLine{
        public CmdLine(String cmd_line, int cmd_ver){
            this.cmd_line = cmd_line;
            this.cmd_ver = cmd_ver;
        }
        public String cmd_line;
        public int cmd_ver;
        public static transient final int CMD_VER = 19;
    }

    private int mode;

    private int type;

    public ElementDialogButton(String name, String text) {
        this(name, text, Mode.BUTTON_MODE);
    }

    public ElementDialogButton(String name, String text, Mode mode) {
        this(name, text, mode, 1);
    }

    public ElementDialogButton(String name, String text, Mode mode, int type) {
        this.button_name = name;
        this.text = text;
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

    public String getName() {
        return button_name;
    }

    public void setName(String name) {
        this.button_name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<CmdLine> getData() {
        //data will not be updated by the client
        //so we should update data with text content whenever we need it
        data = updateButtonData();
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

    public void setMode(Mode mode) {
        this.mode = mode.ordinal();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public enum Mode {
        BUTTON_MODE,
        ON_EXIT,
        ON_ENTER
    }
}
