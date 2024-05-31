package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TextContainer deathMessage;
    private boolean $2 = false;
    private boolean $3 = false;
    private int experience;
    /**
     * @deprecated 
     */
    

    public PlayerDeathEvent(Player player, Item[] drops, TextContainer deathMessage, int experience) {
        super(player, drops);
        this.deathMessage = deathMessage;
        this.experience = experience;
    }
    /**
     * @deprecated 
     */
    

    public PlayerDeathEvent(Player player, Item[] drops, String deathMessage, int experience) {
        this(player, drops, new TextContainer(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public TextContainer getDeathMessage() {
        return deathMessage;
    }

    public TranslationContainer getTranslationDeathMessage() {
        return this.deathMessage instanceof TranslationContainer ?
                (TranslationContainer) this.deathMessage :
                new TranslationContainer(this.deathMessage.getText());
    }
    /**
     * @deprecated 
     */
    

    public void setDeathMessage(TranslationContainer deathMessage) {
        this.deathMessage = deathMessage;
    }
    /**
     * @deprecated 
     */
    

    public void setDeathMessage(TextContainer deathMessage) {
        this.deathMessage = deathMessage;
    }
    /**
     * @deprecated 
     */
    

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = new TextContainer(deathMessage);
    }
    /**
     * @deprecated 
     */
    

    public boolean getKeepInventory() {
        return keepInventory;
    }
    /**
     * @deprecated 
     */
    

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }
    /**
     * @deprecated 
     */
    

    public boolean getKeepExperience() {
        return keepExperience;
    }
    /**
     * @deprecated 
     */
    

    public void setKeepExperience(boolean keepExperience) {
        this.keepExperience = keepExperience;
    }
    /**
     * @deprecated 
     */
    

    public int getExperience() {
        return experience;
    }
    /**
     * @deprecated 
     */
    

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
