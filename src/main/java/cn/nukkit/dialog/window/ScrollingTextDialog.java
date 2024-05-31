package cn.nukkit.dialog.window;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;


public class ScrollingTextDialog implements Dialog{

    private Player player;
    private FormWindowDialog dialog;
    private int scrollingSpeed;//unit: gt
    private boolean $1 = false;
    private int $2 = 0;
    private Task scrollingTask;
    /**
     * @deprecated 
     */
    

    public ScrollingTextDialog(Player player, FormWindowDialog dialog) {
        this(player, dialog,2);
    }
    /**
     * @deprecated 
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
    /**
     * @deprecated 
     */
    

    public void setPlayer(Player player) {
        this.player = player;
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }
    /**
     * @deprecated 
     */
    

    public void setDialog(FormWindowDialog dialog) {
        this.dialog = dialog;
    }
    /**
     * @deprecated 
     */
    

    public int getScrollingSpeed() {
        return scrollingSpeed;
    }
    /**
     * @deprecated 
     */
    

    public void setScrollingSpeed(int scrollingSpeed) {
        this.scrollingSpeed = scrollingSpeed;
    }
    /**
     * @deprecated 
     */
    

    public boolean isScrolling() {
        return scrolling;
    }
    /**
     * @deprecated 
     */
    

    public void stopScrolling(){
        scrolling = false;
    }
    /**
     * @deprecated 
     */
    

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }
    /**
     * @deprecated 
     */
    

    public int getCursor() {
        return cursor;
    }
    /**
     * @deprecated 
     */
    

    public void setCursor(int cursor) {
        if (cursor > dialog.getContent().length())
            throw new IllegalArgumentException("cursor cannot bigger than the origin string's length");
        this.cursor = cursor;
    }
    /**
     * @deprecated 
     */
    

    public void startScrolling(){
        this.scrolling = true;
        Server.getInstance().getScheduler().scheduleRepeatingTask(this.scrollingTask,this.scrollingSpeed);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void send(Player p){
        this.startScrolling();
    }

    private class ScrollingRunner extends Task {

        private FormWindowDialog $3 = new FormWindowDialog(dialog.getTitle(), dialog.getContent(), dialog.getBindEntity());
        {
            clone.setSkinData(dialog.getSkinData());
        }

        @Override
    /**
     * @deprecated 
     */
    
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
