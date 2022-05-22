package cn.nukkit.dialog.response;

import cn.nukkit.dialog.element.ElementDialogButton;

public class FormResponseDialogue {

    private final int clickedButtonId;

    private final ElementDialogButton clickedButton;

    public FormResponseDialogue(int clickedButtonId, ElementDialogButton clickedButton) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButton = clickedButton;
    }

    public int getClickedButtonId() {
        return this.clickedButtonId;
    }

    public ElementDialogButton getClickedButton() {
        return this.clickedButton;
    }
}
