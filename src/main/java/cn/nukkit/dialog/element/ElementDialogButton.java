package cn.nukkit.dialog.element;

import cn.nukkit.dialog.window.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a button element in a dialog window.
 * <p>
 * Each button has a name, display text, associated command lines, a mode, a type, and can optionally link to another dialog.
 * The button's data is dynamically generated from its text content.
 */
public class ElementDialogButton {
    /**
     * The unique name of the button (used in JSON serialization).
     */
    private String button_name;
    /**
     * The text displayed on the button.
     */
    private String text;
    /**
     * The list of command lines associated with this button.
     */
    private List<CmdLine> data;
    /**
     * The dialog to open when this button is pressed, or null if none.
     */
    protected transient Dialog nextDialog = null;

    /**
     * Represents a command line entry for a button.
     */
    public static class CmdLine{
        /**
         * The command line string.
         */
        public String cmd_line;
        /**
         * The command version.
         */
        public int cmd_ver;
        /**
         * The default command version constant.
         */
        public static transient final int CMD_VER = 19;
        /**
         * Constructs a new CmdLine.
         * @param cmd_line The command line string
         * @param cmd_ver The command version
         */
        public CmdLine(String cmd_line, int cmd_ver){
            this.cmd_line = cmd_line;
            this.cmd_ver = cmd_ver;
        }
    }
    /**
     * The button mode (see {@link Mode}).
     */
    private int mode;
    /**
     * The button type (custom usage).
     */
    private int type;
    /**
     * Constructs a button with a name and text.
     * @param name The button name
     * @param text The button text
     */
    public ElementDialogButton(String name, String text) {
        this(name, text, null);
    }
    /**
     * Constructs a button with a name, text, and next dialog.
     * @param name The button name
     * @param text The button text
     * @param nextDialog The dialog to open when pressed
     */
    public ElementDialogButton(String name, String text,Dialog nextDialog){
        this(name, text, nextDialog, Mode.BUTTON_MODE);
    }
    /**
     * Constructs a button with a name, text, next dialog, and mode.
     * @param name The button name
     * @param text The button text
     * @param nextDialog The dialog to open when pressed
     * @param mode The button mode
     */
    public ElementDialogButton(String name, String text,Dialog nextDialog, Mode mode) {
        this(name, text, nextDialog, mode, 1);
    }
    /**
     * Constructs a button with all properties.
     * @param name The button name
     * @param text The button text
     * @param nextDialog The dialog to open when pressed
     * @param mode The button mode
     * @param type The button type
     */
    public ElementDialogButton(String name, String text,Dialog nextDialog, Mode mode, int type) {
        this.button_name = name;
        this.text = text;
        this.nextDialog = nextDialog;
        this.data = updateButtonData();
        this.mode = mode.ordinal();
        this.type = type;
    }
    /**
     * Updates the button's command data based on its text content.
     * @return The list of command lines
     */
    public List<CmdLine> updateButtonData(){
        List<CmdLine> list = new ArrayList<>();
        String[] split = text.split("\n");
        for (String str : split) {
            list.add(new CmdLine(str,CmdLine.CMD_VER));
        }
        return list;
    }
    /**
     * Gets the button's name.
     * @return The button name
     */
    public String getName() {
        return button_name;
    }
    /**
     * Sets the button's name.
     * @param name The new name
     */
    public void setName(String name) {
        this.button_name = name;
    }
    /**
     * Gets the button's text.
     * @return The button text
     */
    public String getText() {
        return text;
    }
    /**
     * Sets the button's text.
     * @param text The new text
     */
    public void setText(String text) {
        this.text = text;
    }
    /**
     * Gets the list of command lines for this button (regenerated from text).
     * @return The list of command lines
     */
    public List<CmdLine> getData() {
        // data will not be updated by the client
        // so we should update data with text content whenever we need it
        data = updateButtonData();
        return data;
    }
    /**
     * Gets the button's mode.
     * @return The button mode
     */
    public Mode getMode() {
        return switch (mode) {
            case 0 -> Mode.BUTTON_MODE;
            case 1 -> Mode.ON_EXIT;
            case 2 -> Mode.ON_ENTER;
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }
    /**
     * Sets the button's mode.
     * @param mode The new mode
     */
    public void setMode(Mode mode) {
        this.mode = mode.ordinal();
    }
    /**
     * Gets the button's type.
     * @return The button type
     */
    public int getType() {
        return type;
    }
    /**
     * Sets the button's type.
     * @param type The new type
     */
    public void setType(int type) {
        this.type = type;
    }
    /**
     * Gets the next dialog to open when this button is pressed.
     * @return The next dialog, or null if none
     */
    public Dialog getNextDialog() {
        return nextDialog;
    }
    /**
     * Sets the next dialog to open when this button is pressed.
     * @param nextDialog The dialog to set
     */
    public void setNextDialog(Dialog nextDialog) {
        this.nextDialog = nextDialog;
    }
    /**
     * The mode of the button, determining its behavior in the dialog flow.
     */
    public enum Mode {
        /** Standard button mode. */
        BUTTON_MODE,
        /** Triggered when exiting the dialog. */
        ON_EXIT,
        /** Triggered when entering the dialog. */
        ON_ENTER
    }
}
