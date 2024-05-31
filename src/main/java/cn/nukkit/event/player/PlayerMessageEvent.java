package cn.nukkit.event.player;

/**
 * @author xtypr
 * @since 2015/12/23
 */
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected String message;
    /**
     * @deprecated 
     */
    

    public String getMessage() {
        return this.message;
    }
    /**
     * @deprecated 
     */
    

    public void setMessage(String message) {
        this.message = message;
    }

}
