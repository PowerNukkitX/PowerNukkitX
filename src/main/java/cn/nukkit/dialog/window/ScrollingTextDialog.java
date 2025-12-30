package cn.nukkit.dialog.window;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

/**
 * Represents a dialog window that displays scrolling text to a player.
 * The text is revealed progressively at a configurable speed.
 */
public class ScrollingTextDialog implements Dialog {

    private Player player;
    private FormWindowDialog dialog;
    private int scrollingSpeed;//unit: gt
    private boolean scrolling = false;
    private int cursor = 0;
    private Task scrollingTask;

    /**
     * Constructs a ScrollingTextDialog for a player and dialog window with default speed.
     * @param player The player
     * @param dialog The dialog window
     */
    public ScrollingTextDialog(Player player, FormWindowDialog dialog) {
        this(player, dialog,2);
    }

    /**
     * Constructs a ScrollingTextDialog for a player, dialog window, and scrolling speed.
     * @param player The player
     * @param dialog The dialog window
     * @param scrollingSpeed The scrolling speed (in game ticks)
     */
    public ScrollingTextDialog(Player player, FormWindowDialog dialog, int scrollingSpeed) {
        this.player = player;
        this.dialog = dialog;
        scrollingTask = new ScrollingRunner();
        this.scrollingSpeed = scrollingSpeed;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }

    public void setDialog(FormWindowDialog dialog) {
        this.dialog = dialog;
    }

    public int getScrollingSpeed() {
        return scrollingSpeed;
    }

    public void setScrollingSpeed(int scrollingSpeed) {
        this.scrollingSpeed = scrollingSpeed;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void stopScrolling(){
        scrolling = false;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        if (cursor > dialog.getContent().length())
            throw new IllegalArgumentException("cursor cannot bigger than the origin string's length");
        this.cursor = cursor;
    }

    public void startScrolling(){
        this.scrolling = true;
        Server.getInstance().getScheduler().scheduleRepeatingTask(this.scrollingTask,this.scrollingSpeed);
    }

    /**
     * Starts the scrolling effect and sends the dialog to the player.
     * @param p The player
     */
    @Override
    public void send(Player p){
        this.startScrolling();
    }

    private class ScrollingRunner extends Task {

        private FormWindowDialog clone = new FormWindowDialog(dialog.getTitle(), dialog.getContent(), dialog.getBindEntity());
        {
            clone.setSkinData(dialog.getSkinData());
        }

        @Override
        public void onRun(int currentTick) {
            if (!scrolling || cursor >= dialog.getContent().length()) {
                cursor = 0;
                player.showDialogWindow(dialog);
                stopScrolling();
                this.cancel();
                return;
            }
            clone.setContent(dialog.getContent().substring(0,cursor));
            player.showDialogWindow(clone);
            if (dialog.getContent().length() - (cursor+1) >= 2 && dialog.getContent().charAt(cursor) == '§')
                cursor+=2;
            else
                cursor++;
        }
    }
}
