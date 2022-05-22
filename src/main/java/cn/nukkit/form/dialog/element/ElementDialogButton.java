package cn.nukkit.form.dialog.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ElementDialogButton {

    private String button_name; // json 格式需要，勿改

    private String text;

    private Object data;

    private int mode;

    private int type;

    public ElementDialogButton(String name, String text) {
        this(name, text, null);
    }

    public ElementDialogButton(String name, String text, Object data) {
        this(name, text, data, 0);
    }

    public ElementDialogButton(String name, String text, Object data, int mode) {
        this(name, text, data, mode, 1);
    }

    public ElementDialogButton(String name, String text, Object data, int mode, int type) {
        this.button_name = name;
        this.text = text;
        if (data == null) {
            List<HashMap<String, Object>> list = new ArrayList<>();
            String[] split = text.split("\n");
            for (String str : split) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cmd_line", str);
                hashMap.put("cmd_ver", 17);
                list.add(hashMap);
            }
            this.data = list;
        } else {
            this.data = data;
        }
        this.mode = mode;
        this.type = type;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
