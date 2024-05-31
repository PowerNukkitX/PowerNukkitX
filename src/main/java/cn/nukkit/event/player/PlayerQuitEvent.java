package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.lang.TextContainer;

public class PlayerQuitEvent extends PlayerEvent {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected TextContainer quitMessage;
    protected boolean $2 = true;
    protected String reason;
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, TextContainer quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, TextContainer quitMessage) {
        this(player, quitMessage, true);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, String quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, String quitMessage) {
        this(player, quitMessage, true);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave, String reason) {
        this(player, new TextContainer(quitMessage), autoSave, reason);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave) {
        this(player, new TextContainer(quitMessage), autoSave);
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, TextContainer quitMessage, boolean autoSave) {
        this(player, quitMessage, autoSave, "No reason");
    }
    /**
     * @deprecated 
     */
    

    public PlayerQuitEvent(Player player, TextContainer quitMessage, boolean autoSave, String reason) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.autoSave = autoSave;
        this.reason = reason;
    }

    public TextContainer getQuitMessage() {
        return quitMessage;
    }
    /**
     * @deprecated 
     */
    

    public void setQuitMessage(TextContainer quitMessage) {
        this.quitMessage = quitMessage;
    }
    /**
     * @deprecated 
     */
    

    public void setQuitMessage(String quitMessage) {
        this.setQuitMessage(new TextContainer(quitMessage));
    }
    /**
     * @deprecated 
     */
    

    public boolean getAutoSave() {
        return this.autoSave;
    }
    /**
     * @deprecated 
     */
    

    public void setAutoSave() {
        this.setAutoSave(true);
    }
    /**
     * @deprecated 
     */
    

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    /**
     * @deprecated 
     */
    

    public String getReason() {
        return reason;
    }
}
